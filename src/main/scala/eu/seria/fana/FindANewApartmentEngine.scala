package eu.seria.fana

import akka.actor.{Props, Actor}
import redis.clients.jedis.{JedisPoolConfig, JedisPool}
import akka.routing.RoundRobinPool
import akka.event.Logging

case class Start()

case class Stop()

case class Update()

case class Status()

object FindANewApartmentEngine {

  def props(config: FanaConfig): Props = Props(new FindANewApartmentEngine(config))

}

private class FindANewApartmentEngine(config: FanaConfig) extends Actor {


  import context._

  val log = Logging(system, this)

  lazy val apartmentsExtractor = context.actorOf(ApartmentsExtractor.props(config, self)
    .withRouter(RoundRobinPool(nrOfInstances = 4)), "apartments-extractor")

  lazy val apartmentsStorage = context.actorOf(ApartmentsStorage.props(jedisPool)
    .withRouter(RoundRobinPool(nrOfInstances = 4)), "apartments-storage")

  lazy val newApartmentsNotifier = context.actorOf(NewApartmentsNotifier.props(config)
    .withRouter(RoundRobinPool(nrOfInstances = 4)), "new-apartments-notifier")

  lazy val latestApartmentsFilter =
    context.actorOf(LatestApartmentsFilter.props(jedisPool, config.redis.apartmentsSetKey)
      .withRouter(RoundRobinPool(nrOfInstances = 4)), "latest-apartments-filter")

  lazy val jedisPool = new JedisPool(new JedisPoolConfig(), config.redis.host, config.redis.port)

  def started: Receive = {
    case Status() => sender ! "started"
    case ApartmentsStored(apartments) => newApartmentsNotifier ! SendNewApartmentsNotification(apartments)
    case LatestApartments(apartments) => apartmentsStorage ! StoreApartments(apartments)
    case ApartmentsExtracted(apartments) => {
      log.info(s"ApartmentsExtracted(${apartments.length}})")
      latestApartmentsFilter ! FilterLatestApartments(apartments)
    }
    case Update() => {
      log.info("Update()")
      apartmentsExtractor ! ExtractApartments()
      system.scheduler.scheduleOnce(config.updateInterval, self, Update())
    }
    case Stop() => {
      log.info("Stop()")
      become(stopped)
    }
  }

  def stopped: Receive = {
    case Status() => sender ! "stopped"
    case Start() => {
      log.info("Start()")
      self ! Update()
      become(started)
    }
  }

  override def receive: Receive = stopped

  override def postStop(): Unit = {
    log.info("postStop")
    jedisPool.destroy()
    super.postStop()
  }
}