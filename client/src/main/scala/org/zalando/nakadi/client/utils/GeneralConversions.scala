package org.zalando.nakadi.client.utils

import java.util.Optional
import scala.collection.JavaConversions._
import scala.language.implicitConversions

object GeneralConversions {

  def toOptional[T](in: Option[T]): Optional[T] = in match {
    case None        => Optional.empty()
    case Some(value) => Optional.of(value)

  }
  def toOption[T](in: Optional[T]): Option[T] = if (in.isPresent()) {
    Option(in.get)
  } else {
    None
  }

  def toOptionOfSeq[T](in: Option[Seq[T]]): Optional[java.util.List[T]] = in match {
    case None              => Optional.empty()
    case Some(seq: Seq[T]) => Optional.of(seqAsJavaList(seq))
  }
}