package org.zalando.nakadi.client.scala.utils

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.mockito.Matchers.any
import org.mockito.Matchers.anyString
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar
import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import spray.json.JsonFormat
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.HttpProtocol
import akka.http.scaladsl.model.HttpProtocols
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller

class ModelConverterTest extends WordSpec with Matchers with MockitoSugar {
  "Conversions" should {

    "be implemented" in {
//      fail()
    }
  }

}