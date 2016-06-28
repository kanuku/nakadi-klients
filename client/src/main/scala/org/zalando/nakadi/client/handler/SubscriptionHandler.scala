package org.zalando.nakadi.client.handler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.Failure
import scala.util.Success

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.actor.SupervisingActor
import org.zalando.nakadi.client.actor.SupervisingActor.SubscribeMsg
import org.zalando.nakadi.client.actor.SupervisingActor.UnsubscribeMsg
import org.zalando.nakadi.client.scala.ClientError
import org.zalando.nakadi.client.scala.Connection
import org.zalando.nakadi.client.scala.EventHandler
import org.zalando.nakadi.client.scala.HttpFactory
import org.zalando.nakadi.client.scala.model.Cursor

import akka.NotUsed
import akka.actor.ActorRef
import akka.actor.PoisonPill
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorAttributes
import akka.stream.OverflowStrategy
import akka.stream.Supervision
import akka.stream.actor.ActorSubscriber
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Framing
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString

trait SubscriptionHandler {
  /**
   * Handles the subscription for an eventHandler.
   */
  def subscribe(eventTypeName: String, endpoint: String, cursor: Option[Cursor], eventHandler: EventHandler): Option[ClientError]
  def unsubscribe(eventTypeName: String, partition: Option[String], listenerId: String): Option[ClientError]
  def createPipeline(cursor: Option[Cursor], consumingActor: ActorRef, url: String, eventHandler: EventHandler)
}

class SubscriptionHandlerImpl(val connection: Connection) extends SubscriptionHandler {
  import HttpFactory._
  import SupervisingActor._
  //Local variables
  private implicit val materializer = connection.materializer(decider())

  private val supervisingActor = connection.actorSystem.actorOf(Props(classOf[SupervisingActor], connection, this), "SupervisingActor" + System.currentTimeMillis())

  private val RECEIVE_BUFFER_SIZE = 40960
  private val EVENT_DELIMITER = "\n"

  val logger = LoggerFactory.getLogger(this.getClass)

  def subscribe(eventTypeName: String, endpoint: String, cursor: Option[Cursor], eventHandler: EventHandler) = {
    supervisingActor ! SubscribeMsg(eventTypeName, endpoint, cursor, eventHandler)
    None
  }
  def unsubscribe(eventTypeName: String, partition: Option[String], listenerId: String) = {
    supervisingActor ! UnsubscribeMsg(eventTypeName, partition, listenerId)
    None
  }

  def decider(): Supervision.Decider = {
    case _ => Supervision.Stop
  }

  def createPipeline(cursor: Option[Cursor], consumingActor: ActorRef, url: String, eventHandler: EventHandler) = {

    val subscriber = ActorSubscriber[ByteString](consumingActor)

    //Setup a flow for the request
    val requestFlow = Flow[Option[Cursor]].via(requestCreator(url))
      .via(connection.requestFlow())
      .buffer(RECEIVE_BUFFER_SIZE, OverflowStrategy.backpressure)
      .via(requestRenderer(eventHandler))
      .withAttributes(ActorAttributes.supervisionStrategy(decider()))

    //create the pipeline
    val result = Source(List(cursor))
      .via(requestFlow).withAttributes(ActorAttributes.supervisionStrategy(decider()))
      .runForeach(_.runWith(Sink.fromSubscriber(subscriber)))

    result.onComplete {
      case Success(requestSource) ⇒
      case Failure(e) ⇒
        val msg = "An exception occurred: " + e.getMessage
        eventHandler.handleOnError(url, Some(msg), e)
        logger.error(msg + e)
        connection.actorSystem().scheduler.scheduleOnce(5.seconds)(stopActor(consumingActor)) //TODO: Make it configurable
    }
  }

  private def stopActor(actor: ActorRef) = {
    logger.info("Stopping the actor [{}]", actor.path.toString())
    actor ! PoisonPill
  }

  private def requestCreator(url: String): Flow[Option[Cursor], HttpRequest, NotUsed] =
    Flow[Option[Cursor]].map(withHttpRequest(url, _, None, connection.tokenProvider))

  private def requestRenderer(handler:EventHandler): Flow[HttpResponse, Source[ByteString, Any], NotUsed] =
    Flow[HttpResponse].filter { x =>
      if (x.status.isSuccess()) {
          logger.info("Connection established with success!")
        x.status.isSuccess()
      } else {
        x.entity.toStrict(10.second).map { body =>
          logger.error(s"http-status: ${ x.status.intValue().toString()}, reason[${ x.status.reason()}], body:[${body.data.decodeString("UTF-8")}]")
        }
        false
      }
    }.map(_.entity.withSizeLimit(Long.MaxValue).dataBytes.via(delimiterFlow))

  private def delimiterFlow = Flow[ByteString]
    .via(Framing.delimiter(ByteString(EVENT_DELIMITER), maximumFrameLength = RECEIVE_BUFFER_SIZE, allowTruncation = true))
}