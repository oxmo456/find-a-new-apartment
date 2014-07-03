package eu.seria.fana

import akka.actor.{Props, Actor}

case class ApartmentLocationExtracted(apartmentLocation: ApartmentLocation)

case class ExtractApartmentLocation(apartmentMapLink: String)

object ApartmentLocationExtractor {

  def props = Props(new ApartmentLocationExtractor)

}

class ApartmentLocationExtractor extends Actor {
  override def receive: Receive = {
    case ExtractApartmentLocation(apartmentMapLink: String) =>
  }
}
