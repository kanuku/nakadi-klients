

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.scala.Client
import org.zalando.nakadi.client.scala.ClientError
import org.zalando.nakadi.client.scala.Listener
import org.zalando.nakadi.client.scala.StreamParameters
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventStreamBatch
import org.zalando.nakadi.client.utils.ClientBuilder

import com.fasterxml.jackson.core.`type`.TypeReference

class VoidListener(val id: String) extends Listener[VoidEvent] {
  val log = LoggerFactory.getLogger(this.getClass)

  def onError(sourceUrl: String, error: Option[ClientError]): Unit = {
    println("Error %s %s".format(sourceUrl, error))
  }

  def onReceive(sourceUrl: String,
                cursor: Cursor,
                events: Seq[VoidEvent]): Unit = {
    //    println("################# start onReceive ##############")
    //    println(s"Has a total of ${events.size} events")
//    Thread.sleep(2000)
    println(s"Event: $id - cursor $cursor")
    //    println("################## onReceive ###################")

  }
  def onSubscribed(endpoint: String, cursor: Option[Cursor]): Unit = {
    println("########## start onSubscribed ############")
    println("Endpoint " + endpoint)
    println("Cursor " + cursor)
    println("########## onSubscribed  #################")

  }
}

case class VoidEvent(body: Map[String, Any]) extends Event

object Whip extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)
  private val token = ""
  val events = Map(
    ("DE.ZALANDO.LOGISTICS.ALE.BAGPACK.PACK_ITEM_SCANNED.ver_1", "BEGIN"),
    ("order-creator-test.shipment-order-created.zalando", "BEGIN"),
    ("DE.ZALANDO.LOGISTICS.WHIP.WMO.CREATED.ver_1", "BEGIN"),
    ("fulfillment-order-service.shipment-order-created", "BEGIN"),
    ("DE.ZALANDO.LOGISTICS.WHIP.COMMISSION_LIVE.COMMISSION_UPDATED.ver_3", "BEGIN"))
  val eventNames = events.map(_._1).toList

  val client: Client = new ClientBuilder() //
    .withHost("")
    //    .withHost("")
    .withTokenProvider(Option(() => token))
    .withSecuredConnection(true) //
    .withVerifiedSslCertificate(false) //
    .withPort(443) //
    .build()
  init()

  def init() = {
    //    val eventTypes = client.getEventTypes().filter {
    //      case Right(Some(events)) => events.filter(et=>eventNames.contains(et.name))
    //      case _  => false
    //    }

    logger.info("Starting the client")
    logger.info("########## start subscriptions ############")
    implicit def typeRef: TypeReference[EventStreamBatch[VoidEvent]] = new TypeReference[EventStreamBatch[VoidEvent]] {}
    import org.zalando.nakadi.client.scala.model.ScalaJacksonJsonMarshaller._
    println("Nr of Events" + events.size)
    for (info <- events) {
      println("Subscribing: " + info._1)
      val listener: Listener[VoidEvent] = new VoidListener(info._1)
            client.subscribe(info._1, StreamParameters(cursor = Some(Cursor("0", info._2)),batchLimit=Some(10)), listener)
    }
  }

}