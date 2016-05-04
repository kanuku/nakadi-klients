package org.zalando.nakadi.client.actor

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.scala.ClientError
import org.zalando.nakadi.client.scala.Listener
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventStreamBatch
import EventReceivingActor.NextEvent
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import akka.stream.actor.ActorSubscriber
import akka.stream.actor.ActorSubscriberMessage.OnComplete
import akka.stream.actor.ActorSubscriberMessage.OnError
import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor._
import akka.util.ByteString
import org.zalando.nakadi.client.utils.ModelConverter
import org.zalando.nakadi.client.scala.EventHandler
import org.zalando.nakadi.client.scala.ScalaResult
import org.zalando.nakadi.client.scala.JavaResult
import org.zalando.nakadi.client.scala.ErrorResult
import org.zalando.nakadi.client.java.model.{ Event => JEvent }
/**
 * This actor serves as Sink for the pipeline.<br>
 * 1. It receives the message and the cursor from the payload.
 * 2. It tries to deserialize the message to EventStreamBatch, containing a cursor and a sequence of Events.
 * 3. Passes the deserialized sequence of events to the listener.
 * 4. Sends the received cursor from the Publisher, to be passed to the pipeline.
 *
 */

class EventConsumingActor[J <: JEvent, S <: Event](url: String,
                                             receivingActor: ActorRef, //
                                             handler: EventHandler[J, S])
    extends Actor with ActorLogging with ActorSubscriber {
  import ModelConverter._
  var currCursor: Cursor = null
  var prevCursor: Cursor = null

  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = {
      Math.max(remainingRequested, 10)
    }
  }

  override def receive: Receive = {
    case OnNext(msg: ByteString) =>
      val message = msg.utf8String
      log.debug("[EventConsumer] Event - url {} - msg {}", url, message)
      handleMsg(message)
    case OnError(err: Throwable) =>
      log.error(err, "[EventConsumer] onError [preCursor {} currCursor {} url {}]", prevCursor, currCursor, url)
      context.stop(self)
    case OnComplete =>
      log.info("[EventConsumer] onComplete [preCursor {} currCursor {} url {}]", prevCursor, currCursor, url)
  }

  def request4NextEvent(cursor: Cursor) = {
    prevCursor = currCursor
    currCursor = cursor
    log.debug("[EventConsumer] NextEvent [preCursor {} currCursor {} url {}]", prevCursor, currCursor, url)
    receivingActor ! NextEvent(Option(cursor))
  }

  def handleMsg(message: String) = {

    handler.handle(url, message) match {
      case Right(Some(cursor)) =>
        request4NextEvent(cursor)
      case Right(None) =>
        log.error("Message lacks of a cursor [{}]", message)
      case Left(ErrorResult(error)) =>
        log.error("Handler could not handle message {}", error.getMessage)
    }
  }
}

 

