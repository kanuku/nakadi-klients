package org.zalando.nakadi.client.actor

import akka.actor.{PoisonPill, Props, ActorLogging, Actor}
import org.zalando.nakadi.client.{ListenParameters, Cursor, Event, Listener}

case class ListenerSubscription(topic: String, partitionId: String)

object ListenerActor{
  def props(listener: Listener) = Props(new ListenerActor(listener))
}

class ListenerActor(val listener: Listener) extends Actor with ActorLogging{

  context.system.eventStream.subscribe(self, classOf[Unsubscription])

  var topicSubscriptions: Set[String] = Set()

  override def receive: Receive = {
    case (topic: String, partition: String, cursor: Cursor, event: Event) => {
      log.debug(s"received [topic=$topic, partition=$partition, cursor=$cursor, event=$event]")
      listener.onReceive(topic, partition, cursor, event)
    }
    case ListenerSubscription(topic: String, partition: String) => {
      topicSubscriptions += topic
      log.info("[listener={}] subscribed to [topic={}, partition={}]", listener.id, topic, partition)
    }
    case ConnectionOpened(topic, partition) => listener.onConnectionOpened(topic, partition)
    case ConnectionClosed(topic, partition, lastCursor) => listener.onConnectionClosed(topic, partition, lastCursor)
    case ConnectionFailed(topic, partition, status, error) => listener.onConnectionFailed(topic, partition, status, error)
    case Unsubscription(topic, listener) => if(topicSubscriptions.contains(topic) && listener.id == this.listener.id) {
      log.info("[listener={}] is unsubscribing [topic={}]", listener.id, topic)
      topicSubscriptions -= topic
      if(topicSubscriptions.isEmpty) {
        log.info("[listener={}] is not subscribed to any topic -> shutting down", listener.id)
        self ! PoisonPill.getInstance
      }
    }
  }

}
