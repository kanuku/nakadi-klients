package org.zalando.nakadi.client.utils

import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.model.Metrics
import org.zalando.nakadi.client.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.model.EventType
import org.zalando.nakadi.client.Serializer


/**
 * Meant for usage in the client side 
 */
object Serialization {
  def metricsDeserializer():Deserializer[Metrics] = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.metricsTR)
  def seqOfEventTypeDeserializer(): Deserializer[Seq[EventType]] = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.listOfEventTypeTR)
  def eventTypeSerializer():Serializer[EventType] = JacksonJsonMarshaller.serializer
}