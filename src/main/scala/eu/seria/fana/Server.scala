package eu.seria.fana

import unfiltered.netty.{ServerErrorResponse, future}
import scala.concurrent.{Future, ExecutionContext}
import unfiltered.netty.future.Plan.Intent
import unfiltered.request.{Seg, Path, GET}
import unfiltered.response.ResponseString
import eu.seria.utils.ApplicationMode._
import com.typesafe.config.ConfigFactory

@io.netty.channel.ChannelHandler.Sharable
class Server(mode: Mode) extends future.Plan with ServerErrorResponse {

  val conf = ConfigFactory.load(mode.toString).withFallback(ConfigFactory.load())

  unfiltered.netty.Http(8080)
    .handler(this)
    .beforeStop {
    findANewApartment.kill()
    //TODO remove auto start
  }.run { _ => println("yeha!"); findANewApartment.start()}


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
    case GET(Path(Seg("apartment" :: id :: Nil))) => {
      findANewApartment.findApartment(id).map(res => {
        ResponseString(res)
      })
    }

  }

  override implicit def executionContext: ExecutionContext = ExecutionContext.Implicits.global
}
