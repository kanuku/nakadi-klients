



import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.Try
import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.scala.model.EventType
import org.zalando.nakadi.client.scala.Client
import org.zalando.nakadi.client.scala.ClientFactory

object CleanupCreatedTestEventTypes extends App {
//  private val log = LoggerFactory.getLogger(ClientFactory.getClass)

  deleteAllEventsCreatedByClientIntegrationTests()

  def deleteAllEventsCreatedByClientIntegrationTests() = {
    val eventNames = List("SimpleEvent", "ClientIntegrationTest", "PaymentCreatedDataChangeEventTest")
    val client = ClientFactory.buildScalaClient()
    val eventsOnNakadi = Await.result(client.getEventTypes(), 25.second)
    val filteredEvents = eventsOnNakadi match {
      case Right(Some(events)) if !events.isEmpty=>
        val result = events.filter(p=> eventNames.exists(s =>p.name.startsWith(s)) && shouldBeRemoved(p.owningApplication) ||shouldBeRemoved(p.owningApplication) )
        println("######################### Number of filtered events to be deleted: "+result.size)
        Try(deleteEventTypesThatStartWith(result, client))
      case left =>
        println("######################### Something went wrong: "+left)
        Nil
    }
    println("######################### Stopping the client")
    client.stop()

  }
  
  def shouldBeRemoved(owningApplication:String)=(owningApplication!=null&&owningApplication.toLowerCase().contains("nakadi-klients"))

  private def deleteEventTypesThatStartWith(events: Seq[EventType], client: Client) = {
    println("######################### Start deleting "+events.size+" events")
         events.map { event =>
          println("######################### Deleting event name: "+event.owningApplication+" owningApplication: " +  event.name)
          Await.result(client.deleteEventType(event.name), 10.second)
        }
    println("######################### Finished deleting events")
  }
}