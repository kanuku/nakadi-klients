package org.zalando.nakadi.client.scala

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.java.model.{Cursor => JCursor}
import org.zalando.nakadi.client.java.model.{Event => JEvent}
import org.zalando.nakadi.client.java.model.{EventStreamBatch => JEventStreamBatch}
import org.zalando.nakadi.client.java.{Listener => JListener}
import org.zalando.nakadi.client.java.{ClientError => JClientError}
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventStreamBatch
import org.zalando.nakadi.client.utils.ModelConverter._
import java.util.{List => JList}
import java.util.Optional
import org.slf4j.LoggerFactory
import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments
import com.google.common.base.Preconditions._

case class ErrorResult(error: Throwable)

/**
  * EventHandlers should handle the logic of deserializing the messages and acting op on this attempt.
  */
trait EventHandler {
  def id(): String
  def handleOnReceive(eventTypeName: String, msg: String): Either[ErrorResult, Cursor]
  def handleOnSubscribed(endpoint: String, cursor: Option[Cursor]): Unit
  def handleOnError(eventTypeName: String, msg: Option[String], exception: Option[Throwable])
}

class ScalaEventHandlerImpl[S <: Event](des: Deserializer[EventStreamBatch[S]], listener: Listener[S])
    extends EventHandler {
  private val log = LoggerFactory.getLogger(this.getClass)
  private def createException(msg: String) =
    new IllegalStateException(msg)
  checkArgument(listener != null, "Listener must not be null", null)
  checkArgument(des != null, "Deserializer must not be null", null)

  def id: String = listener.id

  def handleOnReceive(eventTypeName: String, msg: String): Either[ErrorResult, Cursor] = {
    Try(des.from(msg)) match {
      case Success(EventStreamBatch(cursor, None)) =>
        Right(cursor)
      case Success(EventStreamBatch(cursor, Some(Nil))) =>
        Right(cursor)
      case Success(EventStreamBatch(cursor, Some(events))) =>
        listener.onReceive(eventTypeName, cursor, events)
        Right(cursor)
      case Failure(err) =>
        val errorMsg =
          s"Could not deserialize[Scala] url [$eventTypeName] listener [${listener.id}] msg [$msg] error[${err.getMessage}]"
        log.error(errorMsg)
        listener.onError(errorMsg, None)
        Left(ErrorResult(createException(errorMsg)))

    }

  }

  def handleOnError(eventTypeName: String, msg: Option[String], exception: Option[Throwable]) = {
    val errorMsg    = if (msg.isDefined) msg.get else if(exception.isDefined) exception.get.getMessage else ""
    val clientError = Some(ClientError(errorMsg, exception = exception))
    listener.onError(errorMsg, clientError)
  }
  def handleOnSubscribed(endpoint: String, cursor: Option[Cursor]): Unit =
    listener.onSubscribed(endpoint, cursor)

}
class JavaEventHandlerImpl[J <: JEvent](des: Deserializer[JEventStreamBatch[J]], listener: JListener[J])
    extends EventHandler {
  private val log = LoggerFactory.getLogger(this.getClass)
  private def createException(msg: String) =
    new IllegalStateException(msg)
  checkArgument(listener != null, "Listener must not be null", null)
  checkArgument(des != null, "Deserializer must not be null", null)

  def id: String = listener.getId

  def handleOnReceive(eventTypeName: String, msg: String): Either[ErrorResult, Cursor] = {
    Try(des.from(msg)) match {
      case Success(eventBatch) if (eventBatch.getEvents != null && !eventBatch.getEvents.isEmpty()) =>
        val Some(sCursor: Cursor) = toScalaCursor(eventBatch.getCursor)
        val jcursor: JCursor      = eventBatch.getCursor
        listener.onReceive(eventTypeName, jcursor, eventBatch.getEvents)
        Right(sCursor)
      case Success(eventBatch) =>
        val Some(sCursor: Cursor) = toScalaCursor(eventBatch.getCursor)
        Right(sCursor)
      case Failure(err) =>
        Left(ErrorResult(err))
        val errorMsg =
          s"Could not deserialize[Java] url [$eventTypeName] listener [${listener.getId}] msg [$msg] error[${err.getMessage}]"
        log.error(errorMsg)
        listener.onError(errorMsg, Optional.empty())
        Left(ErrorResult(createException(errorMsg)))
    }
  }

  def handleOnError(eventTypeName: String, msg: Option[String], exception: Option[Throwable]) = {
    val errorMsg    = if (msg.isDefined) msg.get else if(exception.isDefined) exception.get.getMessage else ""
    val clientError = Some(ClientError(errorMsg, None, exception))
    listener.onError(errorMsg, toJavaClientError(clientError))
  }
  def handleOnSubscribed(endpoint: String, cursor: Option[Cursor]): Unit = {
    val res: Optional[JCursor] = toJavaCursor(cursor)
    listener.onSubscribed(endpoint, res)
  }

}
