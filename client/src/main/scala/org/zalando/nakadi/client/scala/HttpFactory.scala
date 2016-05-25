package org.zalando.nakadi.client.scala

import scala.collection.immutable.Seq
import akka.http.scaladsl.model.{ ContentType, HttpHeader, HttpMethod, HttpRequest, MediaRange }
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.Uri.apply
import akka.http.scaladsl.model.headers
import akka.http.scaladsl.model.headers.{ OAuth2BearerToken, RawHeader }
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.headers.{ Accept, RawHeader }
import akka.http.scaladsl.model.MediaTypes.`application/json`
import org.zalando.nakadi.client.scala.model.Cursor
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

//
object HttpFactory {
  /**
   * Alias for a function that returns a string.
   */
  type TokenProvider = () => String

  def withUrl(url: String, params: Option[StreamParameters]) = {
    val paramsList = withQueryParams(params)
    val urlParams = if (!paramsList.isEmpty) paramsList.mkString("?", "&", "") else ""
    url + urlParams
  }

  def withHttpRequest(url: String, httpMethod: HttpMethod, additionalHeaders: Seq[HttpHeader], tokenProvider: Option[TokenProvider], params: Option[StreamParameters]): HttpRequest = {
    val allHeaders: Seq[HttpHeader] = tokenProvider match {
      case None        => additionalHeaders
      case Some(token) => additionalHeaders :+ headers.Authorization(OAuth2BearerToken(token()))
    }
    val paramsList = withQueryParams(params)
    HttpRequest(uri = url, method = httpMethod).withHeaders(allHeaders)
  }

  def withHttpRequestAndPayload(url: String, entity: String, httpMethod: HttpMethod, tokenProvider: Option[TokenProvider]): HttpRequest = {
    val request = HttpRequest(uri = url, method = httpMethod)
    tokenProvider match {
      case None => request.withHeaders(headers.Accept(MediaRange(`application/json`)))
        .withEntity(ContentType(`application/json`), entity)
      case Some(token) => request.withHeaders(headers.Authorization(OAuth2BearerToken(token())),
        headers.Accept(MediaRange(`application/json`)))
        .withEntity(ContentType(`application/json`), entity)
    }
  }

  def withHttpRequest(url: String, cursor: Option[Cursor], flowId: Option[String], tokenProvider: Option[TokenProvider]): HttpRequest = {
    val customHeaders = withDefaultHeaders(cursor, flowId) :+ RawHeader("Accept", "application/x-json-stream")
    val allHeaders = tokenProvider match {
      case None        => customHeaders
      case Some(token) => customHeaders :+ headers.Authorization(OAuth2BearerToken(token()))
    }

    HttpRequest(uri = url, method = HttpMethods.GET).withHeaders(allHeaders)
  }
  private def withQueryParams(params: Option[StreamParameters]): Seq[String] = {
    params match {
      case Some(StreamParameters(_, batchLimit, streamLimit, batchFlushTimeout, streamTimeout, streamKeepAliveLimit, _)) =>
        val parameters = List(("batch_limit", batchLimit), ("stream_limit", streamLimit), //
          ("batch_flush_timeout", batchFlushTimeout), ("stream_timeout", streamTimeout), //
          ("stream_keep_alive_limit", streamKeepAliveLimit))
        for { (key, optional) <- parameters; value <- optional } yield (key + "=" + value)
      case None => Nil
    }
  }

  private def withDefaultHeaders(cursor: Option[Cursor], flowId: Option[String]): Seq[HttpHeader] = {
    val nakadiCursor = cursor match {
      case Some(value) if Option(value.partition).isDefined && Option(value.offset).isDefined =>
        ("X-Nakadi-Cursors", Some("[{\"partition\":\"" + value.partition + "\", \"offset\": \"" + value.offset + "\"}]"))
      case None =>
        ("X-Nakadi-Cursors", None)
    }
    val parameters = List(nakadiCursor, ("X-Flow-Id", flowId))
    for { (key, optional) <- parameters; value <- optional } yield RawHeader(key, value)
  }

}
