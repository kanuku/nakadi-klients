package org.zalando.nakadi.client.scala

import java.security.SecureRandom
import java.security.cert.X509Certificate
import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.actor.EventConsumer
import com.typesafe.scalalogging.Logger
import akka.actor.{ Props, ActorSystem, ActorLogging, _ }
import akka.http.scaladsl.{ Http, HttpsConnectionContext }
import akka.http.scaladsl.model.{ HttpHeader, _ }
import akka.stream.{ ActorMaterializer, OverflowStrategy }
import akka.stream.actor.{ ActorSubscriber, RequestStrategy }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import akka.util.ByteString
import javax.net.ssl.{ SSLContext, TrustManager, X509TrustManager }
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.scala.model.Event
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import akka.http.scaladsl.model.{ HttpHeader, HttpMethod, HttpMethods, HttpResponse, MediaRange }
import java.util.Optional
import org.zalando.nakadi.client.utils.FutureConversions
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.actor.EventReceivingActor
import akka.stream.actor.ActorPublisher
import org.zalando.nakadi.client.scala.model.EventStreamBatch
import akka.stream.Outlet
import akka.stream.ActorMaterializerSettings
import akka.stream.Attributes
import akka.NotUsed
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.Http.HostConnectionPool
import akka.stream.scaladsl.Framing

trait Connection extends HttpFactory {

  //Connection details
  def host: String
  def port: Int
  def tokenProvider(): TokenProvider
  def connection(): Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]

  def get(endpoint: String): Future[HttpResponse]
  def get(endpoint: String, headers: Seq[HttpHeader]): Future[HttpResponse]
  def stream(endpoint: String, headers: Seq[HttpHeader]): Future[HttpResponse]
  def delete(endpoint: String): Future[HttpResponse]
  def post[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse]
  def get4Java[T](endpoint: String, des: Deserializer[T]): java.util.concurrent.Future[Optional[T]]
  def get4Java[T](endpoint: String, headers: Seq[HttpHeader], des: Deserializer[T]): java.util.concurrent.Future[Optional[T]]
  def post4Java[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): java.util.concurrent.Future[Void]
  def put[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse]
  def subscribe[T <: Event](url: String, cursor: Option[Cursor], listener: Listener[T])(implicit des: Deserializer[EventStreamBatch[T]])
  def subscribeJava[T <: org.zalando.nakadi.client.java.model.Event](url: String, request: HttpRequest, listener: org.zalando.nakadi.client.java.Listener[T])(implicit des: Deserializer[T])

  def stop(): Future[Terminated]
  def materializer(): ActorMaterializer
}

/**
 * Companion object with factory methods.
 */
object Connection {
  val RECEIVE_BUFFER_SIZE = 40960
  val EVENT_DELIMITER = "\n"
  /**
   * Creates a new SSL context for usage with connections based on the HTTPS protocol.
   */
  def newSslContext(secured: Boolean, verified: Boolean): Option[HttpsConnectionContext] = (secured, verified) match {
    case (true, true) => Some(new HttpsConnectionContext(SSLContext.getDefault))
    case (true, false) =>
      val permissiveTrustManager: TrustManager = new X509TrustManager() {
        override def checkClientTrusted(x$1: Array[java.security.cert.X509Certificate], x$2: String): Unit = {}
        override def checkServerTrusted(x$1: Array[java.security.cert.X509Certificate], x$2: String): Unit = {}
        override def getAcceptedIssuers(): Array[X509Certificate] = Array.empty
      }
      val sslContext = SSLContext.getInstance("TLS")
      sslContext.init(Array.empty, Array(permissiveTrustManager), new SecureRandom())
      Some(new HttpsConnectionContext(sslContext))
    case _ => None
  }

  /**
   * Creates a new Connection
   */
  def newConnection(host: String, port: Int, tokenProvider: () => String, securedConnection: Boolean, verifySSlCertificate: Boolean): Connection =
    new ConnectionImpl(host, port, tokenProvider, securedConnection, verifySSlCertificate)
}

/**
 * Class for handling the basic http calls.
 */

sealed class ConnectionImpl(val host: String, val port: Int, val tokenProvider: () => String, securedConnection: Boolean, verifySSlCertificate: Boolean) extends Connection {
  import Connection._

  type HttpFlow[T] = Flow[(HttpRequest, T), (Try[HttpResponse], T), HostConnectionPool]
  type StepResult[T] = (T, Option[String])

  private implicit val actorSystem = ActorSystem("Nakadi-Client-Connections")
  private implicit val http = Http(actorSystem)
  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem)
      .withInputBuffer(
        initialSize = 8,
        maxSize = 16))
  private val actors: Map[String, Actor] = Map()

  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  val connection: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = newSslContext(securedConnection, verifySSlCertificate) match {
    case Some(result) => http.outgoingConnectionHttps(host, port, result)
    case None =>
      logger.warn("Disabled HTTPS, switching to HTTP only!")
      http.outgoingConnection(host, port)
  }

  def get(endpoint: String): Future[HttpResponse] = {
    logger.info("Get - URL {}", endpoint)
    executeCall(withHttpRequest(endpoint, HttpMethods.GET, Nil, tokenProvider, None))
  }
  def get(endpoint: String, headers: Seq[HttpHeader]): Future[HttpResponse] = {
    logger.info("Get - URL {} - Headers {}", endpoint, headers)
    executeCall(withHttpRequest(endpoint, HttpMethods.GET, headers, tokenProvider, None))
  }

  private def deserialize4Java[T](response: HttpResponse, des: Deserializer[T]): Future[Optional[T]] = response match {
    case HttpResponse(status, headers, entity, protocol) if (status.isSuccess()) =>

      Try(Unmarshal(entity).to[String].map(body => des.from(body))) match {
        case Success(result) => result.map(Optional.of(_))
        case Failure(error)  => throw new RuntimeException(error.getMessage)
      }

    case HttpResponse(status, headers, entity, protocol) if (status.isFailure()) =>
      throw new RuntimeException(status.reason())
  }
  def stream(endpoint: String, headers: Seq[HttpHeader]): Future[HttpResponse] = {
    logger.info("Streaming on Get: {}", endpoint)
    executeCall(withHttpRequest(endpoint, HttpMethods.GET, headers, tokenProvider, None)) //TODO: Change to stream single event
  }

  def put[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse] = {
    logger.info("Get: {}", endpoint)
    executeCall(withHttpRequest(endpoint, HttpMethods.GET, Nil, tokenProvider, None))
  }

  def post[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse] = {
    val entity = serializer.to(model)
    logger.info("Posting to endpoint {}", endpoint)
    logger.debug("Data to post {}", entity)
    executeCall(withHttpRequestAndPayload(endpoint, entity, HttpMethods.POST, tokenProvider))
  }
  def get4Java[T](endpoint: String, des: Deserializer[T]): java.util.concurrent.Future[Optional[T]] = {
    FutureConversions.fromFuture2Future(get(endpoint).flatMap(deserialize4Java(_, des)))
  }
  def get4Java[T](endpoint: String, headers: Seq[HttpHeader], des: Deserializer[T]): java.util.concurrent.Future[Optional[T]] = {
    FutureConversions.fromFuture2Future(executeCall(withHttpRequest(endpoint, HttpMethods.GET, headers, tokenProvider, None)).flatMap(deserialize4Java(_, des)))
  }
  def post4Java[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): java.util.concurrent.Future[Void] = {
    val entity = serializer.to(model)
    logger.info("Posting to endpoint {}", endpoint)
    logger.debug("Data to post {}", entity)
    val result = executeCall(
      withHttpRequestAndPayload(endpoint, serialize4Java(model), HttpMethods.POST, tokenProvider))
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
  def delete(endpoint: String): Future[HttpResponse] = {
    logger.info("Delete: {}", endpoint)
    executeCall(withHttpRequest(endpoint, HttpMethods.DELETE, Nil, tokenProvider, None))
  }

  private def executeCall(request: HttpRequest): Future[HttpResponse] = {
    val response: Future[HttpResponse] =
      Source.single(request)
        .via(connection).
        runWith(Sink.head)
    logError(response)
    response
  }

  private def logError(future: Future[Any]) {
    future recover {
      case e: Throwable => logger.error("Failed to call endpoint with: ", e.getMessage)
    }
  }

  def stop(): Future[Terminated] = actorSystem.terminate()

  def subscribe[T <: Event](url: String, cursor: Option[Cursor], listener: Listener[T])(implicit des: Deserializer[EventStreamBatch[T]]) = {
    logger.info("Subscribing listener {} to uri {}", listener.id, url)
    import EventReceivingActor._

    //Create the Actors
    val eventReceiver = actorSystem.actorOf(Props(classOf[EventReceivingActor], url))
    val receiver = ActorPublisher[Option[Cursor]](eventReceiver)

    val eventConsumer = actorSystem.actorOf(Props(classOf[EventConsumer[T]], url, listener, eventReceiver, des))
    val consumer = ActorSubscriber[ByteString](eventConsumer)

    val pipeline = Flow[Option[Cursor]].via(requestCreator(url))
      .via(connection)
      .buffer(RECEIVE_BUFFER_SIZE, OverflowStrategy.backpressure)
      .via(requestRenderer)

    Source.fromPublisher(receiver)
      .via(pipeline)
      .runForeach(_.runWith(Sink.fromSubscriber(consumer)))

    eventReceiver ! NextEvent(cursor)
  }

  def requestCreator(url: String): Flow[Option[Cursor], HttpRequest, NotUsed] =
    Flow[Option[Cursor]].map(withHttpRequest(url, _, None, tokenProvider))

  def requestRenderer: Flow[HttpResponse, Source[ByteString, Any], NotUsed] =
    Flow[HttpResponse].filter(x => x.status.isSuccess())
      .map(_.entity.withSizeLimit(Long.MaxValue).dataBytes.via(delimiterFlow))

  def delimiterFlow = Flow[ByteString]
    .via(Framing.delimiter(ByteString(EVENT_DELIMITER), maximumFrameLength = RECEIVE_BUFFER_SIZE, allowTruncation = true))

  def subscribeJava[T <: org.zalando.nakadi.client.java.model.Event](url: String, request: HttpRequest, listener: org.zalando.nakadi.client.java.Listener[T])(implicit des: Deserializer[T]) = ???
}

