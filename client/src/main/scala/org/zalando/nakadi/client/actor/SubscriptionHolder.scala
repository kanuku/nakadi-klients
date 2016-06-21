package org.zalando.nakadi.client.actor

import org.zalando.nakadi.client.scala.model.Cursor

import akka.actor.ActorRef
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

/**
 * Class that keeps track of the subscriptions
 */
trait SubscriptionHolder {
  import SupervisingActor._
  def addCursor(key: SubscriptionKey, cursor: Option[Cursor]): Unit
  def addSubscription(key: SubscriptionKey, key2: ActorRef, entry: SubscriptionEntry): Unit
  def entry(key: SubscriptionKey): Option[SubscriptionEntry]
  def entryByActor(actor: ActorRef): Option[SubscriptionEntry]
  def cursorByActor(actor: ActorRef): Option[Cursor]
  def unsubscribe(key: SubscriptionKey): Unit
  def activeSize: Int
}

class SubscriptionHolderImpl extends SubscriptionHolder {
  import SupervisingActor._
  private var subscriptions: Map[SubscriptionKey, SubscriptionEntry] = Map() //EventTypeName+Partition
  private var cursors: Map[SubscriptionKey, Option[Cursor]] = Map()
  private var actors: Map[String, SubscriptionKey] = Map()
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def addCursor(key: SubscriptionKey, cursor: Option[Cursor]): Unit = {
    cursors = cursors + ((key, cursor))
  }

  def addSubscription(key: SubscriptionKey, key2: ActorRef, entry: SubscriptionEntry) = {
    subscriptions = subscriptions + ((key, entry))
    actors = actors + ((key2.path.toString(), key))
  }

  def unsubscribe(key: SubscriptionKey): Unit = {
    subscriptions = subscriptions - (key)
  }

  def entry(key: SubscriptionKey): Option[SubscriptionEntry] = {
    subscriptions.get(key)
  }

  def entryByActor(actor: ActorRef): Option[SubscriptionEntry] =
    actors.get(actor.path.toString()).flatMap(x => subscriptions.get(x))

  def activeSize: Int = {
    subscriptions.size
  }

  def addActor(actor: ActorRef, key: SubscriptionKey): Unit = {
    actors = actors + ((actor.path.toString(), key))
  }

  def key(actor: ActorRef): Option[SubscriptionKey] = {
    actors.get(actor.path.toString())
  }

  def cursorByActor(actor: ActorRef): Option[Cursor] = {
    actors.get(actor.path.toString()).flatMap(x => cursors.get(x).flatMap(a => a))
  }

}