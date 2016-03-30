package org.zalando.nakadi.client

import java.security.SecureRandom
import java.security.cert.X509Certificate

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import org.slf4j.LoggerFactory

import com.typesafe.scalalogging.Logger

import akka.actor.{ ActorSystem, Terminated }
import akka.http.scaladsl.{ Http, HttpsConnectionContext }
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse, MediaRange }
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.Uri.apply
import akka.http.scaladsl.model.headers
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Sink, Source }
import javax.net.ssl.{ SSLContext, TrustManager, X509TrustManager }

trait Connection {
  def get(endpoint: String): Future[HttpResponse]
  def stop(): Future[Terminated]
  def materializer(): ActorMaterializer
}

/**
 * Companion object for factory methods.
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
 * Class for handling the configuration and most basic calls.
 */

private[client] class ConnectionImpl(host: String, port: Int, tokenProvider: () => String, securedConnection: Boolean, verifySSlCertificate: Boolean) extends Connection {
  import Connection._

  private implicit val actorSystem = ActorSystem("Nakadi-Connections")
  private implicit val http = Http(actorSystem)
  implicit val materializer = ActorMaterializer()
  private val timeout = 5.seconds

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  private val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = newSslContext(securedConnection, verifySSlCertificate) match {
    case Some(result) => http.outgoingConnectionHttps(host, port, result)
    case None =>
      logger.warn("Disabled HTTPS, switching to HTTP only.")
      http.outgoingConnection(host, port)
  }

  def get(endpoint: String): Future[HttpResponse] = {
    logger.info("Calling {}", endpoint)
    val response: Future[HttpResponse] =
      Source.single(DefaultHttpRequest(endpoint))
        .via(connectionFlow).
        runWith(Sink.head)
    logError(response)
    response
  }

  private def logError(future: Future[Any]) {
    future recover {
      case e: Throwable => logger.error("Failed to call endpoint with: ", e.getMessage)
    }
  }

  private def DefaultHttpRequest(url: String): HttpRequest = {
    HttpRequest(uri = url).withHeaders(headers.Authorization(OAuth2BearerToken(tokenProvider())),
      headers.Accept(MediaRange(`application/json`)))
  }

  def stop(): Future[Terminated] = actorSystem.terminate()
}

