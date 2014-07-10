package eu.seria.fana

import akka.actor.{Props, Actor}
import redis.clients.jedis.JedisPool
import play.api.libs.json.{JsArray, JsNumber, JsString, JsObject}
import akka.event.Logging
import collection.JavaConversions._

case class StoreApartments(apartments: List[Apartment])

case class ApartmentsStored(apartments: List[Apartment])

case class FindApartment(id: String)

case class FindLatestApartments()

case class FindLatestApartmentsResult(apartments: String)

case class FindApartmentResult(apartment: Option[String])


object ApartmentsStorage {

  def props(jedisPool: JedisPool, apartmentsKey: String) = Props(new ApartmentsStorage(jedisPool, apartmentsKey))

}

class ApartmentsStorage(jedisPool: JedisPool, apartmentsKey: String) extends Actor {

  lazy val jedis = jedisPool.getResource

  val log = Logging(context.system, this)

  def apartmentToJson(apartment: Apartment) = JsObject(
    "link" -> JsString(apartment.link) ::
      "title" -> JsString(apartment.title) ::
      "description" -> JsString(apartment.description) ::
      "address" -> JsString(apartment.address) ::
      "lat" -> JsNumber(apartment.location.fold[Float](0)(_.latitude)) ::
      "lng" -> JsNumber(apartment.location.fold[Float](0)(_.longitude)) ::
      "price" -> JsNumber(apartment.price.getOrElse[Float](0)) ::
      "images" -> JsArray(apartment.images.map(JsString(_)))
      :: Nil)

  def apartmentTitleAndSha1ToJson(apartment: Apartment) = JsObject(
    "sha1" -> JsString(apartment.sha1) ::
      "title" -> JsString(apartment.title) :: Nil
  )

  def findLatestApartments: String = {
    s"""{"apartments":[${jedis.lrange(apartmentsKey, -50L, -1L).mkString(",")}]}"""
  }

  override def receive: Receive = {
    case FindLatestApartments() => sender ! FindLatestApartmentsResult(findLatestApartments)
    case FindApartment(id) => sender ! FindApartmentResult(Option(jedis.get(id)))
    case StoreApartments(apartments) => {

      log.info(s"StoreApartments(${apartments.length})")

      val transaction = jedis.multi()

      apartments.foreach(apartment => {
        transaction.set(apartment.sha1, apartmentToJson(apartment).toString)
        transaction.rpush(apartmentsKey, apartmentTitleAndSha1ToJson(apartment).toString)
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