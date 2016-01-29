package org.zalando.nakadi.client

import java.net.URI

import akka.actor.ActorSystem
import com.fasterxml.jackson.databind.ObjectMapper
import com.typesafe.config.ConfigFactory
import de.zalando.scoop.{ScoopClient, Scoop}
import org.zalando.nakadi.client.actor.KlientSupervisor.{NewScoopAwareSubscription, NewSubscription}
import scala.concurrent.ExecutionContext.Implicits.global


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

  override val system = ActorSystem("scoop-nakadi-client", ConfigFactory.defaultApplication()
                                                                        .withFallback(scoop.buildConfiguration()))
  scoop.startScoopActor(system)




  override def listenForEvents(topic: String,
                               partitionId: String,
                               parameters: ListenParameters,
                               listener: Listener,
                               autoReconnect: Boolean = false): Unit = {
    checkNotNull(topic, "topic must not be null")
    checkNotNull(partitionId, "partitionId must not be null")
    checkNotNull(parameters, "list parameters must not be null")
    checkNotNull(listener, "listener must not be null")
    checkNotNull(autoReconnect, "autoReconnect must not be null")

    checkExists(parameters.batchFlushTimeoutInSeconds, "batchFlushTimeoutInSeconds is not set")
    checkExists(parameters.batchLimit, "batchLimit is not set")
    checkExists(parameters.startOffset, "startOffset is not specified")
    checkExists(parameters.streamLimit, "streamLimit is not specified")

    supervisor !  NewScoopAwareSubscription(topic,
                                            partitionId,
                                            parameters,
                                            autoReconnect,
                                            listener,
                                            this,
                                            scoop.defaultClient(),
                                            scoopTopic)
  }
}
