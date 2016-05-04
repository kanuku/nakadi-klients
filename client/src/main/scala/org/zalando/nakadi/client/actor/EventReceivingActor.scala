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
  case class HttpError(
    url: String,
    cursor: Option[Cursor],
    status: Int,
    msg: Option[String])
}

/**
 * This actor is a Publisher and serves as source for the pipeline. <br>
 * 1. It receives cursors from the EventConsumingActor.
 * 2. It queues the received cursors.
 * 3. It sends the next cursor(s) from the queue(FIFO) to the pipeline depending on the demand.
 */
class EventReceivingActor(url: String) extends Actor with ActorLogging with ActorPublisher[Option[Cursor]] {

  import EventReceivingActor._

  val queue: Queue[Option[Cursor]] = Queue()

  override def receive: Receive = {
    case NextEvent(cursor) =>
      log.debug("[EventReceivingActor] Request next event chunk {} ", cursor)
      queue.enqueue(cursor)
      sendEvents()
    case Request(cnt) =>
      log.debug("[EventReceivingActor] Requested {} event chunks", cnt)
      sendEvents()
      Thread.sleep(100000)
    case Cancel =>
      log.debug("[EventReceivingActor] Stopping the stream of events!")
      context.stop(self)
    case HttpError(url, cursor, status, _) =>
      log.error("[EventReceivingActor] Cursor {} on URL {} caused error {}", cursor, url, status)
    case e =>
      log.error(e.toString())
  }

  def sendEvents() = while (isActive && totalDemand > 0 && !queue.isEmpty) {
    val cursor = queue.dequeue()
    log.debug("[EventReceivingActor] Requesting events [cursor {} at url {}] ", cursor, url)
    onNext(cursor)
  }

}

