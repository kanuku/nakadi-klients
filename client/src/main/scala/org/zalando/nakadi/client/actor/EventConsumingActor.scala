package org.zalando.nakadi.client.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.stream.actor.ActorSubscriber
import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor.RequestStrategy
import akka.util.ByteString
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.Listener

object EventConsumer {

  case class Msg(msg: ByteString)

  case class ShutdownMsg()
}

case class MyEventExample(orderNumber: String) extends Event

class EventConsumer[T <: Event](url: String, listener: Listener[T], des: Deserializer[T]) extends Actor with ActorLogging with ActorSubscriber {
  import EventConsumer._
  var count = 0

  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = {
      Math.max(remainingRequested, 10)
    }
  }

  override def receive: Receive = {
    case OnNext(msg: ByteString) =>
      val message = msg.utf8String
      log.info("##############"+message)
      if (message.contains("events")) {
        count += 1
        log.info("[Got event nr {} for {} and with msg {} ] ", count, url, message)
        //      Try(ser.fromJson(msg.utf8String)) match {
        //        case Success(event) =>
        //          listener.onReceive(eventType, cursor, event)
        //        case Failure(error) =>
        //          val errorMsg = "Failed to Deserialize with error:" + error.getMessage
        //        listener.onError(url, null, ClientError("Failed to Deserialize with an error!", None))
      }
    //      }
    case OnNext(_) =>
      println("Got something")
  }
}

trait MessageSplitter {

  def deserializeMsg[T <: Event](msg: String)(implicit des: Deserializer[EventStreamBatch[T]]): EventStreamBatch[T] = des.from(msg)
}

 

