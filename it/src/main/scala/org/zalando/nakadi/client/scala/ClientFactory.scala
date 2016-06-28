package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.utils.ClientBuilder
import java.util.function.Supplier

object ClientFactory {
  import sys.process._
  import scala.language.postfixOps
  def OAuth2Token(): Option[() => String] = Option(() => "25c71f66-f93a-409d-8983-8888dfac0942")
  def getJavaClient() =
    builder().buildJavaClient();

  def getScalaClient() = builder().build()

  private def builder() = {
//    	  useSandbox()
        useStaging()
//    useLocal()
  }
  private def useLocal() = {
    new ClientBuilder() //
      .withHost("localhost") //
      .withPort(8080) //
      .withSecuredConnection(false) // s
      .withVerifiedSslCertificate(false) // s
  }
  private def useSandbox() = {
    ClientBuilder()
      .withHost("nakadi-sandbox.aruha-test.zalan.do")
      .withPort(443)
      .withSecuredConnection(true) //s
      .withVerifiedSslCertificate(false) //s
      .withTokenProvider(ClientFactory.OAuth2Token())
  }
  private def useStaging() = {
    useSandbox()
      .withHost("nakadi-staging.aruha-test.zalan.do")
  }
}