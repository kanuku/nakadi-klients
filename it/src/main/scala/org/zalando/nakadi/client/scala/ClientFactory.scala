package org.zalando.nakadi.client.scala
object ClientFactory {
  import sys.process._
  import scala.language.postfixOps
  def host():String = "nakadi-sandbox.aruha-test.zalan.do"
  def OAuth2Token(): () => String = () =>   "9eb412e6-3ffc-4ccd-b167-33af553a4c80"
  def getToken():String = OAuth2Token().apply()
  def port():Integer = 443
  def connection():Connection = Connection.newConnection(host, port, OAuth2Token(), true, false)
  def client():Client = new ClientImpl(connection, "UTF-8")

}