package org.zalando.nakadi.client.utils

import org.zalando.nakadi.client.scala.Client
import org.zalando.nakadi.client.scala.ClientImpl
import org.zalando.nakadi.client.scala.Connection
import java.util.function.Supplier


object ClientBuilder {

  def apply(
    host: String = null,
    port: Int = DEFAULT_PORT,
    tokenProvider: () => String = null,
    securedConnection: Boolean = true,
    verifySSlCertificate: Boolean = true) = new ClientBuilder(host, port, tokenProvider, securedConnection, verifySSlCertificate)

  private val DEFAULT_PORT = 443
}

class ClientBuilder  private(host: String = "", //
                             port: Int, //
                             tokenProvider: () => String = () => "", //
                             securedConnection: Boolean = true, //
                             verifySSlCertificate: Boolean = true) {
  def this() = this(null, ClientBuilder.DEFAULT_PORT, null, true, true)
  def withHost(host: String): ClientBuilder = new ClientBuilder(
    checkNotNull(host),
    port,
    tokenProvider,
    securedConnection,
    verifySSlCertificate)

  def withPort(port: Int): ClientBuilder = new ClientBuilder(
    host,
    port,
    tokenProvider,
    securedConnection,
    verifySSlCertificate)

  def withTokenProvider(tokenProvider: () => String): ClientBuilder = new ClientBuilder(
    host,
    port,
    checkNotNull(tokenProvider),
    securedConnection,
    verifySSlCertificate)

  def withTokenProvider4Java(tokenProvider: Supplier[String]): ClientBuilder = withTokenProvider(() => tokenProvider.get())

  def withSecuredConnection(securedConnection: Boolean = true): ClientBuilder = new ClientBuilder(
    host,
    port,
    tokenProvider,
    checkNotNull(securedConnection),
    verifySSlCertificate)

  def withVerifiedSslCertificate(verifySSlCertificate: Boolean = true): ClientBuilder = new ClientBuilder(
    host,
    port,
    tokenProvider,
    securedConnection,
    checkNotNull(verifySSlCertificate))

  def build(): Client = new ClientImpl(Connection.newConnection(host, port, tokenProvider, securedConnection, verifySSlCertificate), "UTF-8")

  def buildJavaClient(): org.zalando.nakadi.client.java.Client = new org.zalando.nakadi.client.java.ClientImpl(build)

  private def checkNotNull[T](subject: T): T =
    if (Option(subject).isEmpty) throw new NullPointerException else subject

  private def checkState[T](subject: T, predicate: (T) => Boolean, msg: String): T =
    if (predicate(subject)) subject else throw new IllegalStateException()

}