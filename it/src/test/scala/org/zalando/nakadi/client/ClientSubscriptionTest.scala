package org.zalando.nakadi.client
import org.scalatest.{ Matchers, WordSpec }
import org.zalando.nakadi.client.model._
import com.fasterxml.jackson.core.`type`.TypeReference

class ClientSubscriptionTest extends WordSpec with Matchers with JacksonJsonMarshaller with ModelFactory with ClientFactory {

  case class MyEventExample(orderNumber: String)
  implicit def myEventExampleTR: TypeReference[MyEventExample] = new TypeReference[MyEventExample] {}
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
      def onReceive(sourceUrl: String, cursor: Cursor, event: MyEventExample): Unit = ???
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
    } yield MyEventExample("order-"+a)
//    eventAction.create("test-client-integration-event-1936085527-148383828851369665",  List(MyEventExample("test-1")))
    eventAction.create("test-client-integration-event-1936085527-148383828851369665", events)
  }

}


