package org.zalando.nakadi.client

import scala.concurrent.Future
import akka.actor.Terminated
import akka.http.scaladsl.model.HttpResponse
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.protobuf.ByteString
import org.zalando.nakadi.client.model.EventType
import org.zalando.nakadi.client.model.PartitionResolutionStrategy
import akka.http.scaladsl.unmarshalling._
import scala.concurrent.Await
import org.zalando.nakadi.client.model.Partition
import org.zalando.nakadi.client.model.EventEnrichmentStrategy
import org.zalando.nakadi.client.model.EventValidationStrategy

trait Client {
  import Client._

  /**
   * Retrieve monitoring metrics
   *
   * {{{
   * curl --request GET /metrics
   * }}}
   */
  def metrics(): Future[Either[ClientError, HttpResponse]]

  /**
   * Returns a list of all registered EventTypes.
   *
   * {{{
   * curl --request GET /event-types
   * }}}
   *
   */
  def eventTypes()(implicit marshaller: FromEntityUnmarshaller[List[EventType]]): Future[Either[ClientError, List[EventType]]]

  /**
   * Creates a new EventType.
   *
   * {{{
   * curl --request POST -d @fileWithEventType /event-types
   * }}}
   *
   * @param event - The EventType to create.
   *
   */
  def newEventType(eventType: EventType)(implicit marshaller: FromEntityUnmarshaller[EventType]): Future[Option[ClientError]]

  /**
   * Returns the EventType identified by its name.
   * {{{
   * curl --request GET /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   */
  def eventType(name: String)(implicit marshaller: FromEntityUnmarshaller[EventType]): Future[Either[ClientError, EventType]]
  /**
   * Updates the EventType identified by its name.
   * {{{
   * curl --request PUT -d @fileWithEventType /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   * @param event - Event to update
   */
  def updateEventType(name: String, eventType: EventType)(implicit marshaller: FromEntityUnmarshaller[EventType]): Future[Option[ClientError]]
  /**
   * Deletes an EventType identified by its name.
   *
   * {{{
   * curl --request DELETE /event-types/{name}
   * }}}
   *
   * @param name - Name of the EventType
   */
  def deleteEventType(name: String): Future[Option[ClientError]]

  /**
   * Creates a new Event for the given EventType.
   * {{{
   * curl --request POST -d @fileWithEvent /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   * @param event - Event to create
   */
  def newEvent[T](name: String, event: T)(implicit marshaller: FromEntityUnmarshaller[T]): Future[Option[ClientError]]

  /**
   * Request a stream delivery for the specified partitions of the given EventType.
   * {{{
   * curl --request GET  /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   *
   */
  def events[T](name: String)(implicit marshaller: FromEntityUnmarshaller[T]): Future[Either[ClientError, T]]

  /**
   * List the partitions for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions
   * }}}
   * @param name -  Name of the EventType
   */
  def partitions(name: String)(implicit marshaller: FromEntityUnmarshaller[Partition]): Future[Either[ClientError, Partition]]

  /**
   * Returns the Partition for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions/{partition}
   * }}}
   * @param name -  Name of the EventType
   * @param partition - Name of the partition
   */

  def partitionByName(name: String, partition: String)(implicit marshaller: FromEntityUnmarshaller[Partition]): Future[Either[ClientError, Partition]]

  /**
   * Returns all of the validation strategies supported by this installation of Nakadi.
   *
   * {{{
   * curl --request GET /registry/validation-strategies
   * }}}
   */
  def validationStrategies()(implicit marshaller: FromEntityUnmarshaller[EventValidationStrategy]): Future[Either[ClientError, EventValidationStrategy]]

  /**
   * Returns all of the enrichment strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/enrichment-strategies
   * }}}
   */

  def enrichmentStrategies()(implicit marshaller: FromEntityUnmarshaller[EventEnrichmentStrategy]): Future[Either[ClientError, EventEnrichmentStrategy]]

  /**
   * Returns all of the partitioning strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/partitioning-strategies
   * }}}
   */
  def partitionStrategies()(implicit marshaller: FromEntityUnmarshaller[List[PartitionResolutionStrategy]]): Future[Either[ClientError, List[PartitionResolutionStrategy]]]

  /**
   * Shuts down the communication system of the client
   */

  def stop(): Future[Terminated]

}

object Client {
  case class ClientError(msg: String, status: Option[Int])
}

private[client] class ClientImpl(connection: Connection, charSet: String) extends Client {
  import Client._
  implicit val materializer = connection.materializer

  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  def metrics(): Future[Either[ClientError, HttpResponse]] = ???

  def eventTypes()(implicit marshaller: FromEntityUnmarshaller[List[EventType]]): Future[Either[ClientError, List[EventType]]] = {
    logGetError(connection.get(URI_EVENT_TYPES).flatMap(handleGetResponse(_)))
  }

  def newEventType(eventType: EventType)(implicit marshaller: FromEntityUnmarshaller[EventType]): Future[Option[ClientError]] = {
    logPostError(connection.post(URI_EVENT_TYPES).map(handlePostResponse(_)))
  }

  def eventType(name: String)(implicit marshaller: FromEntityUnmarshaller[EventType]): Future[Either[ClientError, EventType]] = ???

  def updateEventType(name: String, eventType: EventType)(implicit marshaller: FromEntityUnmarshaller[EventType]): Future[Option[ClientError]] = ???

  def deleteEventType(name: String): Future[Option[ClientError]] = ???

  def newEvent[T](name: String, event: T)(implicit marshaller: FromEntityUnmarshaller[T]): Future[Option[ClientError]] = ???

  def events[T](name: String)(implicit marshaller: FromEntityUnmarshaller[T]): Future[Either[ClientError, T]] = ???

  def partitions(name: String)(implicit marshaller: FromEntityUnmarshaller[Partition]): Future[Either[ClientError, Partition]] = ???

  def partitionByName(name: String, partition: String)(implicit marshaller: FromEntityUnmarshaller[Partition]): Future[Either[ClientError, Partition]] = ???

  def validationStrategies()(implicit marshaller: FromEntityUnmarshaller[EventValidationStrategy]): Future[Either[ClientError, EventValidationStrategy]] = ???

  def enrichmentStrategies()(implicit marshaller: FromEntityUnmarshaller[EventEnrichmentStrategy]): Future[Either[ClientError, EventEnrichmentStrategy]] = ???

  def partitionStrategies()(implicit marshaller: FromEntityUnmarshaller[List[PartitionResolutionStrategy]]): Future[Either[ClientError, List[PartitionResolutionStrategy]]] =
    logGetError(connection.get(URI_PARTITIONING_STRATEGIES).flatMap(handleGetResponse(_)))

  def stop(): Future[Terminated] = connection.stop()

  //####################
  //#  HELPER METHODS  #
  //####################

  def logGetError[A, T](future: Future[Either[ClientError, T]]): Future[Either[ClientError, T]] = {
    future recover {
      case e: Throwable =>
        logger.error("A unexpected error occured", e)
        Left(ClientError("Error: " + e.getMessage, None))
    }
  }
  def logPostError(future: Future[Option[ClientError]]): Future[Option[ClientError]] = {
    future recover {
      case e: Throwable =>
        logger.error("A unexpected error occured", e)
        Option(ClientError("Error: " + e.getMessage, None))
    }
  }

  def handleGetResponse[T](response: HttpResponse)(implicit unmarshaller: FromEntityUnmarshaller[T], m: Manifest[T]): Future[Either[ClientError, T]] = {
    logger.debug("received [response={}]", response)
    response.status match {
      case status if (status.isSuccess()) =>
        logger.info("Result is successfull ({}) ", status.intValue().toString())
        Unmarshal(response.entity).to[T].map(Right(_))
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
  def handlePostResponse[T](response: HttpResponse): Option[ClientError] = {
    logger.debug("received [response={}]", response)
    response.status match {
      case status if (status.isSuccess()) =>
        None
      case status if (status.isRedirection()) =>
        val msg = "An error occurred with http-status (" + status.intValue() + "}) and reason:" + status.reason()
        logger.info(msg)
        Option(ClientError(msg, Some(status.intValue())))
      case status if (status.isFailure()) =>
        val msg = "An error occurred with http-status (" + status.intValue() + "}) and reason:" + status.reason()
        logger.warn(msg)
        Option(ClientError(msg, Some(status.intValue())))
    }
  }

}

