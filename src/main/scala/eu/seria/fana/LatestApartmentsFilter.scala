package eu.seria.fana

import akka.actor.{ActorLogging, Props, Actor}
import redis.clients.jedis.JedisPool

case class FilterLatestApartments(apartments: List[Apartment])

case class LatestApartments(apartments: List[Apartment])

object LatestApartmentsFilter {

  def props(jedisPool: JedisPool, apartmentsSetKey: String): Props =
    Props(new LatestApartmentsFilter(jedisPool, apartmentsSetKey))

}

class LatestApartmentsFilter(jedisPool: JedisPool, apartmentsSetKey: String) extends Actor with ActorLogging {

  lazy val jedis = jedisPool.getResource

  def latestApartments(apartments: List[Apartment]): LatestApartments = {

    val transaction = jedis.multi()

    apartments.foreach(apartment => {
      transaction.sadd(apartmentsSetKey, apartment.sha1)
    })

    val insertionsResult = transaction.exec()

    LatestApartments(for {
      (apartment, index) <- apartments.zipWithIndex
      if (insertionsResult.get(index) == 1)
    } yield apartment)

  }

  override def postStop(): Unit = {
    jedisPool.returnResource(jedis)
    super.postStop()
  }

  override def receive: Receive = {
    case FilterLatestApartments(apartments) => {
      log.info(s"FilterLatestApartments(${apartments.length})")
      sender ! latestApartments(apartments)
    }
  }
}