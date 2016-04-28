package org.zalando.nakadi.client.examples.scala

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.ClientFactory
import com.fasterxml.jackson.databind.util.ISO8601DateFormat

object EventCreationExample extends App {

  
  
  // 1. Create the client

  import org.zalando.nakadi.client.utils.ClientBuilder
  import org.zalando.nakadi.client.scala.Client
  val client: Client = ClientBuilder()
    .withHost("nakadi-sandbox.aruha-test.zalan.do")
    .withSecuredConnection(true) //s
    .withVerifiedSslCertificate(false) //s
    .withTokenProvider(ClientFactory.OAuth2Token()) //
    //    .build();
    .build()

  // 2. Create a simple Meeting Event class
  case class MeetingsEvent(date: String, topic: String) extends Event

  //We define the Json representation of our Meeting Event.
  val schema: String = """
    { 
      "properties": 
      { 
        "date": { "type": "string" }, 
        "topic": { "type": "string"}  
      } 
    }"""

  // 3. Create the EventType, 
  // We need to create an eventType(a topic), where listeners 
  // can subscribe to, to get our published events.
  val eventTypeSchema = new EventTypeSchema(SchemaType.JSON, schema)

  //First the eventType name, wich will be part of the URL: https://nakadi.test.io/event-types/{eventTypeName}
  //See the API for more information on the EventType model
  //https://github.com/zalando/nakadi/blob/nakadi-jvm/api/nakadi-event-bus-api.yaml#L1240
  val eventTypeName = "org.zalando.laas.meetings-1" 

  val owner = "team-laas"
  val category = EventTypeCategory.UNDEFINED // We want just to pass data without through Nakadi, simple schema-validation is enough!
  val validationStrategies = None // Validation strategies are not defined yet!
  val enrichmentStrategies = Nil
  val partitionStrategy = PartitionStrategy.RANDOM
  def paritionKeyFields() = List("date", "topic")

  val eventType = new EventType(eventTypeName,
    owner,
    category,
    validationStrategies,
    enrichmentStrategies,
    Some(partitionStrategy),
    Some(eventTypeSchema),
    None,
    Option(paritionKeyFields()),
    None)

  //You need to import the default Serializer if you don't sepecify your own!
  import JacksonJsonMarshaller._

  client.createEventType(eventType)

  // 4. Publish the EventType

  val event = new MeetingsEvent("2016-04-28T13:28:15+00:00", "Hackaton")
  client.publishEvent(eventTypeName, event)

}