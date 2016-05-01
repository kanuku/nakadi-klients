package org.zalando.nakadi.client.actor

import akka.actor.ActorLogging
import akka.actor.Actor
import akka.stream.actor.ActorPublisher
import EventReceivingActor._
import scala.collection.mutable.Queue
import org.zalando.nakadi.client.scala.model.Cursor




object EventReceivingActor {
  case class NextEvent(lastReceivedCursor:Cursor)
}



class EventReceivingActor (url: String, cursor:Option[Cursor]) extends Actor with ActorLogging with ActorPublisher[Cursor] {
  
  override def preStart() {
   self ! cursor
 }

  override def receive: Receive = {
    case cursor: Cursor => onNext(cursor)

  }
}