package org.zalando.nakadi.client.scala
import org.mockito.Matchers.any
import org.mockito.Matchers.anyString
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar

class ClientHandlerTest extends WordSpec with Matchers with MockitoSugar with BeforeAndAfter {

  private val conn = mock[Connection]
  private val handler = new ClientHandlerImpl(conn)
  private val endpoint = "event-types"

  before {
    reset(conn)
  }
  def replaySuccessfullResponse() {
    conn.executeCall(null)
  }

  "ClientHandler" should {
    "call [get] method with [endpoint] and [token]" in {
//      handler.get(endpoint)
    }
    "call [get] method with [endpoint], [header-params] and [token]" in {

    }
    "call [stream] method with [endpoint], [header-params] and [token]" in {

    }
    "call [delete] method with [endpoint] and [token]" in {

    }
    "call [put] method with [endpoint, [model] and [token]" in {

    }
    "call [post] method with [endpoint, [model] and [token]" in {

    }
    "call [subscribe] method with [endpoint, [cursor], [listener], and [token]" in {

    }
  }

}