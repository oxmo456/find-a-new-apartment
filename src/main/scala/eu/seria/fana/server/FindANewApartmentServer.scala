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
    .run { s =>
    println("starting unfiltered app at localhost on port %s".format(s.port))
  }


  lazy val findANewApartment = new FindANewApartment(Config("http://www.kijiji.ca",
    "/b-appartement-condo/ville-de-montreal/villeray/k0c37l1700281?origin=ps",
    10 seconds,
    RedisConfig("10.0.1.6", 6379, "apartments")
  ))

  override def intent: Intent = {

    case GET(Path("/")) => {
      logger.info("/")
      Future(ResponseString("ok"))
    }
    case GET(Path("/server/start")) => {
      logger.info("/server/start")
      Future(ResponseString("ok"))
    }
    case GET(Path("/server/stop")) => {
      logger.info("/server/stop")
      Future(ResponseString("ok"))
    }
    case GET(Path("/server/status")) => {
      logger.info("/server/status")
      Future(ResponseString("ok"))
    }


  }

  override implicit def executionContext: ExecutionContext = ExecutionContext.Implicits.global
}
