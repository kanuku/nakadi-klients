package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.utils.ClientBuilder

object ClientFactory {
  def OAuth2Token(): Option[() => String] = Option(() => System.getProperty("OAUTH2_ACCESS_TOKENS", null));
  def getJavaClient() = builder().buildJavaClient();
  def host() = System.getProperty("NAKADI_HOST", "localhost")
  def port() = System.getProperty("NAKADI_PORT", "8080").toInt
  def verifySSLCertificate() = System.getProperty("NAKADI_VERIFY_SSL_CERTIFICATE", "false").toBoolean
  def securedConnection() = System.getProperty("NAKADI_SECURED_CONNECTION", "false").toBoolean

  def getScalaClient() = builder().build()

  private def builder() = {
    new ClientBuilder() //
      .withHost(host()) //
      .withPort(port()) //
      .withSecuredConnection(securedConnection()) // s
      .withVerifiedSslCertificate(verifySSLCertificate()) // s
  }
}
