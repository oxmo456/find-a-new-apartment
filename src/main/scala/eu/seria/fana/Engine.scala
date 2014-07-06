package eu.seria.fana

import akka.actor.{ActorLogging, Props, Actor}
import akka.routing.RoundRobinPool
import scala.util.Failure
import redis.clients.jedis.{JedisPoolConfig, JedisPool}


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

  lazy val latestApartmentsFilter = context.actorOf(LatestApartmentsFilter.props(jedisPool,config.redis.apartmentsSetKey))

  lazy val jedisPool = new JedisPool(new JedisPoolConfig(), config.redis.host, config.redis.port)

  lazy val apartmentsStorage = context.actorOf(ApartmentsStorage.props(jedisPool))

  lazy val newApartmentsNotifier = context.actorOf(NewApartmentsNotifier.props(config))

  def started: Receive = {
    case Status() => sender ! Status.Started
    case Stop() => become(stopped)
    case ApartmentsStored(apartments) => {
      log.info(s"ApartmentsStored(${apartments.length})")
      newApartmentsNotifier ! SendNewApartmentsNotification(apartments)
    }
    case LatestApartments(apartments) => {
      log.info(s"LatestApartments(${apartments.length})")
      apartmentsStorage ! StoreApartments(apartments)
    }
    case ApartmentsExtracted(apartments) => {
      log.info(s"ApartmentsExtracted(${apartments.length})")
      latestApartmentsFilter ! FilterLatestApartments(apartments)
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

  override def postStop(): Unit = {
    log.info("postStop")
    jedisPool.destroy()
    super.postStop()
  }

}
