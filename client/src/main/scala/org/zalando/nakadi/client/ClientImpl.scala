package org.zalando.nakadi.client

import scala.{ Left, Right }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.model.{ EventEnrichmentStrategy, EventType, EventValidationStrategy, Partition, PartitionResolutionStrategy }

import com.typesafe.scalalogging.Logger

import Client.ClientError
import akka.actor.Terminated
import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.{ FromEntityUnmarshaller, Unmarshal }

private[client] class ClientImpl(connection: Connection, charSet: String) extends Client {
  import Client._
  implicit val materializer = connection.materializer

  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  def metrics(): Future[Either[ClientError, HttpResponse]] = ???

  def eventTypes()(implicit unmarshaller: FromEntityUnmarshaller[List[EventType]]): Future[Either[ClientError, Option[List[EventType]]]] = {
    logFutureEither(connection.get(URI_EVENT_TYPES).flatMap(mapToEither(_)))
  }

  def newEventType(eventType: EventType)(implicit marshaller: ToEntityMarshaller[EventType]): Future[Option[ClientError]] = {
    logFutureOption(connection.post(URI_EVENT_TYPES, eventType).map(mapToOption(_)))
  }

  def eventType(name: String)(implicit marshaller: FromEntityUnmarshaller[EventType]): Future[Either[ClientError, Option[EventType]]] = {
    logFutureEither(connection.get(URI_EVENT_TYPE_BY_NAME.format(name)).flatMap(in => mapToEither(in)))
  }

  def updateEventType(name: String, eventType: EventType)(implicit marshaller: ToEntityMarshaller[EventType]): Future[Option[ClientError]] = {
    val result =connection.put(URI_EVENT_TYPE_BY_NAME.format(name),eventType)
    logFutureOption(result.map(in => mapToOption(in)))
  }

  def deleteEventType(name: String): Future[Option[ClientError]] = {
    logFutureOption(connection.delete(URI_EVENT_TYPE_BY_NAME.format(name)).map(in => mapToOption(in)))
  }

  def newEvent[T](name: String, event: T)(implicit marshaller: FromEntityUnmarshaller[T]): Future[Option[ClientError]] = ???

  def events[T](name: String)(implicit marshaller: FromEntityUnmarshaller[T]): Future[Either[ClientError, Option[T]]] = ???

  def partitions(name: String)(implicit marshaller: FromEntityUnmarshaller[Partition]): Future[Either[ClientError, Option[Partition]]] = ???

  def partitionByName(name: String, partition: String)(implicit marshaller: FromEntityUnmarshaller[Partition]): Future[Either[ClientError, Option[Partition]]] = ???

  def validationStrategies()(implicit marshaller: FromEntityUnmarshaller[EventValidationStrategy]): Future[Either[ClientError, Option[EventValidationStrategy]]] = ???

  def enrichmentStrategies()(implicit marshaller: FromEntityUnmarshaller[EventEnrichmentStrategy]): Future[Either[ClientError, Option[EventEnrichmentStrategy]]] = ???

  def partitionStrategies()(implicit marshaller: FromEntityUnmarshaller[List[PartitionResolutionStrategy]]): Future[Either[ClientError, Option[List[PartitionResolutionStrategy]]]] =
    logFutureEither(connection.get(URI_PARTITIONING_STRATEGIES).flatMap(mapToEither(_)))

  def stop(): Future[Terminated] = connection.stop()

  //####################
  //#  HELPER METHODS  #
  //####################

  def logFutureEither[A, T](future: Future[Either[ClientError, T]]): Future[Either[ClientError, T]] = {
    future recover {
      case e: Throwable =>
        logger.error("A unexpected error occured", e)
        Left(ClientError("Error: " + e.getMessage, None))
    }
  }
  def logFutureOption(future: Future[Option[ClientError]]): Future[Option[ClientError]] = {
    future recover {
      case e: Throwable =>
        logger.error("A unexpected error occured", e)
        Option(ClientError("Error: " + e.getMessage, None))
    }
  }

  def mapToEither[T](response: HttpResponse)(implicit unmarshaller: FromEntityUnmarshaller[T], m: Manifest[T]): Future[Either[ClientError, Option[T]]] = {
    logger.debug("received [response={}]", response)
    response.status match {
      case status if (status.isSuccess()) =>
        logger.info("Result is successfull ({}) ", status.intValue().toString())
        Unmarshal(response.entity).to[T].map(in => Right(Option(in)))
      case status if (status.isRedirection()) =>
        val msg = "An error occurred with http-status (" + status.intValue() + "}) and reason:" + status.reason()
        logger.info(msg)
        Future.successful(Left(ClientError(msg, Some(status.intValue()))))
      case status if (status.isFailure()) =>
        val msg = "An error occurred with http-status (" + status.intValue() + "}) and reason:" + status.reason()
        logger.warn(msg)
        Future.successful(Left(ClientError(msg, Some(status.intValue()))))
    }
  }
  def mapToOption[T](response: HttpResponse): Option[ClientError] = {
    response.status match {
      case status if (status.isSuccess()) =>
        logger.debug("Success. http-status: %s", status.intValue().toString())
        None
      case status if (status.isRedirection()) =>
        val msg = "Redirection - http-status: %s, reason[%s]".format(status.intValue().toString(), status.reason())
        logger.info(msg)
        response.entity.toStrict(10.second).map { body =>
          logger.debug("Redirection - http-status: %s, reason[%s], body:[%s]".format(status.intValue().toString(), status.reason(), body.data.decodeString(charSet)))
        }
        Option(ClientError(msg, Some(status.intValue())))
      case status if (status.isFailure()) =>
        response.entity.toStrict(10.second).map { body =>
          logger.debug("Failure - http-status: %s, reason[%s], body:[%s]".format(status.intValue().toString(), status.reason(), body.data.decodeString(charSet)))
        }
        val msg = "Failure - http-status: %s, reason[%s], body:[%s]".format(status.intValue().toString(), status.reason(), response.entity.dataBytes)
        logger.warn(msg)
        Option(ClientError(msg, Some(status.intValue())))
    }
  }

}

