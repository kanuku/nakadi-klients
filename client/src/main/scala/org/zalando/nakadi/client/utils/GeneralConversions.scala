package org.zalando.nakadi.client.utils

import java.util.Optional
import scala.collection.JavaConversions._
import scala.language.implicitConversions

object GeneralConversions {

    def fromOptional[T](in: Option[T]) = in match {
    case None        => Optional.empty()
    case Some(value) => Optional.of(value)

  }
    def fromOption[T](in: Option[T]): Optional[T] = in match {
    case None        => Optional.empty()
    case Some(value) => Optional.of(value)
  }
//    def fromOption[IN, OUT](in: Option[IN])(implicit conversion: IN => OUT): Optional[OUT] = in match {
//    case None        => Optional.empty()
//    case Some(value) => Optional.of(conversion(value))
//  }
    def fromOptionOfSeq[T](in: Option[Seq[T]]): Optional[java.util.List[T]] = in match {
    case None              => Optional.empty()
    case Some(seq: Seq[T]) => Optional.of(seqAsJavaList(seq))
  }
}