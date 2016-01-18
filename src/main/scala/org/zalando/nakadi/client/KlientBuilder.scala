package org.zalando.nakadi.client

import java.net.URI

import com.fasterxml.jackson.databind.ObjectMapper

/**
private   var tokenProvider : Nothing = null
    private   var objectMapper : Nothing = null
    private   var scoop : Nothing = null
    private   var scoopTopic : Nothing = null
  */
class KlientBuilder(val endpoint: URI = null, tokenProvider: () => String) {

  private def checkNotNull[T](subject: T): T =
      if(Option(subject) == None) throw new NullPointerException else subject

  private def checkState[T](subject: T, predicate: (T) => Boolean, msg: String): T =
      if(predicate(subject)) subject else throw new IllegalStateException()

  def withEndpoint(endpoint: URI): KlientBuilder = new KlientBuilder(checkNotNull(endpoint), tokenProvider)

  def withTokenProvider(tokenProvider: () => String): KlientBuilder =
                                                                new KlientBuilder(endpoint, checkNotNull(tokenProvider))

  def build(): Klient = new KlientImpl(
                  checkState(endpoint,      (s: URI) => Option(s) != None, "endpoint is not set -> try withEndpoint()"),
                  checkState(tokenProvider, (s: () => String) => Option(s) != None, "tokenProvider is not set -> try withTokenProvider()"),
                new ObjectMapper() // TODO
  )

  override def toString = s"KlientBuilder($endpoint)"
}
