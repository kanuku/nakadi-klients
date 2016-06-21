package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.utils.ClientBuilder
import java.util.function.Supplier

object ClientFactory {
  import sys.process._
  import scala.language.postfixOps
  def OAuth2Token(): Option[() => String] = Option(() => "********-****-****-****-************")
  def getJavaClient() =
    builder().buildJavaClient();

  def getScalaClient() = builder().build()

  private def builder() = {
    //	  useTest()
    //    useStaging()
    useLocal()
  }
  private def useLocal() = {
    new ClientBuilder() //
      .withHost("localhost") //
      .withPort(8080) //
      .withSecuredConnection(false) // s
      .withVerifiedSslCertificate(false) // s
  }
  private def useTest() = {
    ClientBuilder()
      .withHost("nakadi********.**********.********")
      .withPort(443)
      .withSecuredConnection(true) //s
      .withVerifiedSslCertificate(false) //s
      .withTokenProvider(ClientFactory.OAuth2Token())
  }
  private def useStaging() = {
    useTest()
      .withHost("nakadi********.**********.********")
  }
}