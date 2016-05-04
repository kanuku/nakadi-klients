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
import org.zalando.nakadi.client.utils.ModelConverter
import java.util.{ List => JList }
import java.util.Optional
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

private object EventHandler {
  import ModelConverter._
  def transformScala[S <: Event](message: String, des: Deserializer[EventStreamBatch[S]]): Either[ErrorResult, ScalaResult[S]] = Try(des.from(message)) match {
    case Success(eventBatch) =>
      eventBatch match {
        case EventStreamBatch(cursor, events) => Right(ScalaResult(events, Some(cursor)))
      }
    case Failure(err) => Left(ErrorResult(err))
  }
  def transformJava[J <: JEvent](message: String, des: Deserializer[JEventStreamBatch[J]]): Either[ErrorResult, JavaResult[J]] = {
    Try(des.from(message)) match {
      case Success(eventBatch) =>
        val events = if (eventBatch.getEvents != null && !eventBatch.getEvents.isEmpty()) Some(eventBatch.getEvents) else None
        val sCursor = toScalaCursor(eventBatch.getCursor)
        val jcursor = Option(eventBatch.getCursor)
        Right(JavaResult(events, sCursor, jcursor))
      case Failure(err) =>
        Left(ErrorResult(err))
    }
  }
}
trait Result

case class JavaResult[J <: JEvent](
  val javaEvents: Option[java.util.List[J]],
  val scalaCursor: Option[Cursor],
  val javaCursor: Option[JCursor]) extends Result

case class ScalaResult[S <: Event](
  scalaEvents: Option[Seq[S]] = None,
  scalaCursor: Option[Cursor]) extends Result
case class ErrorResult(error: Throwable) extends Result

class EventHandler[J <: JEvent, S <: Event](java: Option[(Deserializer[JEventStreamBatch[J]], JListener[J])], scala: Option[(Deserializer[EventStreamBatch[S]], Listener[S])]) {
  import EventHandler._
  val log = Logger(LoggerFactory.getLogger(this.getClass))

  private def createException(msg: String) = new IllegalStateException(msg)
  lazy val id: String = {
    (java, scala) match {
      case (Some((_, listener)), _) => listener.getId
      case (_, Some((_, listener))) => listener.id
      case _                        => "COULD NOT BE DEFINED"
    }
  }
  def handle(url: String, msg: String): Either[ErrorResult, Option[Cursor]] = {
    //    log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    //    log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    //    log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    //    log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    //    log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    //    log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    (java, scala) match {
      case (Some((des, listener)), _) => // Java
        transformJava(msg, des).right.flatMap {
          case JavaResult(Some(events), Some(sCursor), Some(jCursor)) =>
            log.debug("RECEIVED SOMETHING 1")
            listener.onReceive(url, jCursor, events)
            Right(Option(sCursor))
          case JavaResult(None, Some(sCursor), Some(jCursor)) =>
            log.debug("RECEIVED SOMETHING 2")
            Right(Option(sCursor))
          case _ =>
            log.debug("RECEIVED SOMETHING 3")
            val errorMsg = s"Could not handle JAVA Transformation url [$url] listener [${listener.getId}] msg [$msg]"
            listener.onError(errorMsg, Optional.empty())
            Left(ErrorResult(createException(errorMsg)))
        }
      case (_, Some((des, listener))) => //Scala
        transformScala(msg, des).right.flatMap {
          case ScalaResult(Some(events), Some(cursor)) =>
            log.debug("RECEIVED SOMETHING 4")
            listener.onReceive(url, cursor, events)
            Right(Option(cursor))
          case ScalaResult(None, Some(cursor)) =>
            log.debug("RECEIVED SOMETHING 5")
            Right(Option(cursor))
          case _ =>
            log.debug("RECEIVED SOMETHING 6")
            val errorMsg = s"Could not handle SCALA Transformation url [$url] listener [${listener.id}] msg [$msg]"
            listener.onError(errorMsg, None)
            Left(ErrorResult(createException(errorMsg)))

        }
      case _ =>
        val errorMsg = s"Could not handle SCALA Transformation url [$url] msg [$msg]"
        Left(ErrorResult(createException(errorMsg)))
    }
  }

}
