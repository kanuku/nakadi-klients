package org.zalando.nakadi.client.actor

import akka.actor.Actor.Receive
import akka.actor.{ActorLogging, Actor}
import org.zalando.nakadi.client.{Cursor, Event, Listener}


class ListenerActor(val listener: Listener) extends Actor with ActorLogging{

  override def receive: Receive = {
    case (topic: String, partition: String, cursor: Cursor, event: Event) =>
                                                                    listener.onReceive(topic, partition, cursor, event)

  }

}
