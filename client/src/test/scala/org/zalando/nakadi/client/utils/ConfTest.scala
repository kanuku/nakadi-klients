package org.zalando.nakadi.client.utils

import java.util.Optional

import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar

class ConfTest extends FlatSpec with Matchers with MockitoSugar {

  "Conf.retryTimeout" should "return 10 (integer)" in {
    assert(Conf.retryTimeout == 10)
  }
  "Conf.receiveBufferSize" should "return 40960 (integer)" in {
	  assert(Conf.receiveBufferSize  == 40960)
  }

}