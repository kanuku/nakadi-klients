package org.zalando.nakadi.client.scala

import scala.collection.immutable.Seq
import scala.concurrent.Future

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventStreamBatch

import com.typesafe.scalalogging.Logger

import akka.actor.Terminated
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer

/**
 * Handleer for delegating low-level http calls and listener subscriptions.
 */
trait ClientHandler {
  import HttpFactory._
  def logger(): Logger

  def connection(): Connection

  def shutdown(): Future[Terminated] = connection().shutdown()

  def materializer(): ActorMaterializer = connection().materializer()

  def get(endpoint: String): Future[HttpResponse] = {
    logger.info("Get - URL {}", endpoint)
    connection().executeCall(withHttpRequest(endpoint, HttpMethods.GET, Nil, connection().tokenProvider, None))
  }
  def get(endpoint: String, headers: Seq[HttpHeader]): Future[HttpResponse] = {
    logger.info("Get - URL {} - Headers {}", endpoint, headers)
    connection().executeCall(withHttpRequest(endpoint, HttpMethods.GET, headers, connection().tokenProvider, None))
  }
  def stream(endpoint: String, headers: Seq[HttpHeader]): Future[HttpResponse] = {
    logger.info("Streaming on Get: {}", endpoint)
    connection().executeCall(withHttpRequest(endpoint, HttpMethods.GET, headers, connection().tokenProvider, None)) //TODO: Change to stream single event
  }

  def delete(endpoint: String): Future[HttpResponse] = {
    logger.info("Delete: {}", endpoint)
    connection().executeCall(withHttpRequest(endpoint, HttpMethods.DELETE, Nil, connection().tokenProvider, None))
  }

  def put[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse] = {
    logger.info("Get: {}", endpoint)
    connection().executeCall(withHttpRequest(endpoint, HttpMethods.GET, Nil, connection().tokenProvider, None))
  }

  def post[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse] = {
    val entity = serializer.to(model)
    logger.info("Posting to endpoint {}", endpoint)
    logger.debug("Data to post {}", entity)
    connection().executeCall(withHttpRequestAndPayload(endpoint, entity, HttpMethods.POST, connection().tokenProvider))
  }

  def subscribe[T <: Event](endpoint: String, cursor: Option[Cursor], listener: Listener[T])(implicit des: Deserializer[EventStreamBatch[T]]) = {
    val eventHandler: EventHandler = new EventHandlerImpl[EmptyJavaEvent, T](None, Option((des, listener)))
    connection().handleSubscription(endpoint, cursor, eventHandler)
  }
}

class ClientHandlerImpl(val connection: Connection) extends ClientHandler {
  def logger(): Logger = Logger(LoggerFactory.getLogger(this.getClass))
}