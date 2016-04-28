package org.zalando.nakadi.client.scala

import scala.collection.immutable.Seq

import akka.http.scaladsl.model.{ ContentType, HttpHeader, HttpMethod, HttpRequest, MediaRange }
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.Uri.apply
import akka.http.scaladsl.model.headers
import akka.http.scaladsl.model.headers.{ OAuth2BearerToken, RawHeader }

//
trait HttpFactory {
  type TokenProvider = () => String
  def withHeaders(params: Option[StreamParameters]): List[HttpHeader] = {
    params match {
      case Some(StreamParameters(cursor, _, _, _, _, _, flowId)) =>
        val nakadiCursor = cursor match {
          case Some(value) if Option(value.partition).isDefined && Option(value.offset).isDefined =>
            ("X-Nakadi-Cursors", Some("[{\"partition\":\"" + value.partition + "\", \"offset\": \"" + value.offset + "\"}]"))
          case None =>
            ("X-Nakadi-Cursors", None)
        }
        val parameters = List(nakadiCursor, ("X-Flow-Id", flowId))
        for { (key, optional) <- parameters; value <- optional } yield RawHeader(key, value)
      case None => Nil
    }
  }
  def withQueryParams(params: Option[StreamParameters]): List[String] = {
    params match {
      case Some(StreamParameters(_, batchLimit, streamLimit, batchFlushTimeout, streamTimeout, streamKeepAliveLimit, _)) =>
        val parameters = List(("batch_limit", batchLimit), ("stream_limit", streamLimit), //
          ("batch_flush_timeout", batchFlushTimeout), ("stream_timeout", streamTimeout), //
          ("stream_keep_alive_limit", streamKeepAliveLimit))
        for { (key, optional) <- parameters; value <- optional } yield (key + "=" + value)
      case None => Nil
    }
  }

  def withHttpRequest(url: String, httpMethod: HttpMethod, additionalHeaders: Seq[HttpHeader], tokenProvider: TokenProvider, params: Option[StreamParameters]): HttpRequest = {
    val allHeaders: Seq[HttpHeader] = additionalHeaders :+ headers.Authorization(OAuth2BearerToken(tokenProvider()))
    val paramsList = withQueryParams(params)
    val urlParams = if (!paramsList.isEmpty) paramsList.mkString("?", "&", "") else ""
    val finalUrl = url + urlParams
    HttpRequest(uri = finalUrl, method = httpMethod).withHeaders(allHeaders)
  }

  def withHttpRequestAndPayload(url: String, entity: String, httpMethod: HttpMethod, tokenProvider: TokenProvider): HttpRequest = {
    HttpRequest(uri = url, method = httpMethod) //
      .withHeaders(headers.Authorization(OAuth2BearerToken(tokenProvider())),
        headers.Accept(MediaRange(`application/json`)))
      .withEntity(ContentType(`application/json`), entity)
  }

}
//
