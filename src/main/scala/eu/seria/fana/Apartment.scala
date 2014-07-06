package eu.seria.fana

case class Apartment(link: String,
                     description: String,
                     location: Option[ApartmentLocation],
                     price: Option[Float],
                     images: List[String]) {


  lazy val sha1: String = eu.seria.utils.sha1(link,
    description,
    price.getOrElse(0).toString,
    images.mkString
  )

}

case class ApartmentLocation(latitude: Float, longitude: Float)
