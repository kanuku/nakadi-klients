package org.zalando.nakadi.client

/**
 * @param partition Id of the partition pointed to by this cursor.
 * @param offset Offset of the event being pointed to.
 */
case class Cursor(
  partition: Integer,
  offset: Integer)
