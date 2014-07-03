package eu.seria.fana.server

import unfiltered.netty.{ServerErrorResponse, future}
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._
import unfiltered.netty.future.Plan.Intent
import unfiltered.request.{Path, GET}
import unfiltered.response.ResponseString
import eu.seria.fana.{ FanaConfig, FindANewApartment}
import eu.seria.utils.ApplicationMode._
import com.typesafe.config.ConfigFactory

@io.netty.channel.ChannelHandler.Sharable
class FindANewApartmentServer(mode: Mode) extends future.Plan with ServerErrorResponse {

  val conf = ConfigFactory.load(mode.toString).withFallback(ConfigFactory.load())

  unfiltered.netty.Http(8080)
    .handler(this)
    .beforeStop {
    findANewApartment.kill()
  }.run(s =>
    println("starting unfiltered app at localhost on port %s".format(s.port))
    )


  lazy val findANewApartment = new FindANewApartment(FanaConfig(conf.getConfig("fana")))

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
