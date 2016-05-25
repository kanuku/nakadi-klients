package org.zalando.nakadi.client.scala

import scala.{ Left, Right }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import akka.actor.Terminated
import akka.http.scaladsl.model.{ HttpHeader, HttpMethod, HttpMethods, HttpResponse, MediaRange }
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{ Accept, RawHeader }
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.utils.Uri
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.handler.SubscriptionHandlerImpl

private[scala] class ClientImpl(connection: Connection, charSet: String = "UTF-8") extends Client {
  import Uri._
  import JacksonJsonMarshaller._
  import HttpFactory._
  implicit val materializer = connection.materializer()

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  private val subscriber = new SubscriptionHandlerImpl(connection)

  def getMetrics(): Future[Either[ClientError, Option[Metrics]]] = {
    logFutureEither(get(URI_METRICS).flatMap(mapToEither(_)(deserializer(metricsTR))))
  }

  def getEventTypes(): Future[Either[ClientError, Option[Seq[EventType]]]] = {
    logFutureEither(get(URI_EVENT_TYPES).flatMap(mapToEither(_)(deserializer(listOfEventTypeTR))))
  }

  def createEventType(eventType: EventType): Future[Option[ClientError]] = {
    logFutureOption(post(URI_EVENT_TYPES, eventType).flatMap(mapToOption(_)))
  }

  def getEventType(name: String): Future[Either[ClientError, Option[EventType]]] = {
    logFutureEither(get(URI_EVENT_TYPE_BY_NAME.format(name)).flatMap(in => mapToEither(in)(deserializer(eventTypeTR))))
  }

  def updateEventType(name: String, eventType: EventType): Future[Option[ClientError]] = {
    val result = put(URI_EVENT_TYPE_BY_NAME.format(name), eventType)
    logFutureOption(result.flatMap(in => mapToOption(in)))
  }

  def deleteEventType(name: String): Future[Option[ClientError]] = {
    logFutureOption(delete(URI_EVENT_TYPE_BY_NAME.format(name)).flatMap(in => mapToOption(in)))
  }

  def publishEvents[T <: Event](eventTypeName: String, events: Seq[T], ser: Serializer[Seq[T]]): Future[Option[ClientError]] = {
    logFutureOption(post(URI_EVENTS_OF_EVENT_TYPE.format(eventTypeName), events).flatMap(in => mapToOption(in)))
  }
  def publishEvents[T <: Event](eventTypeName: String, events: Seq[T]): Future[Option[ClientError]] = {
    logFutureOption(post(URI_EVENTS_OF_EVENT_TYPE.format(eventTypeName), events).flatMap(in => mapToOption(in)))
  }

  def publishEvent[T <: Event](name: String, event: T, ser: Serializer[T]): Future[Option[ClientError]] = {
    logFutureOption(post(URI_EVENTS_OF_EVENT_TYPE.format(name), event).flatMap(in => mapToOption(in)))
  }

  def publishEvent[T <: Event](name: String, event: T): Future[Option[ClientError]] = {
    logFutureOption(post(URI_EVENTS_OF_EVENT_TYPE.format(name), event).flatMap(in => mapToOption(in)))
  }

  def getPartitions(name: String): Future[Either[ClientError, Option[Seq[Partition]]]] = {
    logFutureEither(get(URI_PARTITIONS_BY_EVENT_TYPE.format(name)).flatMap(in => mapToEither(in)(deserializer(listOfPartitionTR))))
  }

  def getValidationStrategies(): Future[Either[ClientError, Option[Seq[EventValidationStrategy.Value]]]] = {
    logFutureEither(get(URI_VALIDATION_STRATEGIES).flatMap(mapToEither(_)(deserializer(listOfEventValidationStrategyTR))))
  }

  def getEnrichmentStrategies(): Future[Either[ClientError, Option[Seq[EventEnrichmentStrategy.Value]]]] = {
    logFutureEither(get(URI_ENRICHMENT_STRATEGIES).flatMap(mapToEither(_)(deserializer(listOfEventEnrichmentStrategyTR))))
  }

  def getPartitioningStrategies(): Future[Either[ClientError, Option[Seq[PartitionStrategy.Value]]]] =
    logFutureEither(get(URI_PARTITIONING_STRATEGIES).flatMap(mapToEither(_)(deserializer(listOfPartitionStrategyTR))))

  def stop(): Option[ClientError] = {
    val result = Await.ready(connection.actorSystem().terminate(), Duration.Inf)
    None
  }

  def subscribe[T <: Event](eventTypeName: String, parameters: StreamParameters, listener: Listener[T], typeRef: TypeReference[EventStreamBatch[T]]): Future[Option[ClientError]] = {
    subscribe(eventTypeName, parameters, listener)(deserializer(typeRef))

  }
  def subscribe[T <: Event](eventType: String, params: StreamParameters, listener: Listener[T])(implicit des: Deserializer[EventStreamBatch[T]]): Future[Option[ClientError]] =
    (eventType, params, listener) match {

      case (_, _, listener) if listener == null =>
        logger.info("listener is null")
        Future.successful(Option(ClientError("Listener may not be empty(null)!", None)))

      case (eventType, _, _) if Option(eventType).isEmpty || eventType == "" =>
        logger.info("eventType is null")
        Future.successful(Option(ClientError("Eventype may not be empty(null)!", None)))

      case (eventType, StreamParameters(cursor, _, _, _, _, _, _), listener) if Option(eventType).isDefined =>
        val url = URI_EVENTS_OF_EVENT_TYPE.format(eventType)
        logger.debug("Subscribing listener {} - cursor {} - parameters {} - eventType {} - url {}", listener.id, cursor, params, eventType, url)
        val finalUrl = withUrl(url, Some(params))
        subscribe(finalUrl, cursor, listener)(des)
        val eventHandler: EventHandler = new EventHandlerImpl[EmptyJavaEvent, T](Right((des, listener)))
        subscriber.subscribe(finalUrl, cursor, eventHandler)
        Future.successful(None)
    }

  def unsubscribe[T <: Event](eventType: String, listener: Listener[T]): Future[Option[ClientError]] = ???

  def unsubscribe[T <: Event](endpoint: String, cursor: Option[Cursor], listener: Listener[T]): Unit = {
    val eventHandler: EventHandler = new EventHandlerImpl[EmptyJavaEvent, T](Right((null, listener)))
    subscriber.subscribe(endpoint, cursor, eventHandler)
  }

  //####################
  //#  HELPER METHODS  #
  //####################

  private def get(endpoint: String): Future[HttpResponse] = {
    logger.info("Get - URL {}", endpoint)
    connection.executeCall(withHttpRequest(endpoint, HttpMethods.GET, Nil, connection.tokenProvider, None))
  }
  private def delete(endpoint: String): Future[HttpResponse] = {
    logger.info("Delete: {}", endpoint)
    connection.executeCall(withHttpRequest(endpoint, HttpMethods.DELETE, Nil, connection.tokenProvider, None))
  }

  def put[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse] = {
    logger.info("Get: {}", endpoint)
    connection.executeCall(withHttpRequest(endpoint, HttpMethods.GET, Nil, connection.tokenProvider, None))
  }

  def post[T](endpoint: String, model: T)(implicit serializer: Serializer[T]): Future[HttpResponse] = {
    val entity = serializer.to(model)
    logger.info("Posting to endpoint {}", endpoint)
    logger.debug("Data to post {}", entity)
    connection.executeCall(withHttpRequestAndPayload(endpoint, entity, HttpMethods.POST, connection.tokenProvider))
  }

  def subscribe[T <: Event](endpoint: String, cursor: Option[Cursor], listener: Listener[T])(implicit des: Deserializer[EventStreamBatch[T]]) = {
    val eventHandler: EventHandler = new EventHandlerImpl[EmptyJavaEvent, T](Right((des, listener)))
    subscriber.subscribe(endpoint, cursor, eventHandler)
  }

  private def logFutureEither[A, T](future: Future[Either[ClientError, T]]): Future[Either[ClientError, T]] = {
    future recover {
      case e: Throwable =>
        logger.error("A unexpected error occured:", e.getMessage)
        Left(ClientError("Error: " + e.getMessage, None))
    }
  }
  private def logFutureOption(future: Future[Option[ClientError]]): Future[Option[ClientError]] = {
    future recover {
      case e: Throwable =>
        logger.error("A unexpected error occured", e)
        Option(ClientError("Error: " + e.getMessage, None))
    }
  }

  private def mapToEither[T](response: HttpResponse)(implicit deserializer: Deserializer[T]): Future[Either[ClientError, Option[T]]] = {
    logger.debug("received [response={}]", response)
    response match {
      case HttpResponse(status, headers, entity, protocol) if (status.isSuccess()) =>
        try {
          Unmarshal(entity).to[String].map(body => Right(Some(deserializer.from(body))))
        } catch {
          case e: Throwable =>
            val msg = "Failed to deserialise the content with error: %s".format(e.getMessage)
            logger.error(msg)
            Future.successful(Left(ClientError(msg, Some(status.intValue()))))
        }
      case HttpResponse(StatusCodes.NotFound, headers, entity, protocol) =>
        Future.successful(Right(None))
      case HttpResponse(status, headers, entity, protocol) if (status.isRedirection()) =>
        val msg = "Not implemented: http-status (" + status.intValue() + "}) and reason:" + status.reason()
        logger.info(msg)
        Future.successful(Left(ClientError(msg, Some(status.intValue()))))
      case HttpResponse(status, headers, entity, protocol) if (status.isFailure()) =>
        Unmarshal(entity).to[String].map { body =>
          val msg = "An error occurred, http-status: %s (%s) Message: %s".format(status.intValue(), status.reason(), body)
          logger.warn(msg)
          Left(ClientError(msg, Some(status.intValue())))
        }
    }
  }

  private[client] def mapToOption[T](response: HttpResponse): Future[Option[ClientError]] = {
    response.status match {
      case status if (status.isSuccess()) =>
        logger.debug("Success. http-status: %s".format(status.intValue()))
        Future.successful(None)
      case status if (status.isRedirection()) =>
        val msg = "Redirection - http-status: %s, reason[%s]".format(status.intValue().toString(), status.reason())
        logger.info(msg)
        response.entity.toStrict(10.second).map { body =>
          logger.debug("Redirection - http-status: %s, reason[%s], body:[%s]".format(status.intValue().toString(), status.reason(), body.data.decodeString(charSet)))
        }
        Future.successful(Option(ClientError(msg, Some(status.intValue()))))
      case status if (status.isFailure()) =>
        response.entity.toStrict(10.second).map { body =>
          val msg = "Failure - http-status: %s, reason[%s], body:[%s]".format(status.intValue().toString(), status.reason(), body.data.decodeString(charSet))
          logger.warn(msg)
          Option(ClientError(msg, Some(status.intValue())))
        }
    }
  }

}

