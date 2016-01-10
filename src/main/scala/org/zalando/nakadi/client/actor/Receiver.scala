package org.zalando.nakadi.client.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import org.zalando.nakadi.client
import org.zalando.nakadi.client.ListenParameters
import org.zalando.nakadi.client.domain.Event
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Future


sealed case class Init()
case class NewListener(listener: ActorRef)


class Receiver (val topic: String,
                val partitionId: String,
                val parameters: ListenParameters,
                val tokenProvider: () => String,
                val automaticReconnect: Boolean) extends Actor
with ActorLogging
{

  var listeners: List[ActorRef] = List()
  val wsClient = NingWSClient()

  override def preStart = {
    self ! Init
  }

  override def receive: Receive = {
    case Init => listen()
    case NewListener(listener) => listeners = listeners ++ List(listener)
    case event: Event => listeners.foreach(l => l ! event)
  }


  def listen() = Future{
    wsClient.url(String.format(client.URI_EVENT_LISTENING,
                              topic,
                              partitionId,
                              parameters.startOffset,
                              parameters.batchLimit,
                              parameters.batchFlushTimeoutInSeconds,
                              parameters.streamLimit))
            .withHeaders (("Authorization", "Bearer " + tokenProvider.apply()) ,
                          ("Content-Type", "application/json"))
            .get()
      .map{response =>
            if(response.status < 200 || response.status > 299) {
              log.warning("could not listen for events on [topic={}, partition={}] -> [response.status={}, response={}] -> restarting",
                          topic, partitionId, response.status, response.statusText)
              preStart
            }
            else self ! Json.parse(response.body).as[Event]
    }
  }.map(x => log.info("connection closed to [topic={}, partition={}]", topic, partitionId))

}
