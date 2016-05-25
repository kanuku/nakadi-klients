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

object EventConsumingActor {
  case class Init(cursor:Option[Cursor])
}

class EventConsumingActor(url: String,
                          receivingActor: ActorRef, //
                          handler: EventHandler)
    extends Actor with ActorLogging with ActorSubscriber {
  import ModelConverter._
  import EventConsumingActor._

  var initialCursor: Option[Cursor] = null

  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = {
      Math.max(remainingRequested, 10)
    }
  }

  override def receive: Receive = {
    case Init(cursor) =>
      log.debug("Initializing - handler {} - cursor - {}", cursor)
      initialCursor = cursor
    case OnNext(msg: ByteString) =>
      import util.Random
      if (Random.nextFloat() > 0.9 && Random.nextBoolean() && Random.nextBoolean())
        throw new IllegalStateException("OMG, not again!")

      val message = msg.utf8String
      log.debug("Event - cursor {} - url {} - msg {}", initialCursor, url, message)
      handler.handleOnReceive(url, message)
    case OnError(err: Throwable) =>
      log.error("onError - cursor {} - url {} - error {}", initialCursor, url, err.getMessage)
      context.stop(self)
    case OnComplete =>
      log.info("onComplete - cursor {} - url {}", initialCursor, url)
      context.stop(self)
  }

  override def postRestart(reason: Throwable) {
    super.postRestart(reason)
    log.info(s">>>>>>>>>>>>> <<<<<<<<<<<<<<<")
    log.info(s">>>>>>>>>>>>> Restarted because of ${reason.getMessage}")
    log.info(">>>>>>>>>>>>> Current cursor {} <<<<<<<<<<<<<<<", initialCursor)

  }
}

 

