package org.zalando.nakadi.client.actor

import scala.collection.mutable.Queue

import org.zalando.nakadi.client.scala.model.Cursor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.Cancel
import akka.stream.actor.ActorPublisherMessage.Request

object EventReceivingActor {
  case class NextEvent(lastReceivedCursor: Option[Cursor])
  case class HttpError(status:Int, msg:Option[String])
}

class EventReceivingActor(url: String) extends Actor with ActorLogging with ActorPublisher[Option[Cursor]] {

  import EventReceivingActor._

  var prev: Option[Cursor] = None
  var curr: Option[Cursor] = None

  val queue: Queue[Option[Cursor]] = Queue()

  override def receive: Receive = {
    case NextEvent(cursor) =>
      log.info("[EventReceivingActor] Request next event chunk {} ", cursor)
      queue.enqueue(cursor)
      sendEvents()
    case Request(cnt) =>
      log.info("[EventReceivingActor] Requested {} event chunks", cnt)
      sendEvents()
    case Cancel =>
      log.info("[EventReceivingActor] Stopping receiving events!")
      context.stop(self)
    case HttpError(status,_) =>
      log.error("[EventReceivingActor] {}",status)
    case e =>
      log.error(e.toString())
  }

  def sendEvents() {
    while (isActive && totalDemand > 0 && !queue.isEmpty) {
//        prev = curr
        val cursor = queue.dequeue()
        log.info("Requesting events 4 cursor {} at url {} ", cursor, url)
        onNext(cursor)
    }
  }
}

