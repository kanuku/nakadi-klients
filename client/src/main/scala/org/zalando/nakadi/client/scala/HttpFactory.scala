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
trait HttpFactory {
  type TokenProvider = () => String
  def withHeaders(params: Option[StreamParameters]): Seq[HttpHeader] = {
    params match {
      case Some(StreamParameters(cursor, _, _, _, _, _, flowId)) =>
        withDefaultHeaders(cursor, flowId)
      case None => Nil
    }
  }
  def withQueryParams(params: Option[StreamParameters]): Seq[String] = {
    params match {
      case Some(StreamParameters(_, batchLimit, streamLimit, batchFlushTimeout, streamTimeout, streamKeepAliveLimit, _)) =>
        val parameters = List(("batch_limit", batchLimit), ("stream_limit", streamLimit), //
          ("batch_flush_timeout", batchFlushTimeout), ("stream_timeout", streamTimeout), //
          ("stream_keep_alive_limit", streamKeepAliveLimit))
        for { (key, optional) <- parameters; value <- optional } yield (key + "=" + value)
      case None => Nil
    }
  }
  
  def withUrl(url:String, params: Option[StreamParameters])={
       val paramsList = withQueryParams(params)
    val urlParams = if (!paramsList.isEmpty) paramsList.mkString("?", "&", "") else ""
    url + urlParams
  }

  def withHttpRequest(url: String, httpMethod: HttpMethod, additionalHeaders: Seq[HttpHeader], tokenProvider: TokenProvider, params: Option[StreamParameters]): HttpRequest = {
    val allHeaders: Seq[HttpHeader] = additionalHeaders :+ headers.Authorization(OAuth2BearerToken(tokenProvider()))
    val paramsList = withQueryParams(params)
    
    HttpRequest(uri = url, method = httpMethod).withHeaders(allHeaders)
  }

  def withHttpRequestAndPayload(url: String, entity: String, httpMethod: HttpMethod, tokenProvider: TokenProvider): HttpRequest = {
    HttpRequest(uri = url, method = httpMethod) //
      .withHeaders(headers.Authorization(OAuth2BearerToken(tokenProvider())),
        headers.Accept(MediaRange(`application/json`)))
      .withEntity(ContentType(`application/json`), entity)
  }

  def withDefaultHeaders(cursor: Option[Cursor], flowId: Option[String]): Seq[HttpHeader] = {
    val nakadiCursor = cursor match {
      case Some(value) if Option(value.partition).isDefined && Option(value.offset).isDefined =>
        ("X-Nakadi-Cursors", Some("[{\"partition\":\"" + value.partition + "\", \"offset\": \"" + value.offset + "\"}]"))
      case None =>
        ("X-Nakadi-Cursors", None)
    }
    val parameters = List(nakadiCursor, ("X-Flow-Id", flowId))
    for { (key, optional) <- parameters; value <- optional } yield RawHeader(key, value)
  }

  def withHttpRequest(url: String, cursor: Option[Cursor], flowId: Option[String], tokenProvider: () => String): HttpRequest = {
    println("############### URL %s cursor %s".format(url,cursor))
    val customHeaders = withDefaultHeaders(cursor, flowId) :+ RawHeader("Accept", "application/x-json-stream")
    val tokenHeader = headers.Authorization(OAuth2BearerToken(tokenProvider()))
    val allHeaders: Seq[HttpHeader] = customHeaders :+ tokenHeader
    HttpRequest(uri = url, method = HttpMethods.GET).withHeaders(allHeaders)
  }

}
//
