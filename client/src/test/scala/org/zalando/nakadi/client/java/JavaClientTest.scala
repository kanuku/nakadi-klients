package org.zalando.nakadi.client.java

import org.scalatest.BeforeAndAfter
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.zalando.nakadi.client.handler.SubscriptionHandler
import akka.http.scaladsl.model.HttpResponse
import org.zalando.nakadi.client.scala.Connection
import org.scalatest.mock.MockitoSugar._
import scala.concurrent.Future
import org.mockito.Mockito.reset
import org.mockito.Mockito.when
import org.mockito.Matchers.anyString
import java.util.Optional
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.HttpProtocols
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes

class JavaClientTest extends WordSpec with Matchers with BeforeAndAfter {
  val connection = mock[Connection]
  val subscriptionHandler = mock[SubscriptionHandler]
  val clientHandler = new JavaClientHandlerImpl(connection, subscriptionHandler)
  val client = new ClientImpl(clientHandler)
  val eventTypeName = "EventTypeName"

  before {
    reset(connection, subscriptionHandler)
  }

  "Java Client" should {

    "map a 404 to an Empty Optional" in {
      val headers = Nil
      val entity = HttpEntity(ContentTypes.`application/json`, "{}")
      val response = new HttpResponse(StatusCodes.NotFound, headers, entity, HttpProtocols.`HTTP/1.1`)
      val futureResponse = Future.successful(response)
      when(connection.get(anyString)).thenReturn(futureResponse)
      val result = client.getEventType(eventTypeName).get
      result shouldBe Optional.empty()
    }
  }
}