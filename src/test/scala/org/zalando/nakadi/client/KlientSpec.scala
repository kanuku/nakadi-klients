package org.zalando.nakadi.client

import java.net.URI

import com.fasterxml.jackson.databind.ObjectMapper
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

class KlientSpec extends WordSpec with Matchers with BeforeAndAfterEach {

  var klient: Klient = null

  override  def beforeEach() = {
    klient = KlientBuilder()
      .withEndpoint(new URI("localhost:8080"))
      .withTokenProvider(() => "my-token")
      .build()
  }

  override def afterEach(): Unit = {
    klient.stop()
  }



  "A Klient builder" must {
    "must build a Klient instance, if everything is set properly" in {
      val klient = KlientBuilder()
        .withEndpoint(new URI("localhost:8080"))
        .withTokenProvider(() => "my-token")
        .build()


    }
  }
}
