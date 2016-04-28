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



//class EventReceivingActor extends Actor with ActorLogging with ActorPublisher[NextEvent] {
//  val queue: Queue[NextEvent] = Queue()
//  val receivedOffset: Set[Integer] = Set()
//
//  override def receive: Receive = {
//    case msg: NextEvent =>
//      if (!visited(msg.lastReceivedCursor.offset.get)) {
//        visited += url.url
//        queue.enqueue(url)
//        if (isActive && totalDemand > 0) {
//          onNext(queue.dequeue())
//        }
//      }
//  }
//}