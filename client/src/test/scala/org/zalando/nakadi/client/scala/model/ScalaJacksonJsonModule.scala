package org.zalando.nakadi.client.scala.model

import org.scalatest.Matchers
import org.scalatest.WordSpec
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.DeserializationFeature

case class Example private (enrichment_strategies: Seq[String])

class ScalaJacksonJsonModuleTest extends WordSpec with Matchers {
  val emptyJsonList = """{"enrichment_strategies": []}"""
  val emptyJson = """{}"""
  val mapper = JacksonJsonMarshaller.defaultObjectMapper

  "Deserialize from empty array to Nil  " in {
    val actual = mapper.readValue(emptyJsonList, classOf[Example])
    println(actual)
    actual shouldBe Example(Nil)
  }
  "Deserialize from null to null" in {
    val actual = mapper.readValue(emptyJson, classOf[Example])
    println(actual)
    actual shouldBe Example(null)
  }

}