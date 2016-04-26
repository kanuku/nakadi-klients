package org.zalando.nakadi.client.scala

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.headers.Accept

import java.security.SecureRandom
import java.security.cert.X509Certificate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import akka.actor.ActorSystem
import akka.actor.Terminated
import akka.http.scaladsl.Http
import akka.http.scaladsl.HttpsConnectionContext
import akka.http.scaladsl.model.ContentType
import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.MediaRange
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.Uri.apply
import akka.http.scaladsl.model.headers
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import akka.http.scaladsl.model.HttpHeader
import scala.collection.immutable.Seq
import org.zalando.nakadi.client.StreamParameters

//
trait HttpFactory {
  type TokenProvider = () => String
  def withHeaders(params: Option[StreamParameters]): List[HttpHeader] = {
    params match {
      case Some(StreamParameters(cursor, _, _, _, _, _, flowId)) =>
        val c = if (cursor.isDefined) Option("[{partition: " + cursor.get.partition + ", offset: " + cursor.get.offset + "}]") else None
        val parameters = List(("X-Nakadi-Cursors", c), ("X-Flow-Id", flowId))
        for { (key, optional) <- parameters; value <- optional } yield RawHeader(key, value)
      case None => Nil
    }
  }
  def withQueryParams(params: Option[StreamParameters]): List[(String, Any)] = {
    params match {
      case Some(StreamParameters(_, batchLimit, streamLimit, batchFlushTimeout, streamTimeout, streamKeepAliveLimit, _)) =>
        val parameters = List(("batch_limit", batchLimit), ("stream_limit", streamLimit), //
          ("batch_flush_timeout", batchFlushTimeout), ("stream_timeout", streamTimeout), //
          ("stream_keep_alive_limit", streamKeepAliveLimit))
        for { (key, optional) <- parameters; value <- optional } yield (key -> value)
      case None => Nil
    }
  }

  def withHttpRequest(url: String, httpMethod: HttpMethod, additionalHeaders: Seq[HttpHeader], tokenProvider: TokenProvider): HttpRequest = {
    val allHeaders: Seq[HttpHeader] = additionalHeaders :+  headers.Authorization(OAuth2BearerToken(tokenProvider()))
    HttpRequest(uri = url, method = httpMethod).withHeaders(allHeaders)
  }

  def withHttpRequestAndPayload(url: String, entity: String, httpMethod: HttpMethod, tokenProvider: TokenProvider): HttpRequest = {
    HttpRequest(uri = url, method = httpMethod) //
      .withHeaders(headers.Authorization(OAuth2BearerToken(tokenProvider())),
        headers.Accept(MediaRange(`application/json`)))
      .withEntity(ContentType(`application/json`), entity)
  }

}
//