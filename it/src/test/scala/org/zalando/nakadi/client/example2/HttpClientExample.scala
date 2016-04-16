package org.zalando.nakadi.client.example2

import scala.concurrent.Future
import scala.util.Random

import org.zalando.nakadi.client.{ ClientFactory, HttpFactory, StreamParameters }
import org.zalando.nakadi.client.model.Cursor
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpMethods, HttpRequest, HttpResponse }
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Source }
import akka.util.ByteString

object HttpClient extends App {
  private implicit val actorSystem = ActorSystem("Nakadi-Client-Connections")
  private implicit val http = Http(actorSystem)
  implicit val materializer = ActorMaterializer()
  val client = new HttpClient()
  client.start()
}

class HttpClient(implicit system: ActorSystem, m: ActorMaterializer) extends ClientFactory with HttpFactory {

  def generateWord(): Seq[String] = for { a <- 0 to 200 } yield Random.alphanumeric.take(10).mkString
  println(generateWord())
  val source = Source.single(generateWord())

  //  source.runForeach { println }

  def start() = {
    val conn: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = connection.connection()
    val url = "/event-types/test-client-integration-event-1936085527-148383828851369665/events"
    val cursor = Cursor(0, 0)
    val params = Some(StreamParameters())
    val headers = RawHeader("Accept", "application/x-json-stream") :: withHeaders(params)
    val request = withHttpRequest(url, HttpMethods.GET, headers, OAuth2Token)
    Source.single(request)
      .via(conn)
      .map(_.entity.dataBytes)
      //      .flatten(FlattenStrategy.concat)
//            .map(_.decodeString("UTF-8"))
      .runForeach(_.runForeach { x => println(" >>> "+x.decodeString("UTF-8")) }).onComplete { _ =>
        println("Shutting down")
        system.terminate()
      }

    //    val result = Source.single(request).via(conn).runForeach {
    //      _ match {
    //        case HttpResponse(status, headers, entity, protocol) if (status.isSuccess()) =>
    //          println(" >>>>>>>>>>>>>>>>>>>>> " + entity.dataBytes)
    //        case HttpResponse(status, headers, entity, protocol) if (status.isSuccess()) =>
    //          println(" >>>>>>>>>>>>>>>>>>>>> Error")
    //      }
    //    }

    val flow = Flow[ByteString]
      .via(akka.stream.scaladsl.Framing.delimiter(
        ByteString("\n"),
        maximumFrameLength = 256,
        allowTruncation = true))
      .map(_.utf8String)
      .map(_ + "!!!\n")
      .map { x =>
        println(s"$x")
        ByteString(x)
      }

  }

}