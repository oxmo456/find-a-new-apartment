package eu.seria.fana

import akka.actor.ActorSystem
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

class FindANewApartment(config: FanaConfig) {

  implicit val timeout = Timeout(5 seconds)

  val system = ActorSystem("fana")

  val engine = system.actorOf(FindANewApartmentEngine.props(config), "engine")

  def stop(): Unit = engine ! Stop()

  def start(): Unit = engine ! Start()

  def kill(): Unit = system.shutdown()

  def status(): Future[Any] = {
    engine ? Status()
  }

}
