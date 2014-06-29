package eu.seria.fana

import akka.actor.{Props, Actor}
import scala.concurrent.duration._

case class Start()

case class Stop()

case class Update()

private class FindANewApartmentEngine extends Actor {

  import context._

  def started: Receive = {
    case ApartmentsExtracted() => {

    }
    case Update() => {

      system.actorOf(Props[ApartmentsExtractor]) ! ExtractApartments()

      system.scheduler.scheduleOnce(5 seconds, self, Update())
    }
    case Stop() => {
      become(stopped)
    }
  }

  def stopped: Receive = {
    case Start() => {
      self ! Update()
      become(started)
    }
  }

  override def receive: Receive = stopped
}