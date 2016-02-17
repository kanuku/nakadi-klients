package org.zalando.nakadi.client.utils

import akka.actor.Terminated
import org.zalando.nakadi.client._
import org.zalando.nakadi.client.utils.KlientMock.MockReturnValues
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object KlientMock {
  case class MockReturnValues( getMetrics: Either[String, Map[String, Any]] = Right(Map("a" -> Map("b" -> "c"))),
                               getPartition: Either[String, TopicPartition] = Right(TopicPartition("partitionid", "oldestAvailableOffset", "newestAvailableOffset")),
                               postEvent: Either[String, Unit] = Right(()),
                               stop: Terminated = null,
                               getPartitions: Either[String, List[TopicPartition]] = Right(List(TopicPartition("partitionid", "oldestAvailableOffset", "newestAvailableOffset"))),
                               getTopics: Either[String, List[Topic]] = Right(List(Topic("topic"))),
                               postEventToPartition: Either[String, Unit] = Right(()))

  lazy val defaultMockReturnValues = new MockReturnValues()
}

class KlientMock(values: MockReturnValues = new MockReturnValues()) extends Klient {

  override def getMetrics: Future[Either[String, Map[String, Any]]] =
    Future(values.getMetrics)

  override def subscribeToTopic(topic: String, parameters: ListenParameters, listener: Listener, autoReconnect: Boolean): Future[Unit] =
    Future(())

  override def stop(): Future[Terminated] =
    Future(values.stop)

  override def getPartition(topic: String, partitionId: String): Future[Either[String, TopicPartition]] =
    Future(values.getPartition)

  override def postEvent(topic: String, event: Event): Future[Either[String, Unit]] =
    Future(values.postEvent)

  override def listenForEvents(topic: String, partitionId: String, parameters: ListenParameters, listener: Listener, autoReconnect: Boolean): Unit = ()

  override def getPartitions(topic: String): Future[Either[String, List[TopicPartition]]] =
    Future(values.getPartitions)

  override def getTopics: Future[Either[String, List[Topic]]] =
    Future(values.getTopics)

  override def postEventToPartition(topic: String, partitionId: String, event: Event): Future[Either[String, Unit]] =
    Future(values.postEventToPartition)


  override def unsubscribeTopic(topic: String, listener: Listener): Unit = ()
}
