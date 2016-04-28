package org.zalando.nakadi.client.utils



/**
 * Meant for usage in the Java client side, because Java just cannot(meme:badPokerface) handle implicits.  
 */
object Serialization {
  
  
//	def defaultSerializer[T]():Serializer[T] = JacksonJsonMarshaller.serializer[T]
//  def metricsDeserializer():Deserializer[Metrics] = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.metricsTR)
//  def eventTypeDeserializer():Deserializer[EventType] = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.eventTypeTR)
//  def customDeserializer[T](tr:TypeReference[T]):Deserializer[T] = JacksonJsonMarshaller.deserializer(tr)
//  def partitionDeserializer():Deserializer[Partition] = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.partitionTR)
//  
//  //Sequence
//  def seqOfEventTypeDeserializer(): Deserializer[Seq[EventType]] = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.listOfEventTypeTR)
//  def seqOfPartitionDeserializer():Deserializer[Seq[Partition]]  = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.listOfPartitionTR)
//  def seqOfEventValidationStrategy():Deserializer[Seq[EventValidationStrategy]] = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.listOfEventValidationStrategyTR)
//  def seqOfEventEnrichmentStrategy():Deserializer[Seq[EventEnrichmentStrategy]] = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.listOfEventEnrichmentStrategyTR)
//  def seqOfPartitionStrategy():Deserializer[Seq[PartitionStrategy]] = JacksonJsonMarshaller.deserializer(JacksonJsonMarshaller.listOfPartitionStrategyTR)
}