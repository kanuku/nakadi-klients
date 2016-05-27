package org.zalando.nakadi.client.scala

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.java.model.{ Cursor => JCursor }
import org.zalando.nakadi.client.java.model.{ Event => JEvent }
import org.zalando.nakadi.client.java.model.{ EventStreamBatch => JEventStreamBatch }
import org.zalando.nakadi.client.java.{ Listener => JListener }
import org.zalando.nakadi.client.java.{ ClientError => JClientError }
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventStreamBatch
import org.zalando.nakadi.client.utils.ModelConverter._
import java.util.{ List => JList }
import java.util.Optional
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

/**
 * Internal Models for handling logic between Java and Scala
 */

/**
 * Base result for Deserialization Attempt.
 */
trait Result

private case class JavaResult[J <: JEvent](
  val javaEvents: Option[java.util.List[J]],
  val scalaCursor: Cursor,
  val javaCursor: JCursor) extends Result

private case class ScalaResult[S <: Event](
  scalaEvents: Option[Seq[S]] = None,
  scalaCursor: Cursor) extends Result

case class ErrorResult(error: Throwable) extends Result

trait EventHandler {
  def id(): String
  def handleOnReceive(eventTypeName: String, msg: String): Either[ErrorResult, Cursor]
  def handleOnSubscribed(endpoint: String, cursor: Option[Cursor]): Unit
  def handleOnError(eventTypeName: String, msg: Option[String], exception: Throwable)
}

class EventHandlerImpl[J <: JEvent, S <: Event](
    eitherOfListeners: Either[(Deserializer[JEventStreamBatch[J]], JListener[J]), (Deserializer[EventStreamBatch[S]], Listener[S])]) extends EventHandler {
  import EventHandler._
  private val log = Logger(LoggerFactory.getLogger(this.getClass))
  private def createException(msg: String) = new IllegalStateException(msg)

  lazy val id: String = {

    eitherOfListeners match {
      //Validations
      case Left((des, listener)) if (listener == null)  => throw new IllegalStateException("EventHandler created without a Listener/Deserializer")
      case Right((des, listener)) if (listener == null) => throw new IllegalStateException("EventHandler created without a Listener/Deserializer")
      //Real handling 
      case Left((_, listener))                          => listener.getId
      case Right((_, listener))                         => listener.id
    }
  }

  def handleOnReceive(eventTypeName: String, msg: String): Either[ErrorResult, Cursor] = {
    eitherOfListeners match {
      case Left((des, listener)) => // Java
        transformJava(msg, des).right.flatMap {
          case JavaResult(Some(events), sCursor, jCursor) =>
            listener.onReceive(eventTypeName, jCursor, events)
            Right(sCursor)
          case JavaResult(None, sCursor, jCursor) =>
            Right(sCursor)
          case _ =>
            val errorMsg = s"Could not handle JAVA Transformation url [$eventTypeName] listener [${listener.getId}] msg [$msg]"
            log.error(errorMsg)
            listener.onError(errorMsg, Optional.empty())
            Left(ErrorResult(createException(errorMsg)))
        }
      case Right((des, listener)) => //Scala
        transformScala(msg, des).right.flatMap {
          case ScalaResult(Some(events), cursor) =>
            listener.onReceive(eventTypeName, cursor, events)
            Right(cursor)
          case ScalaResult(None, cursor) =>
            Right(cursor)
          case _ =>
            val errorMsg = s"Could not handle SCALA Transformation url [$eventTypeName] listener [${listener.id}] msg [$msg]"
            listener.onError(errorMsg, None)
            log.error(errorMsg)
            Left(ErrorResult(createException(errorMsg)))
        }
    }
  }

  def handleOnError(eventTypeName: String, msg: Option[String], exception: Throwable) = {
    val errorMsg = if (msg.isDefined) msg.get else exception.getMessage
    val clientError = Some(ClientError(errorMsg, exception = Some(exception)))
    eitherOfListeners match {
      case Left((des, listener)) => // Java
        listener.onError(errorMsg, toJavaClientError(clientError))
      case Right((des, listener)) => //Scala
        listener.onError(errorMsg, clientError)
    }
  }
  def handleOnSubscribed(endpoint: String, cursor: Option[Cursor]): Unit = eitherOfListeners match {
    case Left((des, listener)) =>
      val res: Optional[JCursor] = toJavaCursor(cursor)
      listener.onSubscribed(endpoint, res) // Java
    case Right((des, listener)) => listener.onSubscribed(endpoint, cursor)
  }

}

private object EventHandler {
  def transformScala[S <: Event](message: String, des: Deserializer[EventStreamBatch[S]]): Either[ErrorResult, ScalaResult[S]] = Try(des.from(message)) match {
    case Success(EventStreamBatch(cursor, events)) => Right(ScalaResult(events, cursor))
    case Failure(err)                              => Left(ErrorResult(err))
  }

  def transformJava[J <: JEvent](message: String, des: Deserializer[JEventStreamBatch[J]]): Either[ErrorResult, JavaResult[J]] = {
    Try(des.from(message)) match {
      case Success(eventBatch) =>
        val events = if (eventBatch.getEvents != null && !eventBatch.getEvents.isEmpty()) Some(eventBatch.getEvents) else None
        val Some(sCursor: Cursor) = toScalaCursor(eventBatch.getCursor)
        val jcursor: JCursor = eventBatch.getCursor
        Right(JavaResult(events, sCursor, jcursor))
      case Failure(err) =>
        Left(ErrorResult(err))
    }
  }
}