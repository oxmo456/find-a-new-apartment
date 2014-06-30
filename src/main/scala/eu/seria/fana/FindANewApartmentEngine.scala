package eu.seria.fana

import akka.actor.{PoisonPill, ActorRef, Props, Actor}
import redis.clients.jedis.{JedisPoolConfig, JedisPool, Jedis}

case class Start()

case class Stop()

case class Update()

object FindANewApartmentEngine {

  def props(config: Config): Props = Props(new FindANewApartmentEngine(config))

}

private class FindANewApartmentEngine(config: Config) extends Actor {

  import context._

  def apartmentsExtractor: ActorRef = system.actorOf(ApartmentsExtractor.props(config, self))

  lazy val latestApartmentsFilter: ActorRef = system.actorOf(LatestApartmentsFilter.props(jedisPool))


  lazy val jedisPool = new JedisPool(new JedisPoolConfig(), config.jedis.host, config.jedis.port)

  def started: Receive = {
    case LatestApartments(apartments) => {
      println(apartments)
    }
    case ApartmentsExtracted(apartments) => {
      latestApartmentsFilter ! FilterLatestApartments(apartments)
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

  override def postStop(): Unit = {
    jedisPool.destroy()
    super.postStop()
  }
}