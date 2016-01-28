package org.zalando.nakadi.client.scoop

import com.typesafe.scalalogging.Logger
import de.zalando.scoop.ScoopClient
import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.{Cursor, Event, Listener}

object ScoopListenerWrapper {
   def apply(scoopClient: ScoopClient, scoopTopic: String, listener: Listener) =
                                                              new ScoopListenerWrapper(scoopClient, scoopTopic, listener)
}

protected class ScoopListenerWrapper private (val scoopClient: ScoopClient,
                                              val scoopTopic: String,
                                              val listener: Listener) extends Listener {

  val logger = Logger(LoggerFactory.getLogger("ScoopListenerWrapper"))

  val ID = "id" // TODO make attribute configurable

  override def id: String = listener.id

  override def onReceive(topic: String, partition: String, cursor: Cursor, event: Event): Unit = {
    logger.debug("received [topic={}, partition={}, cursor={}, event={}]", topic, partition, cursor, event)

    event.metadata.get(ID) match {
      case Some(id: String) => {
        if(scoopClient.isHandledByMe(id)) {
          logger.debug("event [{}=id] is handled by me", ID)
          listener.onReceive(topic, partition, cursor, event)
        }
        else logger.debug("event [{}={}] is NOT handled by me", ID, id)
      }
      case Some(unknown) =>
                    logger.warn("[{}={}] is NOT a String -> there must be configuration problem -> ignored",
                                ID, unknown.toString)

      case None => logger.warn("[identifier={}] is not set in [event={}] -> passed to listener", ID)
                   listener.onReceive(topic, partition, cursor, event)
    }
  }

  override def onConnectionOpened(topic: String, partition: String): Unit = listener.onConnectionOpened(topic, partition)

  override def onConnectionClosed(topic: String, partition: String, lastCursor: Option[Cursor]): Unit =
                                                               listener.onConnectionClosed(topic, partition, lastCursor)

  override def onConnectionFailed(topic: String, partition: String, status: Int, error: String): Unit =
                                                            listener.onConnectionFailed(topic, partition, status, error)
}


