package org.zalando.nakadi.client

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.Duration
import scala.language.implicitConversions

/*
* Access to the configuration in a typed fashion.
*
* Also ensures that the configuration is healthy during the process run. Any parsing errors or inconsistencies will
* be found during launch.
*
* Note: We're exposing durations as 'scala.concurrent.duration.Duration' (instead of 'java.time.Duration') for example
*     because the Scala version has '.toSeconds' but Java version doesn't. AKa110216
*/
object Conf {
  private
  val root = ConfigFactory.load.getConfig("nakadi.client")

  // Typesafe Config is Java/Scala compatible and thus stores durations as 'java.time.Duration'.
  // Import this implicit in the application code, to get them as 'scala.concurrent.duration.Duration'.
  //
  // See -> http://stackoverflow.com/questions/32076311/converting-java-to-scala-durations
  //
  private implicit
  def convDuration(v: java.time.Duration) = scala.concurrent.duration.Duration.fromNanos(v.toNanos)

  val noListenerReconnectDelay: Duration = root.getDuration("noListenerReconnectDelay")
  val pollParallelism = root.getInt("pollParallelism")

  class cScoopListener(cfg: Config) {
    val selectorField = cfg.getString("selectorField")
  }
  val scoopListener = new cScoopListener( root.getConfig("scoopListener") )

  val defaultBatchFlushTimeout: Duration = root.getDuration("defaultBatchFlushTimeout")
  val defaultBatchLimit = root.getInt("defaultBatchLimit")
  val defaultStreamLimit = root.getInt("defaultStreamLimit")

  // ^^^ new entries above, please ^^^

  // Note: If we need to check config value ranges or consistency between multiple values, here's the place.
  //      Just throw an exception if something's not right.
  //
  if (false) {
    throw new RuntimeException( "Bad config: ..." )
  }
}