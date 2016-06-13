package org.zalando.nakadi.client.scala

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
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.HttpProtocol
import akka.http.scaladsl.model.HttpProtocols
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.utils.Uri
import org.zalando.nakadi.client.handler.SubscriptionHandlerImpl
import org.zalando.nakadi.client.handler.SubscriptionHandler

class ScalaClientTest extends WordSpec with Matchers with MockitoSugar with BeforeAndAfter {
  import JacksonJsonMarshaller._
  import Uri._
  private var connection: Connection = mock[Connection]
  private var subscriber: SubscriptionHandler = mock[SubscriptionHandler]
  private val client: Client = new ClientImpl(connection, subscriber)
  val eventTypeName = "EventTypeName"
  before {
    reset(connection)
  }
  "Scala Client " should {

    "map a 404 to a None" in {
      val headers = Nil
      val entity = HttpEntity(ContentTypes.`application/json`, "{}")
      val response = new HttpResponse(StatusCodes.NotFound, headers, entity, HttpProtocols.`HTTP/1.1`)
      val futureResponse = Future.successful(response)
      when(connection.get(anyString)).thenReturn(futureResponse)
      val result =  Await.result(client.getEventType(eventTypeName),5.seconds)
      result shouldBe Right(None)
    }

    "marshall an object when receiving http a 200 with valid payload" in {
    }

    "catch the marshalling exception when receiving a 200 with invalid payload" in {
    }
    "return a ClientError receiving a redirection (300-399)" in {
    }
  }

}