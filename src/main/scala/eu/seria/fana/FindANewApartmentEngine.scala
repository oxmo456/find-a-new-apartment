package eu.seria.fana

import akka.actor.{PoisonPill, ActorRef, Props, Actor}
import redis.clients.jedis.{JedisPoolConfig, JedisPool, Jedis}

case class Start()

case class Stop()

case class Update()

case class Status()

object FindANewApartmentEngine {

  def props(config: FanaConfig): Props = Props(new FindANewApartmentEngine(config))

}

private class FindANewApartmentEngine(config: FanaConfig) extends Actor {

  import context._

  def apartmentsExtractor = system.actorOf(ApartmentsExtractor.props(config, self))

  lazy val apartmentsStorage = system.actorOf(ApartmentsStorage.props(jedisPool))

  lazy val newApartmentsNotifier = system.actorOf(NewApartmentsNotifier.props(config))

  lazy val latestApartmentsFilter =
    system.actorOf(LatestApartmentsFilter.props(jedisPool, config.redis.apartmentsSetKey))

  lazy val jedisPool = new JedisPool(new JedisPoolConfig(), config.redis.host, config.redis.port)

  def started: Receive = {
    case Status() => sender ! "started"
    case ApartmentsStored(apartments) => newApartmentsNotifier ! SendNewApartmentsNotification(apartments)
    case LatestApartments(apartments) => apartmentsStorage ! StoreApartments(apartments)
    case ApartmentsExtracted(apartments) => {
      latestApartmentsFilter ! FilterLatestApartments(apartments)
      sender ! PoisonPill
    }
    case Update() => {
      apartmentsExtractor ! ExtractApartments()
      system.scheduler.scheduleOnce(config.updateInterval, self, Update())
    }
    case Stop() => become(stopped)
  }

  def stopped: Receive = {
    case Status() => sender ! "stopped"
    case Start() => {
      self ! Update()
      become(started)
    }
  }

  override def receive: Receive = stopped

  override def postStop(): Unit = {
    jedisPool.destroy()
    super.postStop()
  }
}