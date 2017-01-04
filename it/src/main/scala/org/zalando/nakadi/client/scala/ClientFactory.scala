package org.zalando.nakadi.client.scala

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.utils.ClientBuilder
import com.google.common.base.Strings

object ClientFactory {

  private val log = LoggerFactory.getLogger(ClientFactory.getClass)
  System.setProperty("NAKADI_HOST", "nakadi-staging.aruha-test.zalan.do")
  System.setProperty("DELETE_EVENTS_AFTER_TEST", "true")
  System.setProperty("NAKADI_SECURED_CONNECTION", "true")
  System.setProperty("NAKADI_PORT", "443")
  System.setProperty("OAUTH2_ACCESS_TOKENS", "f62a1c16-ab65-4127-bc44-eaa2ffa1f64e")

  def OAuth2Token(): Option[() => String] = Option(System.getProperty("OAUTH2_ACCESS_TOKENS", null)) match {
    case None        => null
    case Some(token) if Strings.isNullOrEmpty(token)=> null
    case Some(token) => Option(() => token);
  }

  private def defaultClient() = {
		  val host = System.getProperty("NAKADI_HOST", "localhost")
		  val securedConnection= System.getProperty("NAKADI_SECURED_CONNECTION", "false").toBoolean
		  val verifySslCertificate= System.getProperty("NAKADI_VERIFY_SSL_CERTIFICATE", "false").toBoolean
		  val port=System.getProperty("NAKADI_PORT", "8080").toInt
    new ClientBuilder() //
    .withHost(host)
    .withTokenProvider(OAuth2Token())
    .withSecuredConnection(securedConnection) //
    .withVerifiedSslCertificate(verifySslCertificate) //
    .withPort(port) //
  }

  def buildScalaClient() = {
    defaultClient().build()
  }
  def buildScalaClient(host: String) = {
    defaultClient().withHost(host).build()
  }
  def buildJavaClient() = {
    defaultClient().buildJavaClient()
  }

  def deleteEventTypesThatStartWith(startsWith: String) = {
    if (deleteEventsAfterTest) {
      val client = buildScalaClient()
      val result = client.getEventTypes().map {
        case in => in.right.map {
          case Some(in) =>
            in.foreach { p =>
              if (p.name.startsWith(startsWith))
                Await.result(client.deleteEventType(p.name), 1.second)
            }
          case None =>
            log.info("Events are empty")

        }
      }
      Await.result(result, 15.second)
      client.stop()
    }
  }

  def deleteEventsAfterTest: Boolean = System.getProperty("DELETE_EVENTS_AFTER_TEST", "true").toBoolean
  def deleteEventsOnError: Boolean = System.getProperty("DELETE_EVENTS_ON_ERROR", "true").toBoolean

}

object Application extends App {

  //  ClientFactory.buildScalaClient().deleteEventType("SimpleEventTest-validatePublishedNrOfEvents3KEFJcDFw0FD")
//  ClientFactory.deleteEventTypesThatStartWith("SimpleEvent")
  ClientFactory.deleteEventTypesThatStartWith("ClientIntegrationTest")
}