package eu.seria.fana

import akka.actor.ActorSystem
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import eu.seria.fana.SortingOption.SortingOption

class FindANewApartment(config: FanaConfig) {

  implicit val timeout = Timeout(5 seconds)

  val system = ActorSystem("fana")

  val engine = system.actorOf(Engine.props(config), "engine")

  import system.dispatcher

  def stop(): Unit = engine ! Stop()

  def start(): Unit = engine ! Start()

  def kill(): Unit = system.shutdown()

  def status(): Future[Any] = {
    engine ? Status()
  }

  def findApartment(id: String): Future[String] = {
    ask(engine, FindApartment(id)).mapTo[FindApartmentResult].map {
      result => result.apartment.getOrElse("")
    }
  }

  def latestApartments(sortingOptions: SortingOption): Future[String] = {
    ask(engine, FindLatestApartments(sortingOptions)).mapTo[FindLatestApartmentsResult].map(_.apartments)
  }

}
