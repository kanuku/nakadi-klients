package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.utils.ClientBuilder
import java.util.function.Supplier

object ClientFactory {
  import sys.process._
  import scala.language.postfixOps
  def host(): String = "nakadi-sandbox.aruha-test.zalan.do"
  def localHost(): String = "localhost"
  def localPort(): Integer = 8080
  def OAuth2Token(): Option[() => String] = Option(() => "5375a46c-9195-4a01-90bc-5e304eb7fae8")
  def port(): Integer = 443
  def connection(): Connection = Connection.newConnection(host, port, OAuth2Token(), true, false)
  def client(): Client = new ClientImpl(connection, "UTF-8")

  def getJavaClient() =
    defaultBuilder().buildJavaClient();

  def getScalaClient() = defaultBuilder().build()

  private def defaultBuilder() = {
    connect2Default()
  }
  private def connect2Local() = {
    new ClientBuilder() //
      .withHost(ClientFactory.localHost()) //
      .withPort(ClientFactory.localPort()) //
      .withSecuredConnection(false) // s
      .withVerifiedSslCertificate(false) // s
  }
  private def connect2Default() = {
    ClientBuilder()
      .withHost(ClientFactory.host())
      .withSecuredConnection(true) //s
      .withVerifiedSslCertificate(false) //s
      .withTokenProvider(ClientFactory.OAuth2Token())
  }
}