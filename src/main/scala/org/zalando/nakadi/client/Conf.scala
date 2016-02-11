package org.zalando.nakadi.client

import com.typesafe.config.{Config, ConfigFactory}

import scala.language.implicitConversions

/*
* Access to the configuration in a typed fashion.
*
* Also ensures that the configuration is healthy during the process run. Any parsing errors or inconsistencies will
* be found during launch.
*/
object Conf {
  private
  val root = ConfigFactory.load.getConfig("nakadi.client")

  // Typesafe Config is Java/Scala compatible and thus stores durations as 'java.time.Duration'.
  // Import this implicit in the application code, to get them as 'scala.concurrent.duration.Duration'.
  //
  // See -> http://stackoverflow.com/questions/32076311/converting-java-to-scala-durations
  //
  implicit
  def convDuration(v: java.time.Duration) = scala.concurrent.duration.Duration.fromNanos(v.toNanos)

  val noListenerReconnectDelay = root.getDuration("noListenerReconnectDelay")
  val pollParallelism = root.getInt("pollParallelism")


  // ^^^ new entries above, please ^^^

  // Note: If we need to check config value ranges or consistency between multiple values, here's the place.
  //      Just throw an exception if something's not right.
  //
  if (false) {
    throw new RuntimeException( "Bad config: ..." )
  }
}