package org.zalando.nakadi.client.actor

import akka.actor.ActorLogging
import akka.actor.Actor
import scala.concurrent.duration._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.actor.ActorKilledException
import akka.actor.ActorInitializationException
import akka.actor.SupervisorStrategy._
import akka.actor.AllForOneStrategy
import akka.actor.ActorRef
import org.zalando.nakadi.client.scala.EventHandler
import org.zalando.nakadi.client.handler.SubscriptionHandler
import org.zalando.nakadi.client.scala.Connection
import org.zalando.nakadi.client.handler.SubscriptionHandlerImpl
import akka.actor.ActorLogging
import akka.actor.Actor
import scala.concurrent.duration._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.actor.ActorKilledException
import akka.actor.ActorInitializationException
import akka.actor.SupervisorStrategy._
import akka.actor.AllForOneStrategy
import akka.actor.ActorRef
import org.zalando.nakadi.client.scala.EventHandler
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeoutException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.java.{ JavaClientHandler => JClientHandler }
import org.zalando.nakadi.client.java.{ JavaClientHandlerImpl => JClientHandlerImpl }
import org.zalando.nakadi.client.java.model.{ Cursor => JCursor }
import org.zalando.nakadi.client.java.model.{ Event => JEvent }
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event

import com.typesafe.scalalogging.Logger

import akka.NotUsed
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Terminated
import akka.actor.actorRef2Scala
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.HostConnectionPool
import akka.http.scaladsl.HttpsConnectionContext
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import akka.stream.ActorMaterializerSettings
import akka.stream.OverflowStrategy
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorSubscriber
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Framing
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import akka.http.scaladsl.model.EntityStreamException
import java.util.concurrent.atomic.AtomicLong
import org.zalando.nakadi.client.scala.Connection
import org.zalando.nakadi.client.scala.HttpFactory

object SupervisingActor {
  sealed trait Subscription
  case class Subscribe(eventyTypeName: String, endpoint: String, cursor: Option[Cursor], handler: EventHandler) extends Subscription
  case class Unsubscribe(eventTypeName: String, partition:String, eventHandlerId: String) extends Subscription

}

class SupervisingActor(val connection: Connection, val subscriptionHandler: SubscriptionHandler) extends Actor with ActorLogging {
  import SupervisingActor._
  import ConsumingActor._
  type SubscriptionKey = (String,String) //EventTypeName,PartitionId
  type SubscriptionEntry = (EventHandler, ActorRef)
  private var subscriptions: Map[SubscriptionKey, SubscriptionEntry] = Map()   //SubscriptionKey , SubscriptionEntry

  def receive: Receive = {
    case subscrition: Subscribe =>
      log.info("New subscription {}", subscrition)
      subscribe(subscrition)
    case unsubscription: Unsubscribe =>
      log.info("Number of subscriptions {}", subscriptions.size)
      unsubscribe(unsubscription)
  }

  def subscribe(subscribe: Subscribe) = {
    val Subscribe(eventTypeName, endpoint, cursor, eventHandler) = subscribe
    log.info("Subscription nr {} for eventType {} and listener {}", (subscriptions.size + 1), eventTypeName, eventHandler.id())

    //Create the Consumer
    val consumingActor = context.actorOf(Props(classOf[ConsumingActor], endpoint, eventHandler), "EventConsumingActor-" + subscriptions.size)
    val consumer = ActorSubscriber[ByteString](consumingActor)

    // Notify listener it is subscribed
    eventHandler.handleOnSubscribed(endpoint, cursor)

    //Create the pipeline
    subscriptionHandler.createPipeline(cursor, consumer, endpoint, eventHandler)
    val subscription = (eventHandler, consumingActor)
    val Some(Cursor(partition,_))=cursor
    val key :SubscriptionKey = (eventTypeName,partition)
    val entry: SubscriptionEntry = (eventHandler, consumingActor)
    subscriptions = subscriptions + ((key, entry))
  }

  def unsubscribe(unsubscription: Unsubscribe) = {
    val Unsubscribe(eventTypeName, partition, eventHandlerId) = unsubscription
    val key :SubscriptionKey = (eventTypeName,partition)
    log.info("Unsubscribe({}) for eventType {} and listener {}",subscriptions, eventTypeName, eventHandlerId)
    subscriptions.get(key) match {
      case Some(subscription) =>
        val (handler,actor)= subscription
        log.info("Sending shutdown message to actor: {}", actor)
        actor ! Shutdown(handler)

      case None =>
        log.warning("Listener not found for {}", unsubscription)
    }

  }

}