package eu.seria.fana

case class Apartment(link: String,
                     description: String,
                     price: Option[Float],
                     images: List[String])
