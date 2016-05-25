package org.zalando.nakadi.client.scala

import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.java.{ ClientHandler => JClientHandler }
import org.zalando.nakadi.client.java.{ ClientHandlerImpl => JClientHandlerImpl }
import org.zalando.nakadi.client.java.model.{ Event => JEvent }
import org.zalando.nakadi.client.scala.model.Event

import com.typesafe.scalalogging.Logger

import HttpFactory.TokenProvider
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.HostConnectionPool
import akka.http.scaladsl.HttpsConnectionContext
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import akka.stream.ActorMaterializerSettings
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

sealed trait EmptyJavaEvent extends JEvent
sealed trait EmptyScalaEvent extends Event

trait Connection {
  //Connection details
  import HttpFactory.TokenProvider
  def tokenProvider(): Option[TokenProvider]
  def executeCall(request: HttpRequest): Future[HttpResponse]

  def requestFlow(): Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]
  def materializer(): ActorMaterializer
  def actorSystem(): ActorSystem
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
    new ClientImpl(connection)
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
  var registrationCounter: AtomicLong = new AtomicLong(0);
  type HttpFlow[T] = Flow[(HttpRequest, T), (Try[HttpResponse], T), HostConnectionPool]
  type StepResult[T] = (T, Option[String])

  implicit val actorSystem = ActorSystem("Nakadi-Client-Connections")
  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem)
      .withInputBuffer(
        initialSize = 8,
        maxSize = 16))
  private implicit val http = Http(actorSystem)
  private val actors: Map[String, Actor] = Map()

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def requestFlow(): Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = newSslContext(securedConnection, verifySSlCertificate) match {
    case Some(result) => http.outgoingConnectionHttps(host, port, result)
    case None =>
      logger.warn("Disabled HTTPS, switching to HTTP only!")
      http.outgoingConnection(host, port)
  }

  def executeCall(request: HttpRequest): Future[HttpResponse] = {
    val response: Future[HttpResponse] =
      Source.single(request)
        .via(requestFlow).
        runWith(Sink.head)
    logError(response)
    response
  }

  private def logError(future: Future[Any]) {
    future recover {
      case e: Throwable => logger.error("Failed to call endpoint with: ", e.getMessage)
    }
  }

}
