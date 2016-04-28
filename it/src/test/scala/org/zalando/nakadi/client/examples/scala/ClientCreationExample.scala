package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.model.JacksonJsonMarshaller
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.zalando.nakadi.client.utils.ClientBuilder




object ClientCreationExample extends App {
  import JacksonJsonMarshaller._
  val a = ClientBuilder()
    .withHost("nakadi-sandbox.aruha-test.zalan.do")
    .withSecuredConnection(true) //s
    .withVerifiedSslCertificate(false) //s
    .withTokenProvider(ClientFactory.OAuth2Token()) //
    //    .build();
    .build()
  val eventTypeName = "test-client-integration-event-1936085527-148383828851369665"
  val url = "/event-types/test-client-integration-event-1936085527-148383828851369665/events"
  val result = Await.result(a.getPartitions(eventTypeName), 5.seconds)
  println("########################################")
  println("" + result)
  println("########################################")
}