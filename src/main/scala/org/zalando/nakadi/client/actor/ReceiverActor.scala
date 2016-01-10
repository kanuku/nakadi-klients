package org.zalando.nakadi.client.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import org.zalando.nakadi.client
import org.zalando.nakadi.client.{Cursor, SimpleStreamEvent, ListenParameters}
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


  def listen()(implicit reader: Reads[SimpleStreamEvent]) = Future {
    wsClient.url(String.format(client.URI_EVENT_LISTENING,
                              topic,
                              partitionId,
                              parameters.startOffset,
                              parameters.batchLimit,
                              parameters.batchFlushTimeoutInSeconds,
                              parameters.streamLimit))
            .withHeaders (("Authorization", "Bearer " + tokenProvider.apply()) ,
                          ("Content-Type",  "application/json"))
            .get()
            .map{response =>
                if(response.status < 200 || response.status > 299) {
                  log.warning("could not listen for events on [topic={}, partition={}] -> [response.status={}, response={}] -> restarting",
                              topic, partitionId, response.status, response.statusText)
                  listeners.foreach(_ ! ConnectionClosed(topic, partitionId, lastCursor))

                  if(automaticReconnect) {
                    log.info("initiating reconnect to [topic={}, partition={}]...", topic, partitionId)
                    self ! Init
                  }
                }
                else {
                  listeners.foreach(_ ! ConnectionOpened(topic, partitionId))

                  // FIXME we need not to work with Streams because of long polling
                  self ! Json.parse(response.body).as[SimpleStreamEvent]
            }
      }
  }.map { x =>
    log.info("connection closed to [topic={}, partition={}]", topic, partitionId)
    listeners.foreach(_ ! ConnectionClosed(topic, partitionId, lastCursor))
  }
}
