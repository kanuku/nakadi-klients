package org.zalando.nakadi.client

import java.net.URI
import java.util

import com.fasterxml.jackson.databind.{PropertyNamingStrategy, SerializationFeature, DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.common.collect.{Maps, Iterators}
import com.typesafe.scalalogging.LazyLogging
import io.undertow.util.{HttpString, Headers}
import org.scalatest.{Failed, BeforeAndAfterEach, Matchers, WordSpec}
import org.zalando.nakadi.client.utils.NakadiTestService
import org.zalando.nakadi.client.utils.NakadiTestService.Builder

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

class KlientSpec extends WordSpec with Matchers with BeforeAndAfterEach with LazyLogging {

  var klient: Klient = null
  var service: NakadiTestService = null
  val objectMapper = new ObjectMapper
  objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
  objectMapper.registerModule(new DefaultScalaModule)

  val MEDIA_TYPE = "application/json"
  val TOKEN = "<OAUTH Token>"
  val HOST = "localhost"
  val PORT = 8081

  override  def beforeEach() = {
    klient = KlientBuilder()
      .withEndpoint(new URI(s"http://$HOST:$PORT")) // TODO if no scheme is specified, the library utilized by the client breaks with a NullpointeException...
      .withTokenProvider(() => TOKEN)
      .build()
  }

  override def afterEach(): Unit = {
    klient.stop()

    if(Option(service).isDefined) {
      service.stop()
      service = null
    }
  }

  private def performStandardRequestChecks(expectedRequestPath: String, expectedRequestMethod: HttpString) = {
    val collectedRequestsMap = service.getCollectedRequests
    val requests = collectedRequestsMap.get(expectedRequestPath)
    requests should not be null

    val request = Iterators.getLast(requests.iterator)
    request.getRequestPath should be(expectedRequestPath)
    request.getRequestMethod should be(expectedRequestMethod)

    val headerMap = request.getRequestHeaders

    var headerValues= headerMap.get(Headers.CONTENT_TYPE)
    val mediaType= headerValues.getFirst
    mediaType should be(MEDIA_TYPE)

    headerValues = headerMap.get(Headers.AUTHORIZATION)
    val authorizationHeaderValue = headerValues.getFirst
    authorizationHeaderValue should be(s"Bearer $TOKEN")

    request
  }

  "A Klient" must {
    "retrieve Nakadi metrics" in {
      val expectedResponse = Map("post_event" -> Map("calls_per_second" -> "0.005",
                                                      "count" -> "5",
                                                      "status_codes" -> Map("201" -> 5)),
                                  "get_metrics" -> Map("calls_per_second" -> "0.001",
                                                       "count" -> "1",
                                                       "status_codes" -> Map("401" -> 1)))

      // ---
      val expectedResponseAsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedResponse)
      val requestMethod = new HttpString("GET")
      val requestPath = "/metrics"
      val responseStatusCode: Int = 200

      val builder= new Builder
      service = builder.withHost(HOST)
                       .withPort(PORT)
                       .withHandler(requestPath)
                       .withRequestMethod(requestMethod)
                       .withResponseContentType(MEDIA_TYPE)
                       .withResponseStatusCode(responseStatusCode)
                       .withResponsePayload(expectedResponseAsString)
                       .build
      service.start

      Await.result(
        klient.getMetrics,
        5 seconds
      ) match {
        case Left(error) => fail(s"could not retrieve metrics: $error")
        case Right(metrics) => {
          logger.debug(s"metrics => $metrics")
          performStandardRequestChecks(requestPath, requestMethod)
        }
      }
    }

    "retrieve Nakadi topics" in {
      val expectedResponse = List(Topic("test-topic-1"), Topic("test-topic-2"))


      val expectedResponseAsString = objectMapper.writeValueAsString(expectedResponse)
      val requestMethod = new HttpString("GET")
      val requestPath = "/topics"
      val responseStatusCode = 200

      val builder = new Builder
      service = builder.withHost(HOST)
                       .withPort(PORT)
                       .withHandler(requestPath)
                       .withRequestMethod(requestMethod)
                       .withResponseContentType(MEDIA_TYPE)
                       .withResponseStatusCode(responseStatusCode)
                       .withResponsePayload(expectedResponseAsString)
                       .build
      service.start

      Await.result(
        klient.getTopics,
        10 seconds
      ) match {
          case Left(error) => fail(s"could not retrieve topics: $error")
          case Right(topics) => {
            logger.info(s"topics => $topics")
            topics should be(expectedResponse)
            performStandardRequestChecks(requestPath, requestMethod)
          }
        }
    }

    "post events to Nakadi topics" in {
      val event = Event("http://test.zalando.net/my_type",
                        "ARTICLE:123456",
                         Map("tenant-id" -> "234567",
                             "flow-id" -> "123456789" ),
                         Map("greeting" -> "hello",
                             "target" -> "world"))


      val topic = "test-topic-1"
      val requestMethod = new HttpString("POST")
      val requestPath = s"/topics/$topic/events"
      val responseStatusCode = 201

      val builder = new NakadiTestService.Builder
      service = builder.withHost(HOST)
                       .withPort(PORT)
                       .withHandler(requestPath)
                       .withRequestMethod(requestMethod)
                       .withResponseContentType(MEDIA_TYPE)
                       .withResponseStatusCode(responseStatusCode)
                       .withResponsePayload("")
                       .build
      service.start

      Await.result(
        klient.postEvent(topic, event),
        10 seconds
      ) match {
        case Some(error) => fail(s"an error occurred while posting event to topic $topic")
        case None => logger.debug("event post request was successful")
      }

      val request = performStandardRequestChecks(requestPath, requestMethod)
      val sentEvent = objectMapper.readValue(request.getRequestBody, classOf[Event])
      sentEvent should be(event)
    }

    "retreive partitions of a topic" in {
      val expectedPartitions = List(TopicPartition("111", "0", "0"), TopicPartition("222", "0", "1"))
      val expectedResponse = objectMapper.writeValueAsString(expectedPartitions)


      val topic = "test-topic-1"
      val requestMethod = new HttpString("GET")
      val requestPath = s"/topics/$topic/partitions"
      val responseStatusCode = 200

      val builder = new Builder
      service = builder.withHost(HOST)
                       .withPort(PORT)
                       .withHandler(requestPath)
                       .withRequestMethod(requestMethod)
                       .withResponseContentType(MEDIA_TYPE)
                       .withResponseStatusCode(responseStatusCode)
                       .withResponsePayload(expectedResponse)
                       .build
      service.start

      val receivedPartitions = Await.result(klient.getPartitions(topic), 10 seconds) match {
        case Left(error: String) => throw new RuntimeException(s"could not retrieve partitions: $error")
        case Right(partitions) => partitions
      }
      receivedPartitions should be(expectedPartitions)
      performStandardRequestChecks(requestPath, requestMethod)
    }
  }
}
