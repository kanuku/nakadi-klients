package org.zalando.nakadi.client.utils

import org.zalando.nakadi.client.java.model._
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.java.enumerator._
import org.zalando.nakadi.client.java._
import org.zalando.nakadi.client.java.model.JavaJacksonJsonMarshaller



/**
 * Meant for usage in the Java client side.  
 */
object Serialization {

  def defaultSerializer[T](): Serializer[T] = JavaJacksonJsonMarshaller.serializer[T]
  def metricsDeserializer(): Deserializer[Metrics] = JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.metricsTR)
  def eventTypeDeserializer(): Deserializer[EventType] = JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.eventTypeTR)
  def customDeserializer[T](tr: TypeReference[T]): Deserializer[T] = JavaJacksonJsonMarshaller.deserializer(tr)
  def partitionDeserializer(): Deserializer[Partition] = JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.partitionTR)

  //Sequence
  def seqOfEventTypeDeserializer(): Deserializer[java.util.List[EventType]] = JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfEventTypeTR)
  def seqOfPartitionDeserializer(): Deserializer[java.util.List[Partition]] = JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfPartitionTR)
  def seqOfEventValidationStrategy(): Deserializer[java.util.List[EventValidationStrategy]] = JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfEventValidationStrategyTR)
  def seqOfEventEnrichmentStrategy(): Deserializer[java.util.List[EventEnrichmentStrategy]] = JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfEventEnrichmentStrategyTR)
  def seqOfPartitionStrategy(): Deserializer[java.util.List[PartitionStrategy]] = JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfPartitionStrategyTR)
  
}