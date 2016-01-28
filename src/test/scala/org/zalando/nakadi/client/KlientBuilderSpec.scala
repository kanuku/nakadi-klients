package org.zalando.nakadi.client

import java.net.URI

import com.fasterxml.jackson.databind.ObjectMapper
import org.scalatest.{WordSpec, Matchers}

class KlientBuilderSpec extends WordSpec with Matchers {

  "A Klient builder" must {

    // These things get tested by other things, anyways, so I'm not sure it's useful to test it (in development phase,
    // it's probably been useful). AKa280116
    //
    "build a Klient instance, if everything is set up properly" in {
      KlientBuilder()
        .withEndpoint(new URI("localhost:8080"))
        .withTokenProvider(() => "my-token")
        .build()
    }

    // This could be changed to a "must provide '.buildJavaClient' method (but not test the construction that was
    // already tested above). I.e. keep tests short, and orthogonal. AKa280116
    //
    "build a Java client instance, if everything is set up properly" in {
      KlientBuilder()
        .withEndpoint(new URI("localhost:8080"))
        .withTokenProvider(() => "my-token")
        .buildJavaClient()
    }

    "throw an exception, if not all mandatory arguments are provided" in {

      // Since the arguments are mandatory, what's the point of having a builder, exactly? We can probably
      // do the same (better) with constructor and named arguments, or abstract members that need to be
      // provided by the application. Do you want me to make a suggestion? AKa280116
      //
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

    // hmm.. ObjectMapper is about the Jackson JSON conversions, right?
    // Would you like to see how spray-json does it? It's just one of Scala JSON libraries, but pretty "magical". :)

    "use the specified ObjectMapper" in {

      val objectMapper = new ObjectMapper()

      val klient: KlientImpl = KlientBuilder()
        .withEndpoint(new URI("localhost:8080"))
        .withTokenProvider(() => "my-token")
        .withObjectMapper(objectMapper)
        .build().asInstanceOf[KlientImpl]

      klient.objectMapper == objectMapper should be(true)   // what is this actually testing?
    }
  }
}
