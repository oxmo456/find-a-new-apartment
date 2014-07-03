package eu.seria.fana

import akka.actor.{Props, Actor}
import redis.clients.jedis.JedisPool
import play.api.libs.json.{JsArray, JsNumber, JsString, JsObject}

case class StoreApartments(apartments: List[Apartment])

case class ApartmentsStored(apartments: List[Apartment])

object ApartmentsStorage {

  def props(jedisPool: JedisPool) = Props(new ApartmentsStorage(jedisPool))

}

class ApartmentsStorage(jedisPool: JedisPool) extends Actor {

  lazy val jedis = jedisPool.getResource

  def apartmentToJson(apartment: Apartment) = JsObject(
    "link" -> JsString(apartment.link) ::
      "description" -> JsString(apartment.description) ::
      "price" -> JsNumber(apartment.price.getOrElse[Float](0)) ::
      "images" -> JsArray(apartment.images.map(JsString(_)))
      :: Nil)

  override def receive: Receive = {
    case StoreApartments(apartments) => {
      val transaction = jedis.multi()

      apartments.foreach(apartment => {
        transaction.set(apartment.sha1, apartmentToJson(apartment).toString)
      })

      transaction.exec()

      sender ! ApartmentsStored(apartments)
    }
  }

  override def postStop(): Unit = {
    jedisPool.returnResource(jedis)
    super.postStop()
  }
}
