package eu.seria.fana

import akka.actor.{Props, Actor}
import redis.clients.jedis.JedisPool

case class FilterLatestApartments(apartments: List[Apartment])

case class LatestApartments(apartments: List[Apartment])

object LatestApartmentsFilter {

  def props(jedisPool: JedisPool): Props = Props(new LatestApartmentsFilter(jedisPool))

}

class LatestApartmentsFilter(jedisPool: JedisPool) extends Actor {

  lazy val jedis = jedisPool.getResource

  def latestApartments(apartments: List[Apartment]): LatestApartments = LatestApartments(
    apartments.filter(apartment => {
      jedis.get(apartment.sha1) == null
    }))

  override def receive: Receive = {
    case FilterLatestApartments(apartments) => sender ! latestApartments(apartments)
  }
}
