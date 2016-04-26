package org.zalando.nakadi.client.scala


import java.security.SecureRandom
import java.security.cert.X509Certificate
import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.actor.EventConsumer
import org.zalando.nakadi.client.model.Cursor
import com.typesafe.scalalogging.Logger
import akka.actor.{ Props, ActorSystem, ActorLogging, _ }
import akka.http.scaladsl.{ Http, HttpsConnectionContext }
import akka.http.scaladsl.model.{ HttpHeader, _ }
import akka.stream.{ ActorMaterializer, OverflowStrategy }
import akka.stream.actor.{ ActorSubscriber, RequestStrategy }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import akka.util.ByteString
import javax.net.ssl.{ SSLContext, TrustManager, X509TrustManager }
import org.zalando.nakadi.client.actor.EventConsumer.ShutdownMsg
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.Listener


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
  def put[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse]
  def subscribe[T](url: String, eventType: String, request: HttpRequest, listener: Listener[T])(implicit des: Deserializer[T])

  def stop(): Future[Terminated]
  def materializer(): ActorMaterializer
}

/**
 * Companion object with factory methods.
 */
object Connection {

  /**
   *
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
   * Creates a new
   */
  def newConnection(host: String, port: Int, tokenProvider: () => String, securedConnection: Boolean, verifySSlCertificate: Boolean): Connection =
    new ConnectionImpl(host, port, tokenProvider, securedConnection, verifySSlCertificate)
}

/**
 * Class for handling the basic http calls.
 */

sealed class ConnectionImpl(val host: String, val port: Int, val tokenProvider: () => String, securedConnection: Boolean, verifySSlCertificate: Boolean) extends Connection {
  import Connection._

  private implicit val actorSystem = ActorSystem("Nakadi-Client-Connections")
  private implicit val http = Http(actorSystem)
  implicit val materializer = ActorMaterializer()
  private val actors: Map[String, Actor] = Map()

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  val connection: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = newSslContext(securedConnection, verifySSlCertificate) match {
    case Some(result) => http.outgoingConnectionHttps(host, port, result)
    case None =>
      logger.warn("Disabled HTTPS, switching to HTTP only!")
      http.outgoingConnection(host, port)
  }

  def get(endpoint: String): Future[HttpResponse] = {
    logger.info("Get: {}", endpoint)
    executeCall(withHttpRequest(endpoint, HttpMethods.GET, Nil, tokenProvider))
  }
  def get(endpoint: String, headers: Seq[HttpHeader]): Future[HttpResponse] = {
    logger.info("Get: {}", endpoint)
    executeCall(withHttpRequest(endpoint, HttpMethods.GET, headers, tokenProvider))
  }
  def stream(endpoint: String, headers: Seq[HttpHeader]): Future[HttpResponse] = {
    logger.info("Streaming on Get: {}", endpoint)
    executeCall(withHttpRequest(endpoint, HttpMethods.GET, headers, tokenProvider)) //TODO: Change to stream single event
  }

  def put[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse] = {
    logger.info("Get: {}", endpoint)
    executeCall(withHttpRequest(endpoint, HttpMethods.GET, Nil, tokenProvider))
  }

  def post[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse] = {
    val entity = serializer.to(model)
    logger.info("Posting to endpoint {}", endpoint)
    logger.debug("Data to post {}", entity)
    executeCall(withHttpRequestAndPayload(endpoint, entity, HttpMethods.POST, tokenProvider))
  }

  def delete(endpoint: String): Future[HttpResponse] = {
    logger.info("Delete: {}", endpoint)
    executeCall(withHttpRequest(endpoint, HttpMethods.DELETE, Nil, tokenProvider))
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

  def subscribe[T](url: String, eventType: String, request: HttpRequest, listener: Listener[T])(implicit des: Deserializer[T]) = {
    import EventConsumer._
    case class MyEventExample(orderNumber: String)
    val subscriberRef = actorSystem.actorOf(Props(classOf[EventConsumer[T]], url, eventType, listener, des))
    val subscriber = ActorSubscriber[ByteString](subscriberRef)
    val sink2 = Sink.fromSubscriber(subscriber)
    val flow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = connection

    val req = Source.single(request) //
      .via(flow) //
      .buffer(100, OverflowStrategy.backpressure) //
      .map(x => x.entity.dataBytes)
      .runForeach(_.runWith(sink2)).onComplete { _ =>
        subscriberRef ! ShutdownMsg
        println("Shutting down")
      }
  }

}

