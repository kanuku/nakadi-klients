package org.zalando.nakadi.client

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri.apply
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import akka.http.scaladsl.HttpsConnectionContext
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.MediaTypes.`application/json`
import javax.net.ssl.SSLContext
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.{ SSLContext, TrustManager, X509TrustManager }
import scala.concurrent.duration.DurationInt
import akka.actor.Terminated

trait Client {

  /**
   * Returns monitoring metrics
   */
  def metrics(): Future[HttpResponse]

  /**
   * Returns a list of all registered EventTypes.
   */
  def eventTypes(): Future[HttpResponse]

  /**
   * Creates a new EventType.
   *
   * @param event - The event to create.
   *
   */
  def newEventType(eventType: Any): Future[Boolean]

  /**
   * Returns the EventType identified by its name.
   * @param name - Name of the EventType
   */
  def eventType(name: String): Future[HttpResponse]
  /**
   * Updates the EventType identified by its name.
   *
   *
   * @param name - Name of the EventType
   * @param event - Event to update
   */
  def updateEventType(name: String, event: Any): Future[HttpResponse]
  /**
   * Deletes an EventType identified by its name.
   *
   *
   * @param name - Name of the EventType
   */
  def deleteEventType(name: String): Future[HttpResponse]
  
  
  

  /**
   * Starts a stream delivery for the specified partitions of the given EventType.
   */
  def eventsOfEventType(eventType: String): Future[HttpResponse]

  def partitionsOfEventType(eventType: String): Future[HttpResponse]

  def partitionByName(eventType: String, partitionName: String): Future[HttpResponse]

  def validationStrategies(): Future[HttpResponse]

  def enrichmentStrategies(): Future[HttpResponse]

  def partitionStrategies(): Future[HttpResponse]

  /**
   * Shuts down the communication system of the client
   */

  def stop(): Future[Terminated]

}

object Connection {

  def getNewSslContext(secured: Boolean, verified: Boolean): Option[HttpsConnectionContext] = {
    (secured, verified) match {
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
  }
}

/**
 * Class for handling the configuration and connections only.
 */
private[client] class Connection(host: String, port: Int, tokenProvider: () => String, securedConnection: Boolean, verifySSlCertificate: Boolean) extends Client {
  import Connection._
  private implicit val actorSystem = ActorSystem("Nakadi-client")
  private implicit val http = Http(actorSystem)
  implicit val materializer = ActorMaterializer()

  private val timeout = 5.seconds

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def eventTypes(): Future[HttpResponse] = {
    get(URI_EVENT_TYPES)
  }

  def stop(): Future[Terminated] = actorSystem.terminate()

  private val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = {
    getNewSslContext(securedConnection, verifySSlCertificate) match {
      case Some(result) => http.outgoingConnectionHttps(host, port, result)
      case None =>
        logger.warn("Disabling HTTPS")
        http.outgoingConnection(host, port)
    }
  }

  private def get(endpoint: String): Future[HttpResponse] = {
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
}

object Main extends App {

  val host = "nakadi-sandbox.aruha-test.zalan.do"
  val OAuth2Token = () =>  ""
  val port = 443
  val client = new Connection(host, port, OAuth2Token, true, false)
  val response = client.eventTypes()
  implicit val materializer = client.materializer
  //  response.map { x =>
  //    println("########################")
  //    println("########################")
  //    println("Client=    " + x.toString)
  //  }

  response.map { response =>
    response.status match {
      case status if (response.status.isSuccess()) =>
        println("" + response.toString())
        println("" + response.entity.toStrict(5.seconds).map(x => Some(x.data.decodeString("UTF-8"))))
        println("" + response.entity.getContentType())
        client.stop()
      case _ =>
        println("")
    }
  }
}