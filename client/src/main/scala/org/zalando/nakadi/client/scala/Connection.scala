package org.zalando.nakadi.client.scala

import java.security.SecureRandom
import java.security.cert.X509Certificate
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
import org.zalando.nakadi.client.actor.EventConsumingActor
import org.zalando.nakadi.client.actor.EventReceivingActor
import org.zalando.nakadi.client.actor.EventReceivingActor.NextEvent
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventStreamBatch
import org.zalando.nakadi.client.java.model.{Event => JEvent}
import org.zalando.nakadi.client.utils.FutureConversions
import com.typesafe.scalalogging.Logger
import akka.NotUsed
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Terminated
import akka.actor.actorRef2Scala
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.HostConnectionPool
import akka.http.scaladsl.HttpsConnectionContext
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.ActorMaterializerSettings
import akka.stream.OverflowStrategy
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorSubscriber
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Framing
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import org.zalando.nakadi.client.utils.ModelConverter

trait JEmptyEvent extends JEvent
trait SEmptyEvent extends Event

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
  def subscribeJava[T <: org.zalando.nakadi.client.java.model.Event](
    url: String, cursor: Optional[org.zalando.nakadi.client.java.model.Cursor],
    listener: org.zalando.nakadi.client.java.Listener[T])(implicit des: Deserializer[org.zalando.nakadi.client.java.model.EventStreamBatch[T]]): java.util.concurrent.Future[Void]

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
    val msgHandler:EventHandler[JEmptyEvent, T]  = new EventHandler[JEmptyEvent, T](None,Option((des,listener)))
    handleSubscription(url, cursor, msgHandler)
  }
  private def handleSubscription[J <: JEvent, S <: Event](url: String, cursor: Option[Cursor], msgHandler: EventHandler[J, S] ) = {
    logger.info("Handler [{}] cursor {} uri [{}]", msgHandler.id, cursor, url)
    
    
    import EventReceivingActor._

    //Create the Actors
    val eventReceiver = actorSystem.actorOf(Props(classOf[EventReceivingActor], url))
    val receiver = ActorPublisher[Option[Cursor]](eventReceiver)

    val eventConsumer = actorSystem.actorOf(Props(classOf[EventConsumingActor[J,S]], url,eventReceiver, msgHandler))
    val consumer = ActorSubscriber[ByteString](eventConsumer)

    //Setup the pipeline flow
    val pipeline = Flow[Option[Cursor]].via(requestCreator(url))
      .via(connection)
      .buffer(RECEIVE_BUFFER_SIZE, OverflowStrategy.backpressure)
      .via(requestRenderer)

    //Put all pieces together
    val result= Source.fromPublisher(receiver)
      .via(pipeline)
      .runForeach(_.runWith(Sink.fromSubscriber(consumer)))
      
      result.onFailure{
      case error =>
        logger.error(error.getMessage)
    }
    
    
    
    //HOPAAA!!
    eventReceiver ! NextEvent(cursor)
  }

  private def requestCreator(url: String): Flow[Option[Cursor], HttpRequest, NotUsed] =
    Flow[Option[Cursor]].map(withHttpRequest(url, _, None, tokenProvider))

  private def requestRenderer: Flow[HttpResponse, Source[ByteString, Any], NotUsed] =
    Flow[HttpResponse].filter(x => x.status.isSuccess())
      .map(_.entity.withSizeLimit(Long.MaxValue).dataBytes.via(delimiterFlow))

  private def delimiterFlow = Flow[ByteString]
    .via(Framing.delimiter(ByteString(EVENT_DELIMITER), maximumFrameLength = RECEIVE_BUFFER_SIZE, allowTruncation = true))

  def subscribeJava[T <: org.zalando.nakadi.client.java.model.Event](
    url: String, cursor: Optional[org.zalando.nakadi.client.java.model.Cursor],
    listener: org.zalando.nakadi.client.java.Listener[T])(implicit des: Deserializer[org.zalando.nakadi.client.java.model.EventStreamBatch[T]]) = {
    FutureConversions.fromFuture2FutureVoid {
      Future {
        import ModelConverter._
        val scalaCursor = toScalaCursor(cursor)
        val scalaListener = toScalaListener(listener)
        val msgHandler:EventHandler[T, SEmptyEvent]  = new EventHandler[T, SEmptyEvent](Option((des,listener)),None)
                handleSubscription(url, scalaCursor, msgHandler)

      }
    }

  }
}
