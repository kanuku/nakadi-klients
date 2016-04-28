package org.zalando.nakadi.client

case class ClientError(msg: String, status: Option[Int])