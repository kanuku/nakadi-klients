package org.zalando.nakadi.client

import java.net.URI

import com.fasterxml.jackson.databind.ObjectMapper
import de.zalando.scoop.Scoop
import org.scalatest.{WordSpec, Matchers}

class KlientBuilderSpec extends WordSpec with Matchers {

  "A Klient builder" must {

    "build a Klient instance, if everything is set up properly" in {
      KlientBuilder()
        .withEndpoint(new URI("localhost:8080"))
        .withTokenProvider(() => "my-token")
        .build()
    }

    "build a Java client instance, if everything is set up properly" in {
      KlientBuilder()
        .withEndpoint(new URI("localhost:8080"))
        .withTokenProvider(() => "my-token")
        .buildJavaClient()
    }

    "throw an exception, if not all mandatory arguments are provided" in {
      an [IllegalStateException] must be thrownBy {
        KlientBuilder()
          .withTokenProvider(() => "my-token")
          .build()
      }
      an [IllegalStateException] must be thrownBy {
        KlientBuilder()
          .withEndpoint(new URI("localhost:8080"))
          .build()
      }
    }

    "use the specified ObjectMapper" in {
      val objectMapper = new ObjectMapper()

      val klient: KlientImpl = KlientBuilder()
        .withEndpoint(new URI("localhost:8080"))
        .withTokenProvider(() => "my-token")
        .withObjectMapper(Some(objectMapper))
        .build().asInstanceOf[KlientImpl]

      klient.objectMapper == objectMapper should be(true)
    }

    "create a new KlientBuilder instance containing all original settings together with changed endpoint" in {
      val initialBuilder = KlientBuilder()

      val newURI = new URI("www.zalando.de")
      val nextBuilder = initialBuilder.withEndpoint(newURI)

      nextBuilder.endpoint should be(newURI)
      compareKlientBuilderInstance(initialBuilder, nextBuilder, compareEndpoint = false)
    }

    "create a new KlientBuilder instance containing all original settings together with changed port" in {
      val initialBuilder = KlientBuilder()

      val newPort = 9999
      val nextBuilder = initialBuilder.withPort(newPort)

      nextBuilder.port should be (newPort)
      compareKlientBuilderInstance(initialBuilder, nextBuilder, comparePort = false)
    }

    "create a new KlientBuilder instance containing all original settings together with changed ObjectMapper" in {
      val initialBuilder = KlientBuilder()

      val newObjectMapper = Some(new ObjectMapper())
      val nextBuilder = initialBuilder.withObjectMapper(newObjectMapper)
      nextBuilder.objectMapper should be (newObjectMapper)
      compareKlientBuilderInstance(initialBuilder, nextBuilder, compareObjectMapper = false)
    }

    "create a new KlientBuilder instance containing all original settings together with changed Scoop" in {
      val initialBuilder = KlientBuilder()

      val newScoop = Some(new Scoop())
      val nextBuilder = initialBuilder.withScoop(newScoop)
      nextBuilder.scoop should be (newScoop)
      compareKlientBuilderInstance(initialBuilder, nextBuilder, compareScoop = false)
    }

    "create a new KlientBuilder instance containing all original settings together with changed Scoop topic" in {
      val initialBuilder = KlientBuilder()

      val newScoopTopic = Some("tadaaaa!!")
      val nextBuilder = initialBuilder.withScoopTopic(newScoopTopic)
      nextBuilder.scoopTopic should be (newScoopTopic)
      compareKlientBuilderInstance(initialBuilder, nextBuilder, compareScoopTopic = false)
    }

    "create a new KlientBuilder instance containing all original settings together with changed Secured Connection setting" in {
      val initialBuilder = KlientBuilder()

      val hasSecuredConnection = ! initialBuilder.securedConnection
      val nextBuilder = initialBuilder.withSecuredConnection(hasSecuredConnection)
      nextBuilder.securedConnection should be (hasSecuredConnection)
      compareKlientBuilderInstance(initialBuilder, nextBuilder, compareSecuredConnection = false)
    }

    "create a new KlientBuilder instance containing all original settings together with changed token provider" in {
      val initialBuilder = KlientBuilder()

      val newTokenProvider = () => "some token"
      val nextBuilder = initialBuilder.withTokenProvider(newTokenProvider)
      nextBuilder.tokenProvider should be (newTokenProvider)
      compareKlientBuilderInstance(initialBuilder, nextBuilder, compareTokenProvider = false)
    }
  }


  def compareKlientBuilderInstance(initialBuilder: KlientBuilder,
                                   nextBuilder: KlientBuilder,
                                   compareEndpoint: Boolean = true,
                                   comparePort: Boolean = true,
                                   compareObjectMapper: Boolean = true,
                                   compareScoop: Boolean = true,
                                   compareScoopTopic: Boolean = true,
                                   compareSecuredConnection: Boolean = true,
                                   compareTokenProvider: Boolean = true) = {

    if(compareEndpoint) nextBuilder.endpoint should be(initialBuilder.endpoint)
    if(comparePort) nextBuilder.port should be (initialBuilder.port)
    if(compareObjectMapper) nextBuilder.objectMapper should be (initialBuilder.objectMapper)
    if(compareScoop) nextBuilder.scoop should be(initialBuilder.scoop)
    if(compareScoopTopic) nextBuilder.scoopTopic should be(initialBuilder.scoopTopic)
    if(compareSecuredConnection) nextBuilder.securedConnection should be(initialBuilder.securedConnection)
    if(compareTokenProvider) nextBuilder.tokenProvider should be(initialBuilder.tokenProvider)

  }




}

