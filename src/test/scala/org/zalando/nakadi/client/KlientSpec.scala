package org.zalando.nakadi.client

import java.net.URI
import java.util

import com.fasterxml.jackson.databind.{PropertyNamingStrategy, SerializationFeature, DeserializationFeature, ObjectMapper}
import com.google.common.collect.{Maps, Iterators}
import io.undertow.util.{HttpString, Headers}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import org.zalando.nakadi.client.utils.NakadiTestService
import org.zalando.nakadi.client.utils.NakadiTestService.Builder

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

class KlientSpec extends WordSpec with Matchers with BeforeAndAfterEach {

  var klient: Klient = null
  var service: NakadiTestService = null
  val objectMapper = new ObjectMapper
  objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)

  val MEDIA_TYPE = "application/json"
  val TOKEN = "<OAUTH Token>"
  val HOST = "localhost"
  val PORT = 8081

  override  def beforeEach() = {
    klient = KlientBuilder()
      .withEndpoint(new URI(s"http://$HOST:$PORT"))
      .withTokenProvider(() => "my-token")
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
    authorizationHeaderValue should be(TOKEN)

    request
  }

  "A Klient" must {
    "retrieve Nakadi metrics" in {
      val expectedResponse = Maps.newHashMap[String, Any]

      val getMetricsData = Maps.newHashMap[String, Any]
      getMetricsData.put("calls_per_second", "0.011")
      getMetricsData.put("count", "1")
      var statusCodeMetrics = Maps.newHashMap[String, Any]
      statusCodeMetrics.put("401", "1")
      getMetricsData.put("status_codes", statusCodeMetrics)
      expectedResponse.put("get_metrics", getMetricsData)

      val postEventData = Maps.newHashMap[String, Any]
      postEventData.put("calls_per_second", "0.005")
      postEventData.put("count", "5")
      statusCodeMetrics = Maps.newHashMap[String, Any]
      statusCodeMetrics.put("201", "5")
      postEventData.put("status_codes", statusCodeMetrics)
      expectedResponse.put("post_event", postEventData)

      // ---
      val expectedResponseAsString = objectMapper.writeValueAsString(expectedResponse)
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

      Await.ready(
        klient.getMetrics.map(_ match {
          case Left(error) => fail(s"could not retrieve metrics: $error")
          case Right(metrics) => {
            println(s"metrics => $metrics")
            performStandardRequestChecks(requestPath, requestMethod)
          }
        }),
        20 seconds
      )
    }
  }
}
