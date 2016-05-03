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

/**
 * This actor serves as Sink for the pipeline.<br>
 * 1. It receives the message and the cursor from the payload.
 * 2. It tries to deserialize the message to EventStreamBatch, containing a cursor and a sequence of Events.
 * 3. Passes the deserialized sequence of events to the listener.
 * 4. Sends the received cursor from the Publisher, to be passed to the pipeline.
 *
 */

class EventConsumer[T <: Event](url: String, //
                                listener: Listener[T], //
                                receivingActor: ActorRef, //
                                des: Deserializer[EventStreamBatch[T]])
    extends Actor with ActorLogging with ActorSubscriber {

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
      log.debug("[EventConsumer] Event for lstnr {} and url {} with msg {}", listener.id, url, message)
      handleMsg(message)
    case OnError(err: Throwable) =>
      log.error(err, "[EventConsumer] onError [preCursor {} currCursor {} listner {} url {}]", prevCursor, currCursor, listener.id, url)
      context.stop(self)
    case OnComplete =>
      log.info("[EventConsumer] onComplete [preCursor {} currCursor {} listner {} url {}]", prevCursor, currCursor, listener.id, url)
  }

  def request4NextEvent(cursor: Cursor) = {
    prevCursor = currCursor
    currCursor = cursor
    log.debug("[EventConsumer] NextEvent [preCursor {} currCursor {} listner {} url {}]", prevCursor, currCursor, listener.id, url)
    receivingActor ! NextEvent(Option(cursor))
  }

  def handleMsg(message: String) = {
    Try(deserializeMsg(message, des)) match {
      case Success(EventStreamBatch(cursor, Some(events))) =>
        listener.onReceive(url, cursor, events)
        request4NextEvent(cursor)
      case Success(EventStreamBatch(cursor, None)) =>
        log.debug("[EventConsumer] Keep alive msg at {}", cursor)
        request4NextEvent(cursor)
      case Failure(error) =>
        val errorMsg = "[EventConsumer] DeserializationError [preCursor %s currCursor %s listner %s url %s error %s]".format(prevCursor, currCursor, listener.id, url, error.getMessage)
        log.error(error, message)
        listener.onError(url, null, ClientError("Failed to Deserialize with an error: " + errorMsg, None))
    }
  }
  def deserializeMsg(msg: String, des: Deserializer[EventStreamBatch[T]]): EventStreamBatch[T] = des.from(msg)
}

 

