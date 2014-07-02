package eu.seria.fana.server

import unfiltered.netty.{ServerErrorResponse, future}
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._
import unfiltered.netty.future.Plan.Intent
import unfiltered.request.{Path, GET}
import unfiltered.response.ResponseString
import eu.seria.fana.{RedisConfig, Config, FindANewApartment}

@io.netty.channel.ChannelHandler.Sharable
class FindANewApartmentServer extends future.Plan with ServerErrorResponse {

  val logger = org.clapper.avsl.Logger(this.getClass)

  unfiltered.netty.Http(8080)
    .handler(this)
    .beforeStop {
    findANewApartment.kill()
  }.run(s =>
    println("starting unfiltered app at localhost on port %s".format(s.port))
    )


  lazy val findANewApartment = new FindANewApartment(Config("http://www.kijiji.ca",
    "/b-appartement-condo/ville-de-montreal/villeray/k0c37l1700281?origin=ps",
    10 seconds,
    RedisConfig("10.0.1.6", 6379, "apartments")
  ))

  override def intent: Intent = {

    case GET(Path("/")) => {
      Future(ResponseString("Yo!"))
    }
    case GET(Path("/server/start")) => {
      findANewApartment.start()
      Future(ResponseString("start"))
    }
    case GET(Path("/server/stop")) => {
      findANewApartment.stop()
      Future(ResponseString("stop"))
    }
    case GET(Path("/server/status")) => {
      findANewApartment.status().map(res => ResponseString(res.toString))
    }

  }

  override implicit def executionContext: ExecutionContext = ExecutionContext.Implicits.global
}
