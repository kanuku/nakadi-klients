package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.utils.ClientBuilder

import com.google.common.base.Strings

object ClientFactory {

//  System.setProperty("NAKADI_HOST", "staging.nakadi.zalan.do")
//  System.setProperty("DELETE_EVENTS_AFTER_TEST", "true")
//  System.setProperty("NAKADI_SECURED_CONNECTION", "true")
//  System.setProperty("NAKADI_PORT", "443")
//  System.setProperty("OAUTH2_ACCESS_TOKENS", "yourToken")

  def OAuth2Token(): Option[() => String] = Option(System.getProperty("NAKADI_OAUTH2_TOKEN", null)) match {
    case None                                        => null
    case Some(token) if Strings.isNullOrEmpty(token) => null
    case Some(token)                                 => Option(() => token);
  }

  private def defaultClient() = {
    val host = System.getProperty("NAKADI_HOST", "localhost")
    val securedConnection = System.getProperty("NAKADI_SECURED_CONNECTION", "false").toBoolean
    val verifySslCertificate = System.getProperty("NAKADI_VERIFY_SSL_CERTIFICATE", "false").toBoolean
    val port = System.getProperty("NAKADI_PORT", "8080").toInt
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

  def deleteEventsAfterTest: Boolean = System.getProperty("DELETE_EVENTS_AFTER_TEST", "true").toBoolean
  def deleteEventsOnError: Boolean = System.getProperty("DELETE_EVENTS_ON_ERROR", "true").toBoolean

}

