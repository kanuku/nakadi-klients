package org.zalando.nakadi.client.actor

import org.zalando.nakadi.client.handler.SubscriptionHandler
import org.zalando.nakadi.client.scala.Connection
import org.zalando.nakadi.client.scala.EventHandler
import org.zalando.nakadi.client.scala.model.Cursor

import SupervisingActor.SubscriptionEntry
import SupervisingActor.SubscriptionKey
import akka.actor.Actor
import akka.actor.ActorInitializationException
import akka.actor.ActorKilledException
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.OneForOneStrategy
import akka.actor.PoisonPill
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.Decider
import akka.actor.SupervisorStrategy.Restart
import akka.actor.SupervisorStrategy._
import akka.actor.Terminated
import akka.actor.actorRef2Scala
import akka.stream.actor.ActorSubscriber
import akka.util.ByteString

object SupervisingActor {
  case class SubscribeMsg(eventTypeName: String, endpoint: String, cursor: Option[Cursor], handler: EventHandler) {
    override def toString(): String =
      s"SubscriptionKey(eventTypeName: $eventTypeName - endpoint: $endpoint - cursor: $cursor - listener: ${handler.id})"
  }
  case class UnsubscribeMsg(eventTypeName: String, partition: Option[String], eventHandlerId: String)
  case class OffsetMsg(cursor: Cursor, subKey: SubscriptionKey)
  case class SubscriptionKey(eventTypeName: String, partition: Option[String]) {
    override def toString(): String = partition match {
      case Some(p) => s"SubscriptionKey(eventTypeName:$eventTypeName - Partition:$p)";
      case None    => s"SubscriptionKey(eventTypeName:$eventTypeName)";
    }
  }
  case class SubscriptionEntry(subuscription: SubscribeMsg, actor: ActorRef)
}

class SupervisingActor(val connection: Connection, val subscriptionHandler: SubscriptionHandler) extends Actor with ActorLogging {
  import SupervisingActor._
  import ConsumingActor._
  val subscriptions: SubscriptionHolder = new SubscriptionHolderImpl()
  var subscriptionCounter = 0;
  override val supervisorStrategy: SupervisorStrategy = {
    def defaultDecider: Decider = {
      case _: ActorInitializationException ⇒ Stop
      case _: ActorKilledException         ⇒ Stop
      case _: IllegalStateException        ⇒ Stop
      case _: Exception                    ⇒ Stop
      case _: Throwable                    ⇒ Stop
    }
    OneForOneStrategy()(defaultDecider)
  }

  def receive: Receive = {
    case OffsetMsg(cursor, subKey) =>
      log.info("Received cursor {} - subKey {}", cursor, subKey)
      subscriptions.addCursor(subKey, Some(cursor))
    case subscrition: SubscribeMsg =>
      log.info("New subscription {}", subscrition)
      subscribe(subscrition)
    case unsubscription: UnsubscribeMsg =>
      log.info("Number of subscriptions before unsubscribing {}", subscriptions.activeSize)
      unsubscribe(unsubscription)
      log.info("Number of subscriptions after unsubscribing {}", subscriptions.activeSize)
    case Terminated(terminatedActor) =>
      log.info(s"ConsumingActor terminated {}", terminatedActor.path.name)
      subscriptions.entryByActor(terminatedActor) match {
        case Some(SubscriptionEntry(SubscribeMsg(eventTypeName, endpoint, Some(Cursor(partition, offset)), handler), actor: ActorRef)) =>
          val unsubscription = UnsubscribeMsg(eventTypeName, Option(partition), handler.id())
          unsubscribe(unsubscription)
          val cursor = subscriptions.cursorByActor(terminatedActor)
          val subscription = SubscribeMsg(eventTypeName, endpoint, cursor, handler)
          subscribe(subscription)
        case Some(SubscriptionEntry(SubscribeMsg(eventTypeName, endpoint, None, handler), actor: ActorRef)) =>
          val unsubscription = UnsubscribeMsg(eventTypeName, None, handler.id())
          unsubscribe(unsubscription)
          val cursor = subscriptions.cursorByActor(terminatedActor)
          val subscription = SubscribeMsg(eventTypeName, endpoint, cursor, handler)
          subscribe(subscription)
        case None =>
          log.warning("Did not find any SubscriptionKey for {}", terminatedActor.path.toString())
        case e =>
          log.warning("None exhaustive match! {}", e)
      }
  }

  def subscribe(subscribe: SubscribeMsg) = {
    subscriptionCounter+=1
    val SubscribeMsg(eventTypeName, endpoint, optCursor, eventHandler) = subscribe
    log.info("Subscription nr {} - cursor {} - eventType {} - listener {}", subscriptionCounter , optCursor, eventTypeName, eventHandler.id())

    val subKey: SubscriptionKey = optCursor match {
      case Some(Cursor(partition, _)) => SubscriptionKey(eventTypeName, Some(partition))
      case None                       => SubscriptionKey(eventTypeName, None)
    }

    //Create the Consumer
    val consumingActor = context.actorOf(Props(classOf[ConsumingActor], subKey, eventHandler), "EventConsumingActor-" + subscriptionCounter)

    context.watch(consumingActor) //If the streaming is ending!!

    val subEntry: SubscriptionEntry = SubscriptionEntry(subscribe, consumingActor)

    // Notify listener it is subscribed
    eventHandler.handleOnSubscribed(endpoint, optCursor)

    //Create the pipeline
    subscriptionHandler.createPipeline(optCursor, consumingActor, endpoint, eventHandler)
    subscriptions.addSubscription(subKey, consumingActor, subEntry)
    subscriptions.addCursor(subKey, optCursor)
  }

  def unsubscribe(unsubscription: UnsubscribeMsg) = {
    val UnsubscribeMsg(eventTypeName, partition, eventHandlerId) = unsubscription
    val key: SubscriptionKey = SubscriptionKey(eventTypeName, partition)
    log.info("Unsubscribe({})  ", unsubscription)
    subscriptions.entry(key) match {
      case Some(SubscriptionEntry(handler, actor)) =>
        log.info("Unsubscribing Listener : {} from actor: {}", handler, actor)
        actor ! PoisonPill
        subscriptions.unsubscribe(key)
      case None =>
        log.warning("Listener not found for {}", unsubscription)
    }
  }

}