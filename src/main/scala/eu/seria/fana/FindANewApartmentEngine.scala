package eu.seria.fana

import akka.actor.{PoisonPill, ActorRef, Props, Actor}

case class Start()

case class Stop()

case class Update()

object FindANewApartmentEngine {

  def props(config: Config): Props = Props(new FindANewApartmentEngine(config))

}

private class FindANewApartmentEngine(config: Config) extends Actor {

  import context._

  def apartmentsExtractor: ActorRef = system.actorOf(ApartmentsExtractor.props(config, self))

  def started: Receive = {
    case ApartmentsExtracted(apartments) => {

      println(s"YES ! ${apartments.length}")

      sender ! PoisonPill
    }
    case Update() => {
      apartmentsExtractor ! ExtractApartments()
      system.scheduler.scheduleOnce(config.updateInterval, self, Update())
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