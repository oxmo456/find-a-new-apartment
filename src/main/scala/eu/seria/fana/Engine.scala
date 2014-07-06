package eu.seria.fana

import akka.actor.{ActorLogging, Props, Actor}
import akka.routing.RoundRobinPool
import scala.util.Failure


case class Start()

case class Stop()

case class Status()

case class Update()

object Status extends Enumeration {
  type Status = Value
  val Started = Value("started")
  val Stopped = Value("stopped")
}

object Engine {

  def props(config: FanaConfig): Props = Props(new Engine(config))

}

class Engine(config: FanaConfig) extends Actor with ActorLogging {

  import context._

  lazy val apartmentsExtractor = context.actorOf(ApartmentsExtractor.props(config)
    .withRouter(RoundRobinPool(nrOfInstances = 4)), "apartments-extractor")


  def started: Receive = {
    case Status() => sender ! Status.Started
    case Stop() => become(stopped)
    case ApartmentsExtracted(apartments) => {

      log.info(s"ApartmentsExtracted(${apartments.length})")

    }
    case Update() => {
      log.info("Update()")
      apartmentsExtractor ! ExtractApartments()

      system.scheduler.scheduleOnce(config.updateInterval, self, Update())
    }
    case e => log.warning(s"unknown message: ${e}")
  }

  def stopped: Receive = {
    case Status() => sender ! Status.Started
    case Start() => {
      log.info("Start()")
      become(started)
      self ! Update()
    }
  }


  override def receive: Receive = stopped


}
