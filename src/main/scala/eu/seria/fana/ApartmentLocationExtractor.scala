package eu.seria.fana

import akka.actor.{Props, Actor}

case class ApartmentLocationExtracted(apartmentLocation: Option[ApartmentLocation])

case class ExtractApartmentLocation(apartmentMapLink: String)

object ApartmentLocationExtractor {

  def props = Props(new ApartmentLocationExtractor)

  val ExtractLatLng = """LatLng\((-?\d+\.\d+),\s*(-?\d+\.\d+)\)""".r

}

class ApartmentLocationExtractor extends Actor {

  import ApartmentLocationExtractor._

  override def receive: Receive = {
    case ExtractApartmentLocation(apartmentMapLink) => {
      val apartmentMapPage = scala.io.Source.fromURL(apartmentMapLink).mkString

      val apartmentLocation = ExtractLatLng.findAllIn(apartmentMapPage).matchData.foldLeft[Option[ApartmentLocation]](None) {
        (result, regExpMatch) => {
          Option(ApartmentLocation(regExpMatch.group(1).toFloat, regExpMatch.group(2).toFloat))
        }
      }

      sender ! ApartmentLocationExtracted(apartmentLocation)


    }
  }
}
