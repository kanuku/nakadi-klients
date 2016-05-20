package org.zalando.nakadi.client.utils

import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.ClientError
import org.zalando.nakadi.client.scala.Listener
import org.zalando.nakadi.client.scala.StreamParameters
import org.zalando.nakadi.client.java.{ ClientError => JClientError }
import org.zalando.nakadi.client.java.{ StreamParameters => JStreamParameters }
import org.zalando.nakadi.client.java.model.{ Event => JEvent }
import org.zalando.nakadi.client.java.model.{ EventStreamBatch => JEventStreamBatch }
import org.zalando.nakadi.client.java.model.{ Cursor => JCursor }
import java.util.Optional
import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import org.zalando.nakadi.client.scala.model.EventStreamBatch

object ModelConverter {
  import GeneralConversions._

  def toScalaCursor(in: Optional[JCursor]): Option[Cursor] = if (in.isPresent()) {
    toScalaCursor(in.get)
  } else {
    None
  }

  def toScalaCursor(in: JCursor): Option[Cursor] = Option(in) match {
    case None    => None
    case Some(c) => Some(Cursor(c.getPartition, c.getOffset))
  }

  def toScalaStreamParameters(in: Optional[JStreamParameters]): Option[StreamParameters] = if (in.isPresent()) {
    toScalaStreamParameters(in.get)
  } else {
    None
  }

  def toScalaStreamParameters(in: JStreamParameters): Option[StreamParameters] = Option(in) match {
    case None => None
    case Some(c) => Some(StreamParameters(
      cursor = toScalaCursor(c.getCursor),
      batchLimit = toOption(c.getBatchLimit),
      streamLimit = toOption(c.getStreamLimit),
      batchFlushTimeout = toOption(c.getBatchFlushTimeout),
      streamTimeout = toOption(c.getStreamTimeout),
      streamKeepAliveLimit = toOption(c.getStreamKeepAliveLimit),
      flowId = toOption(c.getFlowId)))
  }

  def toJavaCursor(in: Cursor): JCursor = in match {
    case Cursor(partition, offset) =>
      new JCursor(partition, offset)
    case null => null
  }

  def toScalaListener[T <: JEvent](in: org.zalando.nakadi.client.java.Listener[T]): Listener[T] = if (in == null) {
    null
  } else {
    createListenerWrapper(in)
  }

  def toScalaCursor[T <: JEvent](in: JEventStreamBatch[T]): Option[Cursor] = Option(in) match {
    case None     => None
    case Some(in) => toScalaCursor(in.getCursor)
  }

  def toJavaEvents[T <: JEvent](in: JEventStreamBatch[T]): Option[java.util.List[T]] = Option(in) match {
    case None     => None
    case Some(in) => Option(in.getEvents)
  }

  def toJavaClientError(error: Option[ClientError]): Optional[JClientError] = error match {
    case Some(ClientError(msg, httpStatusCodeOpt, exceptionOpt)) => Optional.of(new JClientError(msg, toOptional(httpStatusCodeOpt), toOptional(exceptionOpt)))
    case None => Optional.empty()
  }

  private def createListenerWrapper[T <: org.zalando.nakadi.client.java.model.Event, B <: Event](in: org.zalando.nakadi.client.java.Listener[T]): Listener[T] = {
    new Listener[T] {
      val logger = Logger(LoggerFactory.getLogger(this.getClass))
      def id: String = in.getId
      def onReceive(eventUrl: String, cursor: Cursor, events: Seq[T]): Unit = {
        logger.debug("[ListenerWrapper] cursor {} url {} events {}", cursor, eventUrl, events)
        in.onReceive(eventUrl, toJavaCursor(cursor), seqAsJavaList(events))
      }
      def onError(eventUrl: String, error: Option[ClientError]) = {
        logger.debug("[ListenerWrapper] cursor {} url {} error {}", eventUrl, error)
        in.onError(eventUrl, toJavaClientError(error))
      }
    }

  }
}