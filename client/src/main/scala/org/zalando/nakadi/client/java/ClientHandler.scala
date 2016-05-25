package org.zalando.nakadi.client.java

import java.util.Optional

import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.java.model.{ Event => JEvent }
import org.zalando.nakadi.client.scala.model.{ Cursor => ScalaCursor }
import org.zalando.nakadi.client.java.model.{ EventStreamBatch => JEventStreamBatch }
import org.zalando.nakadi.client.java.{ StreamParameters => JStreamParameters }
import org.zalando.nakadi.client.java.{ Listener => JListener }
import org.zalando.nakadi.client.scala.Connection
import org.zalando.nakadi.client.scala.EmptyScalaEvent
import org.zalando.nakadi.client.scala.EventHandler
import org.zalando.nakadi.client.scala.EventHandlerImpl
import org.zalando.nakadi.client.scala.HttpFactory
import org.zalando.nakadi.client.scala.{ StreamParameters => ScalaStreamParameters }
import org.zalando.nakadi.client.utils.FutureConversions
import org.zalando.nakadi.client.utils.ModelConverter

import com.typesafe.scalalogging.Logger

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import org.zalando.nakadi.client.handler.SubscriptionHandlerImpl

/**
 * Handleer for delegating low-level http calls and listener subscriptions for the Java API.
 */
trait ClientHandler {

  def logger(): Logger
  def connection: Connection

  def deserialize4Java[T](response: HttpResponse, des: Deserializer[T]): Future[Optional[T]]

  def get(endpoint: String): Future[HttpResponse]

  def get4Java[T](endpoint: String, des: Deserializer[T]): java.util.concurrent.Future[Optional[T]]
  def get4Java[T](endpoint: String, headers: Seq[HttpHeader], des: Deserializer[T]): java.util.concurrent.Future[Optional[T]]
  def post4Java[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): java.util.concurrent.Future[Void]

  def subscribeJava[T <: JEvent](url: String, parameters: JStreamParameters, listener: JListener[T])(implicit des: Deserializer[JEventStreamBatch[T]])
}

class ClientHandlerImpl(val connection: Connection) extends ClientHandler {
  val logger: Logger = Logger(LoggerFactory.getLogger(this.getClass))
  import HttpFactory._
  private implicit val mat = connection.materializer()

  //TODO: Use constructor later make the tests simpler
  private val subscriber = new SubscriptionHandlerImpl(connection)
  
  def deserialize4Java[T](response: HttpResponse, des: Deserializer[T]): Future[Optional[T]] = response match {
    case HttpResponse(status, headers, entity, protocol) if (status.isSuccess()) =>

      Try(Unmarshal(entity).to[String].map(body => des.from(body))) match {
        case Success(result) => result.map(Optional.of(_))
        case Failure(error)  => throw new RuntimeException(error.getMessage)
      }

    case HttpResponse(status, headers, entity, protocol) if (status.isFailure()) =>
      throw new RuntimeException(status.reason())
  }

  def get(endpoint: String): Future[HttpResponse] = {
    logger.info("Get - URL {}", endpoint)
    connection.executeCall(withHttpRequest(endpoint, HttpMethods.GET, Nil, connection.tokenProvider, None))
  }

  def get4Java[T](endpoint: String, des: Deserializer[T]): java.util.concurrent.Future[Optional[T]] = {
    FutureConversions.fromFuture2Future(get(endpoint).flatMap(deserialize4Java(_, des)))
  }
  def get4Java[T](endpoint: String, headers: Seq[HttpHeader], des: Deserializer[T]): java.util.concurrent.Future[Optional[T]] = {
    FutureConversions.fromFuture2Future(connection.executeCall(withHttpRequest(endpoint, HttpMethods.GET, headers, connection.tokenProvider, None)).flatMap(deserialize4Java(_, des)))
  }
  def post4Java[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): java.util.concurrent.Future[Void] = {
    val entity = serializer.to(model)
    logger.info("Posting to endpoint {}", endpoint)
    logger.debug("Data to post {}", entity)
    val result = connection.executeCall(
      withHttpRequestAndPayload(endpoint, serialize4Java(model), HttpMethods.POST, connection.tokenProvider))
      .flatMap(response4Java(_))
    FutureConversions.fromOption2Void(result)
  }

  private def serialize4Java[T](model: T)(implicit serializer: Serializer[T]): String =
    Try(serializer.to(model)) match {
      case Success(result) => result
      case Failure(error)  => throw new RuntimeException("Failed to serialize: " + error.getMessage)
    }

  private def response4Java[T](response: HttpResponse): Future[Option[String]] = response match {
    case HttpResponse(status, headers, entity, protocol) if (status.isSuccess()) =>
      Try(Unmarshal(entity).to[String]) match {
        case Success(result) => result.map(Option(_))
        case Failure(error)  => throw new RuntimeException(error.getMessage)
      }

    case HttpResponse(status, headers, entity, protocol) if (status.isFailure()) =>
      Unmarshal(entity).to[String].map { x =>
        val msg = "http-stats(%s) - %s - problem: %s ".format(status.intValue(), x, status.defaultMessage())
        logger.warn(msg)
        throw new RuntimeException(msg)
      }
  }

  def subscribeJava[T <: JEvent](url: String, parameters: JStreamParameters, listener: JListener[T])(implicit des: Deserializer[JEventStreamBatch[T]]) =
    FutureConversions.fromFuture2FutureVoid {
      (Future {
        import ModelConverter._
        val params: Option[ScalaStreamParameters] = toScalaStreamParameters(parameters)
        val eventHandler: EventHandler = new EventHandlerImpl[T, EmptyScalaEvent](Left((des, listener)))
        val finalUrl = withUrl(url, params)
        subscriber.subscribe(url, getCursor(params), eventHandler)
      })
    }
  private def getCursor(params: Option[ScalaStreamParameters]): Option[ScalaCursor] = params match {
    case Some(ScalaStreamParameters(cursor, batchLimit, streamLimit, batchFlushTimeout, streamTimeout, streamKeepAliveLimit, flowId)) => cursor
    case None => None
  }

}

