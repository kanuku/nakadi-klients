package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.utils.ClientBuilder

import com.google.common.base.Strings

object ClientFactory {

  System.setProperty("NAKADI_HOST", "nakadi-staging.aruha-test.zalan.do")
//  System.setProperty("DELETE_EVENTS_AFTER_TEST", "true")
  System.setProperty("NAKADI_SECURED_CONNECTION", "true")
  System.setProperty("NAKADI_PORT", "443")
  System.setProperty("OAUTH2_ACCESS_TOKENS", "eyJraWQiOiJwbGF0Zm9ybS1pYW0tdmNlaHloajYiLCJhbGciOiJFUzI1NiJ9.eyJzdWIiOiJmOThmOGY0ZC1hNTczLTQzODYtYjdmNS1hOGVkNDkxYTQ0OTIiLCJodHRwczovL2lkZW50aXR5LnphbGFuZG8uY29tL3JlYWxtIjoidXNlcnMiLCJodHRwczovL2lkZW50aXR5LnphbGFuZG8uY29tL3Rva2VuIjoiQmVhcmVyIiwiaHR0cHM6Ly9pZGVudGl0eS56YWxhbmRvLmNvbS9tYW5hZ2VkLWlkIjoiZmJlbmphbWluIiwiYXpwIjoienRva2VuIiwiaHR0cHM6Ly9pZGVudGl0eS56YWxhbmRvLmNvbS9icCI6IjgxMGQxZDAwLTQzMTItNDNlNS1iZDMxLWQ4MzczZmRkMjRjNyIsImF1dGhfdGltZSI6MTQ5NTM0Nzc1NywiaXNzIjoiaHR0cHM6Ly9pZGVudGl0eS56YWxhbmRvLmNvbSIsImV4cCI6MTQ5NjIyMDQ4NiwiaWF0IjoxNDk2MjE2ODc2fQ.MOTvpa_oT_bsJftxQh6WpbDeghdnh2rCcP9q0gqd6wAnyOYddZCgDHpTr28xFH6-JtoXHLWrg4SrqavR4pUFqQ")

  def OAuth2Token(): Option[() => String] = Option(()=>System.getProperty("OAUTH2_ACCESS_TOKENS", null))

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

