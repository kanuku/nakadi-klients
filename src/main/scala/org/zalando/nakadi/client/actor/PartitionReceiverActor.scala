package org.zalando.nakadi.client.actor

import java.io.ByteArrayOutputStream
import java.net.URI

import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import com.fasterxml.jackson.databind.ObjectMapper
import org.zalando.nakadi.client
import org.zalando.nakadi.client.{Cursor, SimpleStreamEvent, ListenParameters}
import play.api.libs.iteratee.Iteratee
import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.ExecutionContext.Implicits.global

sealed case class Init()
case class NewListener(listener: ActorRef)
case class ConnectionOpened(topic: String, partition: String)
case class ConnectionClosed(topic: String, partition: String, lastCursor: Option[Cursor])


object PartitionReceiver{
  def props(endpoint: URI,
            topic: String,
            partitionId: String,
            parameters: ListenParameters,
            tokenProvider: () => String,
            automaticReconnect: Boolean,
            objectMapper: ObjectMapper) =
    Props(new PartitionReceiver(endpoint, topic, partitionId, parameters, tokenProvider, automaticReconnect, objectMapper) )
}

class PartitionReceiver (val endpoint: URI,
                         val topic: String,
                         val partitionId: String,
                         val parameters: ListenParameters,
                         val tokenProvider: () => String,
                         val automaticReconnect: Boolean,
                         val objectMapper: ObjectMapper)  extends Actor with ActorLogging
{

  log.info(">>> NEW INSTANCE ")

  var listeners: List[ActorRef] = List()
  var lastCursor: Option[Cursor] = None

  val wsClient = NingWSClient()

  override def preStart() = {
    log.info(">>> RESTART --> " + listeners)
    self ! Init
  }


  override def receive: Receive = {
    case Init => lastCursor match {
      case None => listen(parameters)
      case Some(cursor) => listen(ListenParameters(Option(cursor.offset),
                                                   parameters.batchLimit,
                                                   parameters.batchFlushTimeoutInSeconds,
                                                   parameters.streamLimit))
    }
      log.info(">>> INIT --> " + listeners)
    case NewListener(listener) => listeners = listeners ++ List(listener)
    case streamEvent: SimpleStreamEvent => streamEvent.events.foreach{event =>
        lastCursor = Some(streamEvent.cursor)
        listeners.foreach(listener => listener ! Tuple4(topic, partitionId, streamEvent.cursor, event))
    }
  }

  // TODO check earlier ListenParameters
  // TODO what about closing connections?
  def listen(parameters: ListenParameters) =
    wsClient.url(String.format(endpoint + client.URI_EVENT_LISTENING,
                               topic,
                               partitionId,
                               parameters.startOffset.getOrElse(throw new IllegalStateException("no startOffset set")),
                               parameters.batchLimit.getOrElse(throw new IllegalStateException("no batchLimit set")).toString,
                               parameters.batchFlushTimeoutInSeconds.getOrElse(throw new IllegalStateException("no batchFlushTimeoutInSeconds set")).toString,
                               parameters.streamLimit.getOrElse(throw new IllegalStateException("no streamLimit set")).toString))
            .withHeaders (("Authorization", "Bearer " + tokenProvider.apply()) ,
                          ("Content-Type",  "application/json"))
            .getStream()
            .map{
              case (response, body) => {
                    if(response.status < 200 || response.status > 299) {
                      log.warning("could not listen for events on [topic={}, partition={}] -> [response.status={}] -> restarting",
                                  topic, partitionId, response.status)
                      listeners.foreach(_ ! ConnectionClosed(topic, partitionId, lastCursor))

                      if(automaticReconnect) {
                        log.info("initiating reconnect to [topic={}, partition={}]...", topic, partitionId)
                        self ! Init
                      }
                    }
                    else {
                      listeners.foreach(_ ! ConnectionOpened(topic, partitionId))

                      // TODO more functional logic

                      val bout = new ByteArrayOutputStream(1024)

                      /*
                       * We can not simply rely on EOL for the end of each JSON object as
                       * Nakadi puts the in the middle of the response body sometimes.
                       * For this reason, we need to apply some very simple JSON parsing logic.
                       *
                       * See also http://json.org/ for string parsing semantics
                       */
                      var stack: Int = 0
                      var hasOpenString: Boolean = false

                      body |>>> Iteratee.foreach[Array[Byte]] { bytes =>
                        for(byteItem <- bytes) {
                          bout.write(byteItem.asInstanceOf[Int])

                          if (byteItem == '"')  hasOpenString = !hasOpenString
                          else if (!hasOpenString && byteItem == '{')  stack += 1
                          else if (!hasOpenString && byteItem == '}') {
                            stack -= 1

                            if (stack == 0 && bout.size != 0) {
                              val streamEvent = objectMapper.readValue(bout.toByteArray, classOf[SimpleStreamEvent])
                              log.debug("received [streamingEvent={}]", streamEvent)

                              if(Option(streamEvent.events).isDefined && ! streamEvent.events.isEmpty)
                                self ! streamEvent
                              else{
                                log.debug(s"received empty [streamingEvent={}] --> ignored", streamEvent)
                              }
                              bout.reset()
                            }
                          }
                        }
                      }
        }
      }
            }
            .map { x =>
              log.info("connection closed to [topic={}, partition={}]", topic, partitionId)
              listeners.foreach(_ ! ConnectionClosed(topic, partitionId, lastCursor))
              if(automaticReconnect) {
                log.info(s"[automaticReconnect=$automaticReconnect] -> reconnecting")
                self ! Init
              }
            }


  override def toString = s"PartitionReceiver(listeners=$listeners, lastCursor=$lastCursor, endpoint=$endpoint, topic=$topic, partitionId=$partitionId, parameters=$parameters, automaticReconnect=$automaticReconnect)"
}
