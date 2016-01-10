package org.zalando.nakadi.client.actor

import akka.actor.{ActorLogging, Actor}
import org.zalando.nakadi.client.{Cursor, Event, Listener}


class ListenerActor(val listener: Listener) extends Actor with ActorLogging{
  override def receive: Receive = {
    case (topic: String, partition: String, cursor: Cursor, event: Event) => listener.onReceive(topic, partition, cursor, event)
    case ConnectionOpened(topic: String, partition: String) => listener.onConnectionOpened(topic, partition)
    case ConnectionClosed(topic: String, partition: String, lastCursor: Option[Cursor]) => listener.onConnectionClosed(topic, partition, lastCursor)
  }

}
