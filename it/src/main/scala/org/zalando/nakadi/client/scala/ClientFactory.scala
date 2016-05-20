package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.utils.ClientBuilder
import java.util.function.Supplier

object ClientFactory {
  import sys.process._
  import scala.language.postfixOps
  def host(): String = "nakadi-sandbox.aruha-test.zalan.do"
  def localHost(): String = "localhost"
  def localPort(): Integer = 8080
  def OAuth2Token(): Option[() => String] = Option(() => "63e78acc-3fae-46d6-b8c5-1c213d99ba7d")
  def port(): Integer = 443
  def client(): Client = Connection.newClient(host, port, OAuth2Token(), true, false)

  def getJavaClient() =
    builder().buildJavaClient();

  def getScalaClient() = builder().build()

  private def builder() = {
//    useRemote()
    useLocal()
  }
  private def useLocal() = {
    new ClientBuilder() //
      .withHost(ClientFactory.localHost()) //
      .withPort(ClientFactory.localPort()) //
      .withSecuredConnection(false) // s
      .withVerifiedSslCertificate(false) // s
  }
  private def useRemote() = {
    ClientBuilder()
      .withHost(ClientFactory.host())
      .withSecuredConnection(true) //s
      .withVerifiedSslCertificate(false) //s
      .withTokenProvider(ClientFactory.OAuth2Token())
  }
}