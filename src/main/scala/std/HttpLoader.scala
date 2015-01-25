package std

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent._
import spray.http._
import spray.can.Http
import spray.http.HttpMethods._
import spray.http.ContentTypes._

import scala.util.{Success, Failure}

import ExecutionContext.Implicits.global

import scala.concurrent.duration._

/**
 * Created by kojuhovskiy on 25/01/15.
 */
object HttpLoader {
  def main(args: Array[String]): Unit = {
    for (i <- 1 to 20) {
      implicit val actorSystem = ActorSystem()
      implicit val timeout = Timeout(60.second)

      val host = "192.168.59.103"

      val url = s"http://$host:9090/request"

      val reqs = List.fill(5)(-1)

      val responses: List[Future[HttpResponse]] =
        reqs.map {
          i => {
            val response: Future[HttpResponse] =
              (IO(Http) ?
                HttpRequest(method = POST, uri = Uri(url),
                  entity = HttpEntity(`application/json`,
                    """{"title": "fountain", "titleRus": "фонтан", "year": 1988}"""))).mapTo[HttpResponse]

            response onComplete {
              case Success(res) => println(res.entity)
              case _ => println("error")
            }

            response
          }
        }

      Await.result(Future.sequence(responses), 35 seconds)

      //    response.onSuccess()

      println("HW")

      actorSystem.shutdown()
    }
  }
}
