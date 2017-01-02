package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.utils.ClientBuilder
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

object ClientFactory {

  private val log = LoggerFactory.getLogger(ClientFactory.getClass)

  def OAuth2Token(): Option[() => String] =Option(System.getProperty("OAUTH2_ACCESS_TOKENS", "05c4db83-4327-44ab-9951-e431c92946a3")) match {
    case None => null
    case Some(token) => Option(() => token);
  }
  private def defaultClient() = new ClientBuilder() //
    .withTokenProvider(OAuth2Token())
    .withSecuredConnection(false) //
    .withVerifiedSslCertificate(false) //
    .withPort(443) //

  def buildScalaClient() = {
    defaultClient().build()
  }
  def buildScalaClient(host: String) = {
    defaultClient().withHost(host).build()
  }
  def buildJavaClient() = {
    defaultClient().buildJavaClient()
  }

  def deleteAllEventTypes(startsWith: String) = {
    val client = buildScalaClient()
    val result = client.getEventTypes() map {
      case Right(Some(in)) =>
        in.foreach { p =>
          if (p.name.startsWith(startsWith))
            Try(client.deleteEventType(p.name)) match {
              case Success(result) => result.map {
                case None =>
                  log.info("Managed to delete event:" + p.name)
                case Some(error) =>
                  log.error("failed to delete event {} with eror: " + error,p.name)
              }
              case Failure(error) => log.error("Deletion of eventType {} failed with:"+error.getMessage,p.name)
            }
        }
      case Right(None) =>
        log.info("Events are empty")
      case Left(error) =>
        log.error("An error occurred:" + error)

    }
    Await.result(result, 15.second)
    client.stop()
  }

}




object A extends App{
     ClientFactory.deleteAllEventTypes("SimpleEvent")
}