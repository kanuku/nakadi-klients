package org.zalando.nakadi.client.scala
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.PartitionStrategyType
import org.zalando.nakadi.client.scala.test.factory.EventIntegrationHelper
import org.zalando.nakadi.client.scala.test.factory.events.MySimpleEvent
import org.zalando.nakadi.client.scala.test.factory.events.SimpleEventListener
import org.zalando.nakadi.client.scala.model.PartitionStrategy
import org.scalatest.BeforeAndAfter
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.scalatest.BeforeAndAfterAll

class ClientIntegrationTest extends WordSpec with Matchers with BeforeAndAfterAll {

  import org.scalatest.Matchers._
  import ClientFactory._
  import MySimpleEvent._

  val client = ClientFactory.getScalaClient()
  val eventGenerator = new DefaultMySimpleEventGenerator() {
    def eventTypeId = s"ClientIntegrationTest-Scala"
  }

  override def afterAll {
    client.stop()
  }

  "POST/GET/DELETE /event-types" in {
    val eventType = eventGenerator.eventType
    //POST
    val creationResult = Await.result(client.createEventType(eventType), 10.seconds)
    creationResult shouldBe None

    //GET
    var eventClientResult = Await.result(client.getEventType(eventType.name), 10.seconds)
    eventClientResult.isRight shouldBe true
    var Right(eventTypeOpt) = eventClientResult
    eventTypeOpt.isDefined shouldBe true
    val Some(eventTypeResult) = eventTypeOpt

    //    Compare EventType
    eventType.category shouldBe eventTypeResult.category
    eventType.dataKeyFields shouldBe List()
    eventType.name shouldBe eventTypeResult.name
    eventType.owningApplication shouldBe eventTypeResult.owningApplication
    eventType.partitionStrategy shouldBe eventTypeResult.partitionStrategy
    eventType.schema shouldBe eventTypeResult.schema
    eventType.statistics shouldBe eventTypeResult.statistics
    eventType.enrichmentStrategies shouldBe eventTypeResult.enrichmentStrategies
    eventType.partitionKeyFields shouldBe eventTypeResult.partitionKeyFields

    //DELETE
    val deletedResult = Await.result(client.deleteEventType(eventType.name), 10.seconds)
    deletedResult.isEmpty shouldBe true

    //GET
    eventClientResult = Await.result(client.getEventType(eventType.name), 10.seconds)
    eventClientResult.isRight shouldBe true

    eventClientResult.right.get.isDefined shouldBe false

  }

  "GET /metrics" in {
    val result = Await.result(client.getMetrics(), 10.seconds)
    result.isRight shouldBe true
    val Right(metricsOpt) = result
    metricsOpt.isDefined shouldBe true
    val Some(metrics) = metricsOpt
    println(metrics)
    metrics.version shouldNot be(null)
    metrics.gauges.size should be > 0
  }

  "GET /event-types" in {
    val result = Await.result(client.getEventTypes(), 10.seconds)
    result.isRight shouldBe true
    val Right(eventTypesOpt) = result
    eventTypesOpt.isDefined shouldBe true
    val Some(eventTypes) = eventTypesOpt
    eventTypes.size should (equal(0) or (be > 0))
  }

  "GET /registry/partitions-strategies" in {
    val result = Await.result(client.getPartitioningStrategies(), 10.seconds)
    result.isRight shouldBe true
  }

  "GET /registry/enrichment-strategies" in {
    val result = Await.result(client.getEnrichmentStrategies(), 10.seconds)
    result.isRight shouldBe true
    val Right(strategies) = result
    strategies.isDefined shouldBe true
  }

}

