package org.zalando.nakadi.client

import org.scalatest.Matchers
import org.scalatest.WordSpec
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.zalando.nakadi.client.model.SprayJsonMarshaller
import scala.concurrent.Future
import org.zalando.nakadi.client.model._

class KlientIntegrationTest extends WordSpec with Matchers with SprayJsonMarshaller {

  val host = "nakadi-sandbox.aruha-test.zalan.do"
  val OAuth2Token = () => "42b2f4f8-4052-4fca-916e-972f573b1c52"
  val port = 443
  val client = new ClientImpl(Connection.newConnection(host, port, OAuth2Token, true, false),"UTF-8")
  
  //Defaults
  val partitionStrategy = new PartitionResolutionStrategy("hash",None)
  
  
  "Nakadi Client" should {
    "parse multiple PartitionResolutionStrategy" in {
      val Right(result) = executeCall(client.partitionStrategies())
      result.size should be >0 
    }
    "parse multiple EventType" in {
      val eventType=new EventType("nakadi-client-test",
          "laas-team",
          EventTypeCategory.UNDEFINED,None,None,
          partitionStrategy,
          None,
          None,
          None,
          None
      )
      client.newEventType(eventType)
      val Right(result) = Await.result(client.eventTypes(), 10.second)
          result.size should be >20 
    }
    
    def executeCall[T](call: =>Future[T]):T={
      Await.result(call, 10.second)
    }

  }

}



