package org.zalando.nakadi.client

import java.net.URI

import akka.actor.Terminated
import com.fasterxml.jackson.databind.ObjectMapper
import de.zalando.scoop.Scoop
import org.zalando.nakadi.client.actor.KlientSupervisor.NewSubscription
import org.zalando.nakadi.client.scoop.{ScoopListenerWrapper, ScoopClientWrapper}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ScoopAwareNakadiKlient(endpoint: URI,
                             port: Int,
                             securedConnection: Boolean,
                             tokenProvider: () => String,
                             objectMapper: ObjectMapper,
                             scoopOption: Option[Scoop],
                             scoopTopicOption: Option[String])
  extends KlientImpl(endpoint, port, securedConnection, tokenProvider, objectMapper) {

  val scoopTopic = scoopTopicOption.getOrElse(throw new IllegalArgumentException("no Scoop topic specified"))
  val scoop = scoopOption.getOrElse(throw new IllegalArgumentException("no Scoop builder given"))
  val scoopSystem = scoop.build()
  val scoopClient = ScoopClientWrapper(this, scoop.defaultClient(), scoopTopic)

  override def listenForEvents(topic: String,
                               partitionId: String,
                               parameters: ListenParameters,
                               listener: Listener,
                               autoReconnect: Boolean = false): Unit =
        super.listenForEvents(topic, partitionId, parameters, ScoopListenerWrapper(scoopClient, scoopTopic, listener))


  /**
   * Shuts down the communication system of the client and Scoop and returns any Terminated instance.
   */
  override def stop(): Future[Terminated] = Future.sequence(Seq(super.stop(), scoopSystem.terminate()))
                                                  .flatMap[Terminated]( seq => Future(seq.head))

}
