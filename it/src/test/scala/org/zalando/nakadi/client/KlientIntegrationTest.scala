package org.zalando.nakadi.client

import org.scalatest.Matchers
import org.scalatest.WordSpec
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.zalando.nakadi.client.model.SprayJsonMarshaller
import scala.concurrent.Future
import org.zalando.nakadi.client.model._
import scala.util.Random

class KlientIntegrationTest extends WordSpec with Matchers with SprayJsonMarshaller {

  val host = ""
  val OAuth2Token = () => ""
  val port = 443
  val client = new ClientImpl(Connection.newConnection(host, port, OAuth2Token, true, false), "UTF-8")

  //Defaults
  val partitionStrategy = new PartitionResolutionStrategy("hash", None)
  val paritionKeyFields = List("order_number")
  val schemaDefinition = """{ "properties": { "order_number": { "type": "string" } } }"""
  val eventTypeSchema = new EventTypeSchema(SchemaType.JSON, schemaDefinition)

  "Nakadi Client" should {
    "parse multiple PartitionResolutionStrategy" in {
      val Right(result) = executeCall(client.partitionStrategies())
      result.size should be > 0
    }
    "Event POST/GET/DELETE" in {
      val random = Random.nextInt()
      val name = "test-client-integration"+random
      val team = "laas-team"
      val eventType = new EventType(name, team, EventTypeCategory.BUSINESS,
        None, None, partitionStrategy, Option(eventTypeSchema), Option(paritionKeyFields), None, None)
      val creationResult = executeCall(client.newEventType(eventType))
      creationResult.isDefined shouldBe false
      //Now get it back
      val Right(Some(result)) = executeCall(client.eventType(name))
      result == eventType

    }
    "parse multiple EventType" in {
      val eventType = new EventType("nakadi-client-test",
        "laas-team",
        EventTypeCategory.UNDEFINED, None, None,
        partitionStrategy,
        None,
        None,
        None,
        None)
      client.newEventType(eventType)
      val Right(result) = Await.result(client.eventTypes(), 10.second)
      result.size should be > 20
    }

    def executeCall[T](call: => Future[T]): T = {
      Await.result(call, 10.second)
    }

  }

}



