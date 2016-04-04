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
  val host = "nakadi-sandbox.aruha-test.zalan.do"
  val OAuth2Token = () => "41c7e243-fe44-4aa9-9df1-e63a5de8aed6"
  val port = 443
  val client = new ClientImpl(Connection.newConnection(host, port, OAuth2Token, true, false), "UTF-8")
  
  //Defaults
  val partitionStrategy = new PartitionResolutionStrategy("hash", None)
  val paritionKeyFields = List("order_number")
  val schemaDefinition = """{ "properties": { "order_number": { "type": "string" } } }"""
  val eventTypeSchema = new EventTypeSchema(SchemaType.JSON, schemaDefinition)

  val actions = new EventTypesActions(client)

  "Nakadi Client" should {
    "parse multiple PartitionResolutionStrategy" in {
      val Right(result) = executeCall(client.partitionStrategies())
      result.size should be > 0
    }
    
     

  }
  private def executeCall[T](call: => Future[T]): T = {
    Await.result(call, 10.second)
  }
}



