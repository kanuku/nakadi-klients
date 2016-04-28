package org.zalando.nakadi.client.subscription

import org.zalando.nakadi.client.scala.ClientFactory
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.model.Event

object Client extends App {
 val client = new LeClient
 client.send(100)
}

class LeClient  {
  import ClientFactory._
  case class MyEventExample(orderNumber: String) extends Event
  implicit def myEventExampleTR: TypeReference[MyEventExample] = new TypeReference[MyEventExample] {}
  import JacksonJsonMarshaller._
  val eventType = "test-client-integration-event-1936085527-148383828851369665"
  def send(in: Int) {
    val events = for {
      a <- 1 to in
    } yield MyEventExample("order-" + a)

    client.publishEvents(eventType, events)
  }
}