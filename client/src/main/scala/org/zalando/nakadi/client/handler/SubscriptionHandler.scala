package org.zalando.nakadi.client.handler

import java.util.concurrent.TimeoutException

import scala.concurrent.ExecutionContext.Implicits.global

import org.reactivestreams.Subscriber
import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.actor.SupervisingActor
import org.zalando.nakadi.client.actor.SupervisingActor.Subscribe
import org.zalando.nakadi.client.scala.Connection
import org.zalando.nakadi.client.scala.EventHandler
import org.zalando.nakadi.client.scala.HttpFactory
import org.zalando.nakadi.client.scala.model.Cursor

import com.typesafe.scalalogging.Logger

import akka.NotUsed
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.http.scaladsl.model.EntityStreamException
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Framing
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString

trait SubscriptionHandler {
  /**
   * Handles the subscription for an eventHandler.
   */
  def subscribe(eventTypeName: String, endpoint: String, cursor: Option[Cursor], eventHandler: EventHandler)
  def unsubscribe(eventTypeName: String,partition:String, listenerId: String)
  def createPipeline(cursor: Option[Cursor], subscriber: Subscriber[ByteString], url: String, eventHandler: EventHandler)
}

class SubscriptionHandlerImpl(val connection: Connection) extends SubscriptionHandler {
  import HttpFactory._
  import SupervisingActor._
  //Local variables
  private implicit val materializer = connection.materializer()

  val supervisingActor = connection.actorSystem.actorOf(Props(classOf[SupervisingActor], connection, this), "EventReceivingActor-" + System.currentTimeMillis())

  private val RECEIVE_BUFFER_SIZE = 40960
  private val EVENT_DELIMITER = "\n"

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def subscribe(eventTypeName: String, endpoint: String, cursor: Option[Cursor], eventHandler: EventHandler) = {
    supervisingActor ! Subscribe(eventTypeName,endpoint, cursor, eventHandler)
  }
  def unsubscribe(eventTypeName: String,partition:String, listenerId: String)= {
    supervisingActor ! Unsubscribe(eventTypeName,partition, listenerId)
  }

  def createPipeline(cursor: Option[Cursor], subscriber: Subscriber[ByteString], url: String, eventHandler: EventHandler) = {
    //Setup a flow for the request
    val requestFlow = Flow[Option[Cursor]].via(requestCreator(url))
      .via(connection.requestFlow())
      .buffer(RECEIVE_BUFFER_SIZE, OverflowStrategy.backpressure)
      .via(requestRenderer)

    //create the pipeline
    val result = Source(List(cursor))
      .via(requestFlow)
      .runForeach(_.runWith(Sink.fromSubscriber(subscriber)))

    result.onFailure {
      case exception: TimeoutException => //When connection does not react within the timeout range
        logger.error("Received an Exception timeout, restarting the client {}", exception.getMessage)
        eventHandler.handleOnError(url, None, exception)
      case exception: EntityStreamException => //When connection is broken without  
        logger.error("Received an Exception timeout, restarting the client {}", exception.getMessage)
        eventHandler.handleOnError(url, None, exception)
      case e =>
        logger.error("Exception not handled" + e)
    }
  }

  private def requestCreator(url: String): Flow[Option[Cursor], HttpRequest, NotUsed] =
    Flow[Option[Cursor]].map(withHttpRequest(url, _, None, connection.tokenProvider))

  private def requestRenderer: Flow[HttpResponse, Source[ByteString, Any], NotUsed] =
    Flow[HttpResponse].filter(x => x.status.isSuccess())
      .map(_.entity.withSizeLimit(Long.MaxValue).dataBytes.via(delimiterFlow))

  private def delimiterFlow = Flow[ByteString]
    .via(Framing.delimiter(ByteString(EVENT_DELIMITER), maximumFrameLength = RECEIVE_BUFFER_SIZE, allowTruncation = true))
}