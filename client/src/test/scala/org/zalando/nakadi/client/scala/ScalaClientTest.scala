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
import org.zalando.nakadi.client.scala.model.ScalaJacksonJsonMarshaller
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala._
import org.zalando.nakadi.client.utils.Uri
import org.zalando.nakadi.client.handler.SubscriptionHandlerImpl
import org.zalando.nakadi.client.handler.SubscriptionHandler
import org.mockito.ArgumentCaptor
import org.zalando.nakadi.client.Deserializer

class ScalaClientTest extends WordSpec with Matchers with MockitoSugar with BeforeAndAfter {
  import ScalaJacksonJsonMarshaller._
  import Uri._
  private var connection: Connection = mock[Connection]
  private var subscriptionHandler: SubscriptionHandler = mock[SubscriptionHandler]
  private val client: Client = new ClientImpl(connection, subscriptionHandler)
  private val listener = mock[Listener[Event]]
  private val deserialzer = mock[Deserializer[EventStreamBatch[Event]]]

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
      val result = Await.result(client.getEventType(eventTypeName), 5.seconds)
      result shouldBe Right(None)
    }

    "Pass the streamParameters correctly to the subscriptionHandler" in {
      val partition = "partition"
      val offset = "81237"

      val cursor = Some(Cursor(partition, offset))
      val batchLimit:Option[Integer] = Some(7162);
      val streamLimit:Option[Integer] = Some(817625);
      val batchFlushTimeout:Option[Integer] = Some(92837);
      val streamTimeout:Option[Integer] = Some(22871);
      val streamKeepAliveLimit:Option[Integer] = Some(9928378);
      val flowId = Some("X-Flow-ID");

      when(subscriptionHandler.subscribe(anyString, anyString, any[Option[Cursor]], any[EventHandler])).thenReturn(None)
      val eventTypeNameCap = ArgumentCaptor.forClass(classOf[String])
      val endpointCap = ArgumentCaptor.forClass(classOf[String])
      val cursorCap = ArgumentCaptor.forClass(classOf[Option[Cursor]])
      val eventHandlerCap = ArgumentCaptor.forClass(classOf[EventHandler])

      val parameters = StreamParameters(
        cursor,
        batchLimit,
        streamLimit,
        batchFlushTimeout,
        streamTimeout,
        streamKeepAliveLimit,
        flowId)

       client.subscribe[Event](eventTypeName, parameters, listener)(deserialzer) shouldBe None

      verify(subscriptionHandler, times(1)).subscribe(
        eventTypeNameCap.capture(),
        endpointCap.capture(),
        cursorCap.capture(),
        eventHandlerCap.capture())

      eventTypeNameCap.getValue shouldBe eventTypeName
      cursorCap.getValue shouldBe Some(Cursor(partition, offset))
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