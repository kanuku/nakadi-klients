package org.zalando.nakadi.client.utils

import java.util.Optional

import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar

class GeneralConversionsTest extends FlatSpec with Matchers with MockitoSugar {

  case class Test(in: String, out: Integer)
  val test = Test("Default", 99)

  "toOptional" should "return a Java Optional object" in {
    val result: Optional[Test] = GeneralConversions.toOptional[Test](Some(test))
    assert(result!=null,"Result must not be null!!")
   val res= result.get
    assert(res.in==test.in)
    assert(res.out==test.out)
  }
  "toOption" should "return a scala Option object" in {
    val result:Option[Test] = GeneralConversions.toOption(Optional.of(test))
    assert(result.isDefined,"Result must not be null")
    val Some(res)=result
    assert(res.in==test.in)
    assert(res.out==test.out)
    
  
  }
  "toOptionOfSeq" should "return a Sequence of Options" in {

  }

}