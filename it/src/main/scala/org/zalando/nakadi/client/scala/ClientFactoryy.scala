package org.zalando.nakadi.client.scala

object ClientFactory {
  def host():String = "nakadi-sandbox.my-test.fernan.do"
  def OAuth2Token(): () => String = () => ""
  def getToken():String = OAuth2Token().apply()
  def port():Integer = 443
  def connection():Connection = Connection.newConnection(host, port, OAuth2Token(), true, false)
  def client():Client = new ClientImpl(connection, "UTF-8")

}