package org.zalando.nakadi.client.utils

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import collection.JavaConversions._
import org.zalando.nakadi.client.ClientError
import java.util.Optional

object FutureConversions {

  private def extractEither[T](either: Either[String, T]): T = either match {
    case Left(error) => throw new RuntimeException(error)
    case Right(t)    => t
  }
  private def extractOption[T >: Null](option: Option[ClientError]): T = option match {
    case Some(v) => throw new RuntimeException(v.msg)
    case None    => null
  }

  /**
   * Transforms the value in the option wrapped in the either right inside a
   * future into a java Optional wrapped in a Java future.
   * If either left inside the future contains a client error,
   * then a RuntimeException is thrown with the error!
   */
  def fromOptionOfEither2Optional[T](in: scala.concurrent.Future[Either[ClientError, Option[T]]]): java.util.concurrent.Future[Optional[T]] = {
    new MFuture[Either[ClientError, Option[T]], Optional[T]](in, a => fromRightOptionOfEither2Option(a))
  }

  /**
   * Transforms the sequence in the option wrapped in the either right inside a
   * future into a java List wrapped in an Optional wrapped in a Java future.
   * If either left inside the future contains a client error,
   * then a RuntimeException is thrown with the error!
   */
  def fromSeqOfOptionalEither2OptionalList[T](in: scala.concurrent.Future[Either[ClientError, Option[Seq[T]]]]): java.util.concurrent.Future[Optional[java.util.List[T]]] = {
    new MFuture[Either[ClientError, Option[Seq[T]]], Optional[java.util.List[T]]](in, a => fromSeqOfOptionalEither2OptionalList(a))
  }

  /**
   * Transforms an optional into a Void if it is empty, else RuntimeException is thrown with the error!
   */
  def fromOptional2Future(in: scala.concurrent.Future[Option[ClientError]]): java.util.concurrent.Future[Void] = {
    new MFuture[Option[ClientError], Void](in, a => extractOption(a))
  }

  private def fromSequenceToList[T](in: Seq[T]): Optional[java.util.List[T]] = in match {
    case Nil => Optional.empty()
    case seq => Optional.of(new java.util.ArrayList[T](seq))

  }

  private def fromRightOptionOfEither2Option[R](in: Either[ClientError, Option[R]]): Optional[R] = in match {
    case Left(e)            => throw new RuntimeException(e.msg)
    case Right(Some(value)) => Optional.of(value)
    case Right(None)        => Optional.empty()
  }
  private def fromSeqOfOptionalEither2OptionalList[R](in: Either[ClientError, Option[Seq[R]]]): Optional[java.util.List[R]] = in match {
    case Left(e)          => throw new RuntimeException(e.msg)
    case Right(None)      => Optional.empty()
    case Right(Some(Nil)) => Optional.of(new java.util.ArrayList[R]())
    case Right(Some(seq)) => Optional.of(new java.util.ArrayList[R](seq))
  }

  private def convert[T](x: scala.concurrent.Future[Either[String, T]]): java.util.concurrent.Future[T] =
    new MFuture[Either[String, T], T](x, a => extractEither(a))

}

private class MFuture[A, B](f: scala.concurrent.Future[A], converter: A => B) extends java.util.concurrent.Future[B] {
  override def isCancelled: Boolean = throw new UnsupportedOperationException

  override def get(): B = converter.apply(Await.result(f, Duration.Inf))

  override def get(timeout: Long, unit: TimeUnit): B = converter.apply(Await.result(f, Duration.create(timeout, unit)))

  override def cancel(mayInterruptIfRunning: Boolean): Boolean = throw new UnsupportedOperationException

  override def isDone: Boolean = f.isCompleted
}
