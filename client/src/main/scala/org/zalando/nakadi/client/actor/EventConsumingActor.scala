package org.zalando.nakadi.client.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.stream.actor.ActorSubscriber
import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor.RequestStrategy
import akka.util.ByteString
import org.zalando.nakadi.client.Listener
import org.zalando.nakadi.client.NakadiDeserializer
import org.zalando.nakadi.client.model.Cursor
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import org.zalando.nakadi.client.ClientError

object EventConsumer {

  case class Msg(
    msg: ByteString)

  case class ShutdownMsg()
}

class EventConsumer[T](url: String, eventType: String, listener: Listener[T], ser: NakadiDeserializer[T]) extends Actor with ActorLogging with ActorSubscriber {
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
      if (message.contains("events")){
        
        count += 1
      println(s"[Got event nr $count for type $eventType and cursor * and message $message ] ")
      //      Try(ser.fromJson(msg.utf8String)) match {
      //        case Success(event) =>
      //          listener.onReceive(eventType, cursor, event)
      //        case Failure(error) =>
      //          val errorMsg = "Failed to Deserialize with error:" + error.getMessage
      listener.onError(eventType, null, ClientError("Failed to Deserialize with an error!", None))
      }
    //      }
    case OnNext(_) =>
      println("Got something")
  }
}


