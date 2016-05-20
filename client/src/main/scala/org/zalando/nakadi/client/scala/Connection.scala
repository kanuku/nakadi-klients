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
import org.zalando.nakadi.client.actor.EventPublishingActor
import org.zalando.nakadi.client.actor.EventPublishingActor.NextEvent
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventStreamBatch
import org.zalando.nakadi.client.java.model.{ Event => JEvent }
import org.zalando.nakadi.client.java.model.{ EventStreamBatch => JEventStreamBatch }
import org.zalando.nakadi.client.java.model.{ Cursor => JCursor }
import org.zalando.nakadi.client.java.{ StreamParameters => JStreamParameters }
import org.zalando.nakadi.client.java.{ ClientHandler => JClientHandler }
import org.zalando.nakadi.client.java.{ ClientHandlerImpl => JClientHandlerImpl }
import org.zalando.nakadi.client.java.{ Listener => JListener }
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
import akka.actor.ActorRef
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.zalando.nakadi.client.actor.EventReceivingActor
import java.util.concurrent.TimeoutException

sealed trait EmptyJavaEvent extends JEvent
sealed trait EmptyScalaEvent extends Event

trait Connection {
  //Connection details
  import HttpFactory.TokenProvider
  def tokenProvider(): Option[TokenProvider]
  def executeCall(request: HttpRequest): Future[HttpResponse]
  def handleSubscription[J <: JEvent, S <: Event](url: String, cursor: Option[Cursor], eventHandler: EventHandler)

  def connection(): Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]
  def materializer(): ActorMaterializer
  def shutdown(): Future[Terminated]
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
   * Creates a new Client Handler
   */
  def newClient(host: String, port: Int, tokenProvider: Option[() => String], securedConnection: Boolean, verifySSlCertificate: Boolean): Client = {
    val connection = new ConnectionImpl(host, port, tokenProvider, securedConnection, verifySSlCertificate)
    val handler = new ClientHandlerImpl(connection)
    new ClientImpl(handler)
  }

  def newClientHandler4Java(host: String, port: Int, tokenProvider: Option[() => String], securedConnection: Boolean, verifySSlCertificate: Boolean): JClientHandler = {
    new JClientHandlerImpl(new ConnectionImpl(host, port, tokenProvider, securedConnection, verifySSlCertificate))
  }
}

/**
 * Class for handling the basic http calls.
 */

sealed class ConnectionImpl(val host: String, val port: Int, val tokenProvider: Option[() => String], securedConnection: Boolean, verifySSlCertificate: Boolean) extends Connection {
  import Connection._
  import HttpFactory._

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

  def executeCall(request: HttpRequest): Future[HttpResponse] = {
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

  def shutdown(): Future[Terminated] = actorSystem.terminate()

  def handleSubscription[J <: JEvent, S <: Event](url: String, cursor: Option[Cursor], eventHandler: EventHandler) = {
    logger.info("Handler [{}] cursor {} uri [{}]", eventHandler.id, cursor, url)

    import EventPublishingActor._

    //Create the Actors
    val publishingActor = actorSystem.actorOf(Props(classOf[EventReceivingActor], url))
    val publisher = ActorPublisher[Option[Cursor]](publishingActor)

    val subscribingActor = actorSystem.actorOf(Props(classOf[EventConsumingActor], url, publishingActor, eventHandler))
    val subscriber = ActorSubscriber[ByteString](subscribingActor)

    val pipeline = createPipeline(publisher, subscriber, url, eventHandler)

    //Start
    publishingActor ! NextEvent(cursor)
  }

  private def createPipeline(publisher: Publisher[Option[Cursor]], subscriber: Subscriber[ByteString], url: String, eventHandler: EventHandler) = {
    //Setup a flow for the request
    val requestFlow = Flow[Option[Cursor]].via(requestCreator(url))
      .via(connection)
      .buffer(RECEIVE_BUFFER_SIZE, OverflowStrategy.backpressure)
      .via(requestRenderer)

    //create the pipeline
    val result = Source.fromPublisher(publisher)
      .via(requestFlow)
      .runForeach(_.runWith(Sink.fromSubscriber(subscriber)))

    result.onFailure {
      case exception: TimeoutException =>
        logger.error("Received an Exception timeout, restarting the client {}", exception.getMessage)
        eventHandler.handleError(url, None, exception)
    }

    result
  }

  private def start() = {

  }

  private def requestCreator(url: String): Flow[Option[Cursor], HttpRequest, NotUsed] =
    Flow[Option[Cursor]].map(withHttpRequest(url, _, None, tokenProvider))

  private def requestRenderer: Flow[HttpResponse, Source[ByteString, Any], NotUsed] =
    Flow[HttpResponse].filter(x => x.status.isSuccess())
      .map(_.entity.withSizeLimit(Long.MaxValue).dataBytes.via(delimiterFlow))

  private def delimiterFlow = Flow[ByteString]
    .via(Framing.delimiter(ByteString(EVENT_DELIMITER), maximumFrameLength = RECEIVE_BUFFER_SIZE, allowTruncation = true))

}
