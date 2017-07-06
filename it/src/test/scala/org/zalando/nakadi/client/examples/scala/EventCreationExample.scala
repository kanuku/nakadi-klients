package org.zalando.nakadi.client.examples.scala

import org.zalando.nakadi.client.scala.Client
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventType
import org.zalando.nakadi.client.scala.model.EventTypeCategory
import org.zalando.nakadi.client.scala.model.EventTypeSchema
import org.zalando.nakadi.client.scala.model.ScalaJacksonJsonMarshaller
import org.zalando.nakadi.client.scala.model.PartitionStrategy
import org.zalando.nakadi.client.scala.model.SchemaType
import org.zalando.nakadi.client.utils.ClientBuilder
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.collection.mutable.ListBuffer
import org.zalando.nakadi.client.scala.ClientFactory
import org.zalando.nakadi.client.scala.model.EventEnrichmentStrategy
import scala.concurrent.ExecutionContext.Implicits.global

object EventCreationExample extends App {

  // 1. Create the client

  import org.zalando.nakadi.client.utils.ClientBuilder
  import org.zalando.nakadi.client.scala.Client
  val client: Client = ClientFactory.buildScalaClient()

  // 2. Create a simple Meeting Event class
  case class MeetingsEvent(date: String, topic: String) extends Event

  //We define the Json representation of our Meeting Event.
  val schema: String = """
    { 
      'properties': 
      { 
        'date': { 'type': 'string' }, 
        'topic': { 'type': 'string'}  
      } 
    }""".replaceAll("'", "\"")

  // 3. Create the EventType, 
  // We need to create an eventType(a topic), where listeners 
  // can subscribe to, to get our published events.
  val eventTypeSchema = new EventTypeSchema(SchemaType.JSON, schema)

  //First the eventType name, wich will be part of the URL: https://nakadi.test.io/event-types/{eventTypeName}
  //See the API for more information on the EventType model
  //https://github.com/zalando/nakadi/blob/nakadi-jvm/api/nakadi-event-bus-api.yaml#L1240
  //  val eventTypeName = "Example-unique-million-messages"
  val eventTypeName = "idakan-test-event"
  //  val eventTypeName = "Example-unique-hundred-messages-3"

  val owner = "team-laas"
  val category = EventTypeCategory.UNDEFINED // We want just to pass data without through Nakadi, simple schema-validation is enough!
  val enrichmentStrategies = Nil //Seq(EventEnrichmentStrategy.METADATA)
  val partitionStrategy = Some(PartitionStrategy.RANDOM)
  val dataKeyFields = Nil
  val paritionKeyFields = List("date", "topic")

  val eventType = new EventType(eventTypeName,
    owner,
    category,
    enrichmentStrategies,
    partitionStrategy,
    eventTypeSchema,
    dataKeyFields,
    paritionKeyFields,
    None)

  //You need to import the default Serializer if you don't sepecify your own!
  import ScalaJacksonJsonMarshaller._

  client.createEventType(eventType)
  Thread.sleep(10000)
  // 4. Publish the EventType
  //  System.exit(0)
  var counter = 0
  for (n <- 1 to 50) {
    val event = new MeetingsEvent("" + System.currentTimeMillis(), "Hackaton")
    var events = ListBuffer[MeetingsEvent]()
    for (a <- 1 to 10) {
      counter += 1
      events += MeetingsEvent("" + System.currentTimeMillis(),
        "Hackaton" + counter)
    }
    Await.result(client.publishEvents(eventTypeName, events), 10.seconds)
  }
  client.stop()

}
