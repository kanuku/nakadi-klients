package org.zalando.nakadi.client.scala

import scala.reflect._
import scala.reflect.runtime.universe._
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import org.zalando.nakadi.client.scala.model.DataOperation
import org.zalando.nakadi.client.scala.model.BatchItemStep
import org.zalando.nakadi.client.scala.model.BatchItemPublishingStatus
import org.zalando.nakadi.client.scala.model.EventTypeCategory
import org.zalando.nakadi.client.scala.model.SchemaType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.scala.model.EventEnrichmentStrategy
import org.zalando.nakadi.client.scala.model.EventEnrichmentStrategyType

//case object EnumModule extends SimpleModule() {
//  //  addSerializer(ser)
//}
//
//object FunninessLevel extends Enumeration {
//  type FunninessLevel = Value
//  val LOL = Value("LOL")
//  val ROFL = Value("ROFL")
//  val LMAO = Value("LMAO")
//}
//
//class EnumSerializer extends JsonSerializer[EventEnrichmentStrategy.Value] {
//  def serialize(id: EventEnrichmentStrategy.Value, json: JsonGenerator, provider: SerializerProvider) = {
//    json.writeString(id.toString())
//  }
//}
//class EnumDeserializer extends JsonDeserializer[EventEnrichmentStrategy.Value] {
//  def deserialize(jp: JsonParser, ctxt: DeserializationContext): EventEnrichmentStrategy.Value = {
//    EventEnrichmentStrategy.withName(jp.getText())
//  }
//}
//
//object Test extends App {
//  import JacksonJsonMarshaller._
//
//  val mapper = defaultObjectMapper
//
//  case class EnumHolder(@JsonScalaEnumeration(classOf[EventEnrichmentStrategyType]) humor: Seq[EventEnrichmentStrategy.Value])
//  val holder = EnumHolder(List(EventEnrichmentStrategy.METADATA))
//  val json = """["metadata_enrichment"]"""
//  println("#### EnumHolder  toJson #####" + mapper.writeValueAsString(holder))
//  println("#### EnumHolder  toEnum #####" + mapper.readValue(json, new TypeReference[EventEnrichmentStrategy.Value]{}))
//
//}


  
