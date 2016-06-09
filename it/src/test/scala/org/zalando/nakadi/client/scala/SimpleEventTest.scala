package org.zalando.nakadi.client.scala

import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.zalando.nakadi.client.scala.test.factory.EventIntegrationHelper
import org.zalando.nakadi.client.scala.test.factory.events.SimpleEventListener
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.scala.test.factory.events.MySimpleEvent
import org.zalando.nakadi.client.scala.model.Cursor

class SimpleEventTest extends WordSpec with Matchers {

  import org.scalatest.Matchers._

  import ClientFactory._
  import JacksonJsonMarshaller._
  import MySimpleEvent._

  val client = ClientFactory.getScalaClient()
  
  val eventGenerator = new DefaultMySimpleEventGenerator() {
    def eventTypeId = "SimpleEventIntegrationTest"
  }

  "Create and Receive nr of SimpleEvents" in {

    val it = new EventIntegrationHelper(eventGenerator, client)
    val listener = new SimpleEventListener()
    val nrOfEvents = 4500
    val cursor = Some(Cursor("0", "BEGIN"))

    it.createEventType()
    val events = it.publishEvents(nrOfEvents)

    client.subscribe(eventGenerator.eventTypeName, StreamParameters(cursor = cursor), listener)

    val receivedEvents = listener.waitToReceive(nrOfEvents)

    receivedEvents.size shouldBe events.size
    receivedEvents shouldBe events

  }
}

