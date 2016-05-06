package org.zalando.nakadi.client.scala
object ClientFactory {
  import sys.process._
  import scala.language.postfixOps
  def host():String = "nakadi-sandbox.aruha-test.zalan.do"
  def OAuth2Token(): () => String = () =>   "021e8f9d-a1e3-4d30-9259-39a39f4ddb9c"
  def getToken():String = OAuth2Token().apply()
  def port():Integer = 443
  def connection():Connection = Connection.newConnection(host, port, OAuth2Token(), true, false)
  def client():Client = new ClientImpl(connection, "UTF-8")

}