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
import org.zalando.nakadi.client.java.{ ClientHandler => JClientHandler }
import org.zalando.nakadi.client.java.{ ClientHandlerImpl => JClientHandlerImpl }
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
  case class Subscribe(endpoint: String, cursor: Option[Cursor], handler: EventHandler) extends Subscription
  case class Unsubscribe(handler: EventHandler) extends Subscription

}

class SupervisingActor(val connection: Connection, val subscriptionHandler: SubscriptionHandler) extends Actor with ActorLogging {
  import SupervisingActor._
  import EventConsumingActor._
  type SubscriptionEntry = (EventHandler, ActorRef)
  //Listener ID + Susbscription
  private var handlerMap: Map[String, SubscriptionEntry] = Map()
  private var registrationCounter: AtomicLong = new AtomicLong(0);

  def receive: Receive = {
    case subscrition: Subscribe =>
      log.info("New subscription {}", subscrition)
      subscribe(subscrition)
    case Unsubscribe(handler) =>
  }

  def subscribe(subscribe: Subscribe) = {
    registrationCounter.incrementAndGet()
    val Subscribe(endpoint, cursor, eventHandler) = subscribe
    //Create the Consumer
    val consumingActor = connection.actorSystem.actorOf(Props(classOf[EventConsumingActor], endpoint, null, eventHandler), "EventConsumingActor-" + registrationCounter.get)
    val consumer = ActorSubscriber[ByteString](consumingActor)

    //Create the pipeline
    subscriptionHandler.createPipeline(cursor, consumer, endpoint, eventHandler)
    val subscription = (eventHandler, consumingActor)
    val entry: SubscriptionEntry = (eventHandler, consumingActor)
    handlerMap = handlerMap + ((eventHandler.id(), entry))

    // Notify listener it is subscribed
    eventHandler.handleOnSubscribed(endpoint, cursor)
  }

  def unsubscribe() = {

  }

}