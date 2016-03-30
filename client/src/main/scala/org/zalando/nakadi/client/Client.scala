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
import akka.http.scaladsl.unmarshalling._
import org.zalando.nakadi.client.model.DefaultMarshaller
import scala.concurrent.Await

trait Client {

  /**
   * Retrieve monitoring metrics
   *
   * {{{
   * curl --request GET /metrics
   * }}}
   */
  def metrics(): Future[HttpResponse]

  /**
   * Returns a list of all registered EventTypes.
   *
   * {{{
   * curl --request GET /event-types
   * }}}
   *
   */
  def eventTypes()(implicit marshaller: FromEntityUnmarshaller[EventType], charset: String = "UTF-8"): Future[EventType]

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
  def newEventType(eventType: EventType): Future[Boolean]

  /**
   * Returns the EventType identified by its name.
   * {{{
   * curl --request GET /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   */
  def eventType(name: String): Future[HttpResponse]
  /**
   * Updates the EventType identified by its name.
   * {{{
   * curl --request PUT -d @fileWithEventType /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   * @param event - Event to update
   */
  def updateEventType(name: String, event: Any): Future[HttpResponse]
  /**
   * Deletes an EventType identified by its name.
   *
   * {{{
   * curl --request DELETE /event-types/{name}
   * }}}
   *
   * @param name - Name of the EventType
   */
  def deleteEventType(name: String): Future[HttpResponse]

  /**
   * Creates a new Event for the given EventType.
   * {{{
   * curl --request POST -d @fileWithEvent /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   * @param event - Event to create
   */
  def newEvent(name: String, event: Any): Future[HttpResponse]

  /**
   * Request a stream delivery for the specified partitions of the given EventType.
   * {{{
   * curl --request GET  /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   *
   */
  def events(name: String): Future[HttpResponse]

  /**
   * List the partitions for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions
   * }}}
   * @param name -  Name of the EventType
   */
  def partitions(name: String): Future[HttpResponse]

  /**
   * Returns the Partition for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions/{partition}
   * }}}
   * @param name -  Name of the EventType
   * @param partition - Name of the partition
   */

  def partitionByName(name: String, partition: String): Future[HttpResponse]

  /**
   * Returns all of the validation strategies supported by this installation of Nakadi.
   *
   * {{{
   * curl --request GET /registry/validation-strategies
   * }}}
   */
  def validationStrategies(): Future[HttpResponse]

  /**
   * Returns all of the enrichment strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/enrichment-strategies
   * }}}
   */

  def enrichmentStrategies(): Future[HttpResponse]

  /**
   * Returns all of the partitioning strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/partitioning-strategies
   * }}}
   */
  def partitionStrategies(): Future[HttpResponse]

  /**
   * Shuts down the communication system of the client
   */

  def stop(): Future[Terminated]

}

object Client {
  case class ClientError(msg: String)
}

private[client] class ClientImpl(connection: Connection) extends Client {
  implicit val materializer = connection.materializer

  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  def metrics(): Future[HttpResponse] = ???

  def eventTypes()(implicit marshaller: FromEntityUnmarshaller[EventType], charset: String = "UTF-8"): Future[EventType] = {
    connection.get(URI_EVENT_TYPES).flatMap(evaluateResponse(_))
  }

  def evaluateResponse[T](response: HttpResponse)(implicit unmarshaller: FromEntityUnmarshaller[T], m: Manifest[T], charset: String = "UTF-8"): Future[T] = {
    logger.debug("received [response={}]", response)
    response.status match {
      case status if (status.isSuccess()) =>
        logger.info("Result is successfull ({}) ", status.intValue().toString())
        Unmarshal(response.entity).to[T]
      case status if (status.isRedirection()) =>
        logger.info("Result is a redirect with http-status ({}) ", status.intValue().toString())
        //TODO: Implement logic for following redirects
        Unmarshal(response.entity).to[T].map { x =>
          logger.info("#####  >>>>> ")
          x
        }

      case status if (status.isFailure()) =>
        logger.warn("An error occurred with http-status ({}) and reason:{} ", status.reason(), status.intValue().toString())
        Unmarshal(response.entity).to[T]
    }
  }
  def newEventType(eventType: EventType): Future[Boolean] = ???

  def eventType(name: String): Future[HttpResponse] = ???

  def updateEventType(name: String, event: Any): Future[HttpResponse] = ???

  def deleteEventType(name: String): Future[HttpResponse] = ???

  def newEvent(name: String, event: Any): Future[HttpResponse] = ???

  def events(name: String): Future[HttpResponse] = ???

  def partitions(name: String): Future[HttpResponse] = ???

  def partitionByName(name: String, partition: String): Future[HttpResponse] = ???

  def validationStrategies(): Future[HttpResponse] = ???

  def enrichmentStrategies(): Future[HttpResponse] = ???

  def partitionStrategies(): Future[HttpResponse] = ???

  def stop(): Future[Terminated] = connection.stop()

}

object Main extends App with DefaultMarshaller {
  val host = "nakadi-sandbox.aruha-test.zalan.do"
  val OAuth2Token = () => "3ba41b94-a30a-4dee-a90e-237f4e77edde"
  val port = 443
  val client = new ClientImpl(Connection.newConnection(host, port, OAuth2Token, true, false))

  val response = client.eventTypes()
  Await.result(response, 10.second)
  response.map(r =>
    println("########################  " + r))

}