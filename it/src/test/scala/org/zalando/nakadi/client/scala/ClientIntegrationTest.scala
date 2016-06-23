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
  val nrOfEvents = 45
  val listener = new SimpleEventListener()

  override def afterAll {
    client.stop()
  }

  "GET /metrics" in {
    val result = Await.result(client.getMetrics(), 10.seconds)
    result.isRight shouldBe true
    val Right(metricsOpt) = result
    metricsOpt.isDefined shouldBe true
    val Some(metrics) = metricsOpt
    metrics.version shouldNot be (null)
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

}

