package org.zalando.nakadi.client

/**
 *
 */
trait Deserializer[T] {
  def from(from: String): T
}

/**
 *
 */
trait Serializer[T] {
  def to(from: T): String
}

