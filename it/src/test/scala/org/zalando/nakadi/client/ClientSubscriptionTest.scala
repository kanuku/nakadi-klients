package org.zalando.nakadi.client
import org.scalatest.{ Matchers, WordSpec }
import org.zalando.nakadi.client.scala.model._
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.scala.ModelFactory
import org.zalando.nakadi.client.scala.EventTypesActions
import org.zalando.nakadi.client.scala.ClientFactory
import org.zalando.nakadi.client.scala.EventActions
import org.zalando.nakadi.client.scala.StreamParameters
import org.zalando.nakadi.client.scala.Listener
import org.zalando.nakadi.client.scala.ClientError

class ClientSubscriptionTest extends WordSpec with Matchers with ModelFactory {
  import ClientFactory._
  import JacksonJsonMarshaller._
  case class MyEventExample(orderNumber: String) extends Event
  implicit def myEventExampleTR: TypeReference[EventStreamBatch[MyEventExample]] = new TypeReference[EventStreamBatch[MyEventExample]] {}
  val eventAction = new EventActions(client)
  val eventTypeAction = new EventTypesActions(client)
  "POST/PUT/GET/DELETE single EventType " in {

    val events = for {
      a <- 0 to 4005
    } yield MyEventExample("order-" + a)

    val listener = new Listener[MyEventExample] {
      def id: String = "test"
      def onError(sourceUrl: String, cursor: Cursor, error: ClientError): Unit = {
        println("YOOOOOOOOOOOOOO ")
      }
      def onSubscribed(): Unit = ???
      def onUnsubscribed(): Unit = ???
      def onReceive(sourceUrl: String, cursor: Cursor, event: Seq[MyEventExample]): Unit = ???
    }
    val url = "/event-types/test-client-integration-event-1936085527-148383828851369665/events"
    val eve = "test-client-integration-event-1936085527-148383828851369665"
    val params = new StreamParameters()
    client.subscribe(eve, params, listener)
    //    eventAction.create("test-client-integration-event-1936085527-148383828851369665",  List(MyEventExample("test-1")))
    //    eventAction.create("test-client-integration-event-1936085527-148383828851369665", events)
    //    while(true){
    //      
    //    }

  }

  "Create" in {
    val events = for {
      a <- 0 to 4005
    } yield MyEventExample("order-" + a)
    //    eventAction.create("test-client-integration-event-1936085527-148383828851369665",  List(MyEventExample("test-1")))
    eventAction.create("test-client-integration-event-1936085527-148383828851369665", events)
  }

}


