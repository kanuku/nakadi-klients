package org.zalando.nakadi.client

import java.io.IOException
import java.net.URI

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import com.fasterxml.jackson.databind.{PropertyNamingStrategy, SerializationFeature, DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaModule}
import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.slf4j.LoggerFactory


class KlientBuilder(val endpoint: URI = null, val tokenProvider: () => String = null, val objectMapper: ObjectMapper = null)
  extends LazyLogging
{
  private def checkNotNull[T](subject: T): T =
                                   if(Option(subject) == None) throw new NullPointerException else subject


  private def checkState[T](subject: T, predicate: (T) => Boolean, msg: String): T =
                                   if(predicate(subject)) subject else throw new IllegalStateException()


  def withEndpoint(endpoint: URI): KlientBuilder =
                                new KlientBuilder(checkNotNull(endpoint), tokenProvider, objectMapper)


  def withTokenProvider(tokenProvider: () => String): KlientBuilder =
                                new KlientBuilder(endpoint, checkNotNull(tokenProvider), objectMapper)


  def withObjectMapper(objectMapper: ObjectMapper): KlientBuilder =  {
    checkNotNull(objectMapper)
    objectMapper.registerModule(new DefaultScalaModule)
    new KlientBuilder(endpoint, tokenProvider, objectMapper)
  }


  def defaultObjectMapper: ObjectMapper = {
    val mapper = new ObjectMapper
    mapper.registerModule(new DefaultScalaModule)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    mapper.addHandler(new DeserializationProblemHandler() {
      @Override
      @throws(classOf[IOException])
      def handleUnknownProperty(ctxt: Nothing, jp: JsonParser, deserializer: Nothing, beanOrClass: Nothing, propertyName: Nothing): Boolean = {
        logger.warn(s"unknown property occurred in JSON representation: [beanOrClass=$beanOrClass, property=$propertyName]", beanOrClass, propertyName)
        return true
      }
    })
    mapper
  }


  def build(): Klient = new KlientImpl(
                  checkState(endpoint,      (s: URI) => Option(s) != None, "endpoint is not set -> try withEndpoint()"),
                  checkState(tokenProvider, (s: () => String) => Option(s) != None, "tokenProvider is not set -> try withTokenProvider()"),
                  Option(objectMapper).getOrElse(defaultObjectMapper)
  )

  
  def buildJavaClient(): Client = new JavaClientImpl(build())


  override def toString = s"KlientBuilder($endpoint, $tokenProvider, $objectMapper)"
}
