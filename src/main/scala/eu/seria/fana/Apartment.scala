package eu.seria.fana

case class Apartment(link: String,
                     description: String,
                     price: Option[Float],
                     images: List[String]) {


  lazy val sha1: String = eu.seria.utils.sha1(link,
    description,
    price.getOrElse(0).toString,
    images.mkString
  )

}
