package org.zalando.nakadi.client.actor

import java.io.ByteArrayOutputStream

import akka.actor.{ActorRef, ActorLogging, Actor}
import org.zalando.nakadi.client
import org.zalando.nakadi.client.{Cursor, SimpleStreamEvent, ListenParameters}
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.{Reads, Json}
import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed case class Init()
case class NewListener(listener: ActorRef)
case class ConnectionOpened(topic: String, partition: String)
case class ConnectionClosed(topic: String, partition: String, lastCursor: Option[Cursor])


class PartitionReceiver (val topic: String,
                         val partitionId: String,
                         val parameters: ListenParameters,
                         val tokenProvider: () => String,
                         val automaticReconnect: Boolean) (implicit reader: Reads[SimpleStreamEvent]) extends Actor with ActorLogging
{
  var listeners: List[ActorRef] = List()
  var lastCursor: Option[Cursor] = None

  val wsClient = NingWSClient()


  override def preStart = self ! Init


  override def receive: Receive = {
    case Init => listen()
    case NewListener(listener) => listeners = listeners ++ List(listener)
    case streamEvent: SimpleStreamEvent => streamEvent.events.map{event =>
        lastCursor = Some(streamEvent.cursor)
        listeners.foreach(listener => listener ! Tuple4(topic, partitionId, streamEvent.cursor, event))
    }
  }


  def listen()(implicit reader: Reads[SimpleStreamEvent]) =
    wsClient.url(String.format(client.URI_EVENT_LISTENING,
                              topic,
                              partitionId,
                              parameters.startOffset,
                              parameters.batchLimit,
                              parameters.batchFlushTimeoutInSeconds,
                              parameters.streamLimit))
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
                          else if (!hasOpenString && byteItem == '{')  stack += 1;
                          else if (!hasOpenString && byteItem == '}') {
                            stack -= 1

                            if (stack == 0 && bout.size != 0) {
                              val streamEvent = Json.parse(bout.toByteArray).as[SimpleStreamEvent]
                              log.debug("received [streamingEvent={}]", streamEvent)
                              self ! streamEvent
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
            }
}
