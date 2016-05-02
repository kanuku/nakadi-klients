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

object EventConsumer {
  case class Msg(msg: ByteString)
  case class ShutdownMsg()
}

case class MyEventExample(orderNumber: String) extends Event

class EventConsumer[T <: Event](url: String, //
                                listener: Listener[T], //
                                receivingActor: ActorRef, //
                                des: Deserializer[EventStreamBatch[T]])
    extends Actor with ActorLogging with ActorSubscriber with MessageSplitter {

  import EventConsumer._
  import EventReceivingActor._

//   val requestStrategy = WatermarkRequestStrategy(50)
  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = {
      log.info("########## Received: {}", remainingRequested)
      Math.max(remainingRequested, 10)
    }
  }

  override def receive: Receive = {
    case OnNext(msg: ByteString) =>
      val message = msg.utf8String
      log.info("[EventConsumer] Event for lstnr {} and url {} with msg {}", listener.id, url, message)
      Try(deserializeMsg(message, des)) match {
        case Success(EventStreamBatch(cursor, Some(events))) =>
          listener.onReceive(url, cursor, events)
          request4NextEvent(cursor)
        case Success(EventStreamBatch(cursor, None)) =>
          log.info("[EventConsumer] Keep alive msg at {}", cursor)
          request4NextEvent(cursor)
        case Failure(error) =>
          val errorMsg = "[EventConsumer] Failed to Deserialize with error:" + error.getMessage
          log.error(error, message)
          listener.onError(url, null, ClientError("Failed to Deserialize with an error: "+errorMsg, None))
        //TODO: Restart itself
      }
    case OnError(err: Throwable) =>
      log.error(err, "EventConsumer] Received Exception!")
      context.stop(self)
    case OnComplete => // 5
      log.info("[EventConsumer] Stream Completed!")
//      context.stop(self)
  }

  def request4NextEvent(cursor: Cursor) = {
    
    receivingActor ! NextEvent(Option(cursor))
  }
}

trait MessageSplitter {

  def deserializeMsg[T <: Event](msg: String, des: Deserializer[EventStreamBatch[T]]): EventStreamBatch[T] = des.from(msg)
}

 

