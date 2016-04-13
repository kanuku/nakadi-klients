package org.zalando.nakadi.client


/**
 *
 */
trait Deserializer[T] {
  def fromJson(from: String): T
}

/**
 *
 */
trait Serializer[T]  {
  def toJson(from: T): String
}

