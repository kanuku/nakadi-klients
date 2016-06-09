package org.zalando.nakadi.client.scala.test.factory.events

import org.zalando.nakadi.client.scala.model._
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.scala.model.EventStreamBatch

object MySimpleEvent {
  def schemaDefinition() = """{ "properties": { "order_number": { "type": "string" } } }"""
  implicit def myEventExampleTR: TypeReference[EventStreamBatch[MySimpleEvent]] = new TypeReference[EventStreamBatch[MySimpleEvent]] {}
}

case class MySimpleEvent(orderNumber: String) extends Event

 