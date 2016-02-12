package test.unit

/*
* ConfTest
*/
import org.scalatest._
import org.zalando.nakadi.client.Conf

class ConfTest extends FlatSpec with Matchers {

  behavior of "Configuration"

  it should "(be consistent and) not cause an exception when loaded" in {
    Conf    // initiates the configuration object
  }
}