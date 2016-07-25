package org.zalando.nakadi.client.utils

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import akka.util.Timeout
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
  private val root = ConfigFactory.load.getConfig("nakadi.client")

  // Typesafe Config is Java/Scala compatible and thus stores durations as 'java.time.Duration'.
  // See -> http://stackoverflow.com/questions/32076311/converting-java-to-scala-durations
  //
  private implicit def convDuration(v: java.time.Duration) = scala.concurrent.duration.Duration.fromNanos(v.toNanos)
  
  
  val receiveBufferSize = root.getInt("receiveBufferSize")
  val retryTimeout = root.getInt("retryTimeout")
  
  def validateConfigurations(){
    val msg = "%s must be configured in reference.conf file !"
    if(receiveBufferSize<0){
      throw new IllegalStateException(receiveBufferSize.formatted("Size of receive buffer(receiveBufferSize)"));
    }
    if(retryTimeout<0){
      throw new IllegalStateException(receiveBufferSize.formatted("Retry timeout(retryTimeout)"));
    }
      
  }
  
  validateConfigurations()

}