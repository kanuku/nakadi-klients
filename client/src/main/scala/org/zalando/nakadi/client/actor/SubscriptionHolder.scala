package org.zalando.nakadi.client.actor

import org.zalando.nakadi.client.scala.model.Cursor

import akka.actor.ActorRef
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import com.google.common.base.Preconditions

/**
 * Tracks subscriptions of SubscriptionEntries and Cursors.
 */
trait SubscriptionHolder {
  import SupervisingActor._
  def addCursor(key: SubscriptionKey, cursor: Option[Cursor]): Unit
  def add(key: SubscriptionKey, key2: ActorRef, entry: SubscriptionEntry): Unit
  def entry(key: SubscriptionKey): Option[SubscriptionEntry]
  def entryByActor(actor: ActorRef): Option[SubscriptionEntry]
  def cursorByActor(actor: ActorRef): Option[Cursor]
  def remove(key: SubscriptionKey): Unit
  def size: Int
}

class SubscriptionHolderImpl extends SubscriptionHolder {
  import SupervisingActor._
  import Preconditions._
  private var subscriptions: Map[SubscriptionKey, SubscriptionEntry] = Map() //EventTypeName+Partition
  private var cursors: Map[SubscriptionKey, Option[Cursor]] = Map()
  private var actors: Map[String, SubscriptionKey] = Map()
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def addCursor(key: SubscriptionKey, cursor: Option[Cursor]): Unit = {
    cursors = cursors + ((key, cursor))
  }

  def add(key: SubscriptionKey, key2: ActorRef, entry: SubscriptionEntry) = {
    checkNotNull(key)
    checkNotNull(key2)
    checkNotNull(entry)
    subscriptions = subscriptions + ((key, entry))
    actors = actors + ((key2.path.toString(), key))
  }

  def remove(key: SubscriptionKey): Unit = {
    removeCursor(key)

    entry(key) match {
      case None =>
        logger.warn(s"Subscription [$key] not found!")
      case Some(SubscriptionEntry(subscription, actor)) => {
        removeSubscriptionKey(actor)
        subscriptions = subscriptions - (key)
        logger.info(s"Removed subscription [$subscription] for key [$key]!")
      }
    }
  }

  private def removeCursor(key: SubscriptionKey): Unit = cursors.get(key) match {
    case None => logger.warn(s"Cursor subscription for $key not found!")
    case Some(cursor) =>
      cursors = cursors - (key)
      logger.info(s"Removed cursor [$cursor] with subscription [$key]!")
  }

  private def removeSubscriptionKey(actor: ActorRef): Unit = actors.get(actor.path.toString()) match {
    case None => logger.warn(s"SubscriptionKey for actor [${actor.path.toString()}] not found!")
    case Some(key) =>
      actors = actors - actor.path.toString()
      logger.info(s"Removed subscriptionKey [$key] for actor [${actor.path.toString()}]!")
  }

  def entry(key: SubscriptionKey): Option[SubscriptionEntry] = {
    subscriptions.get(key)
  }

  def entryByActor(actor: ActorRef): Option[SubscriptionEntry] =
    actors.get(actor.path.toString()).flatMap(x => subscriptions.get(x))

  def size: Int = {
    subscriptions.size
  }

  def addActor(actor: ActorRef, key: SubscriptionKey): Unit = {
    actors = actors + ((actor.path.toString(), key))
  }

  def cursorByActor(actor: ActorRef): Option[Cursor] = {
    actors.get(actor.path.toString()).flatMap(x => cursors.get(x).flatMap(a => a))
  }

}