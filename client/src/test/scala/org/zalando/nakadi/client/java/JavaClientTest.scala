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
import org.mockito.Matchers.any
import org.mockito.Matchers.anyString
import org.mockito.Matchers.anyObject
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.mockito.Matchers.anyString
import java.util.Optional
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.HttpProtocols
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.java.model._
import org.zalando.nakadi.client.scala.model.{ Cursor => SCursor }
import org.zalando.nakadi.client.java._
import org.mockito.ArgumentCaptor
import org.zalando.nakadi.client.scala.EventHandler
import org.scalatest.mock.MockitoSugar

class JavaClientTest extends WordSpec with Matchers with BeforeAndAfter with MockitoSugar {
  val connection = mock[Connection]
  val subscriptionHandler = mock[SubscriptionHandler]
  val clientHandler = new JavaClientHandlerImpl(connection, subscriptionHandler)
  val client = new ClientImpl(clientHandler)
  val eventTypeName = "EventTypeName"
  val listener = mock[Listener[Event]]
  val deserialzer = mock[Deserializer[EventStreamBatch[Event]]]

  before {
    reset(connection, subscriptionHandler)
  }

  "Java Client" should {

    "Map a 404 to an empty optional" in {
      val headers = Nil
      val entity = HttpEntity(ContentTypes.`application/json`, "{}")
      val response = new HttpResponse(StatusCodes.NotFound, headers, entity, HttpProtocols.`HTTP/1.1`)
      val futureResponse = Future.successful(response)
      when(connection.get(anyString)).thenReturn(futureResponse)

      //For an event
      val result1 = client.getEventType(eventTypeName).get
      result1 shouldBe Optional.empty()

      //For enrichment strategies
      val result2 = client.getEnrichmentStrategies().get
      result2 shouldBe Optional.empty()

    }

    "Pass the streamParameters correctly to the subscriptionHandler" in {
      val partition = "partition"
      val offset = "81237"

      val cursor = Optional.of(new Cursor(partition, offset))
      val batchLimit: Optional[Integer] = Optional.of(7162);
      val streamLimit: Optional[Integer] = Optional.of(817625);
      val batchFlushTimeout: Optional[Integer] = Optional.of(92837);
      val streamTimeout: Optional[Integer] = Optional.of(22871);
      val streamKeepAliveLimit: Optional[Integer] = Optional.of(9928378);
      val flowId: Optional[String] = Optional.of("X-Flow-ID");

      val parameters = new StreamParameters(
        cursor,
        batchLimit,
        streamLimit,
        batchFlushTimeout,
        streamTimeout,
        streamKeepAliveLimit,
        flowId)

      when(subscriptionHandler.subscribe(anyString, anyString, any[Option[SCursor]], any[EventHandler])).thenReturn(None)

      client.subscribe[Event](eventTypeName, parameters, listener, deserialzer) shouldBe Optional.empty()

      val eventTypeNameCap = ArgumentCaptor.forClass(classOf[String])
      val endpointCap = ArgumentCaptor.forClass(classOf[String])
      val cursorCap = ArgumentCaptor.forClass(classOf[Option[SCursor]])
      val eventHandlerCap = ArgumentCaptor.forClass(classOf[EventHandler])

      verify(subscriptionHandler, times(1)).subscribe(
        eventTypeNameCap.capture(),
        endpointCap.capture(),
        cursorCap.capture(),
        eventHandlerCap.capture())

      eventTypeNameCap.getValue shouldBe eventTypeName
      cursorCap.getValue shouldBe Some(SCursor(partition, offset))
      val urlResult = "/event-types/EventTypeName/events?batch_limit=%s&stream_limit=%s&batch_flush_timeout=%s&stream_timeout=%s&stream_keep_alive_limit=%s"
        .format(batchLimit.get,
          streamLimit.get,
          batchFlushTimeout.get,
          streamTimeout.get,
          streamKeepAliveLimit.get)
      endpointCap.getValue shouldBe urlResult

    }
  }
}