package eu.seria.fana

import akka.actor.{ActorLogging, Props, Actor}
import org.jsoup.nodes.Document
import org.jsoup.Jsoup
import eu.seria.utils.jsoup._

import collection.JavaConversions._
import scala.util.Try

case class ExtractApartment(apartmentLink: String)

case class ApartmentExtracted(apartment: Apartment)

object ApartmentExtractor {

  def props(config: FanaConfig) = Props(new ApartmentExtractor(config))

  val MetaDescription = "head meta[name=description]"
  val Price = "span[itemprop=price] strong"
  val Images = "#ImageThumbnails ul li img"
  val ImageName = """\$_\d{2}.JPG""".r
  val ExtractPrice = """\d+,?""".r
  val ExtractLatLng = """LatLng\((-?\d+\.\d+),\s*(-?\d+\.\d+)\)""".r
  val ExtractCode = """(?<=/)\d+(?=[?#]|$)""".r

}

class ApartmentExtractor(config: FanaConfig) extends Actor with ActorLogging {

  import ApartmentExtractor._

  def description(implicit htmlDocument: Document): String = {
    htmlDocument.select(MetaDescription).first().content
  }

  def price(implicit htmlDocument: Document): Option[Float] = {
    val price = htmlDocument.select(Price).first().textNodes().head.toString
    Try(ExtractPrice.findAllIn(price).mkString.replace(',', '.').toFloat).toOption
  }


  def images(implicit htmlDocument: Document): List[String] = {
    htmlDocument.select(Images).map(element => {
      ImageName.replaceAllIn(element.src, """\$_20.JPG""")
    }).toList
  }

  def apartmentCode(apartmentLink: String): Option[String] = {
    ExtractCode.findFirstIn(apartmentLink)
  }

  def apartmentLocation(apartmentCode: Option[String]): Option[ApartmentLocation] = {
    apartmentCode.fold[Option[ApartmentLocation]](None) {
      code => {
        val apartmentMapPage = scala.io.Source.fromURL(config.baseUrl + config.apartmentMapUrl + code).mkString
        ExtractLatLng.findAllIn(apartmentMapPage).matchData.foldLeft[Option[ApartmentLocation]](None) {
          (result, regExpMatch) => {
            Option(ApartmentLocation(regExpMatch.group(1).toFloat, regExpMatch.group(2).toFloat))
          }
        }
      }
    }
  }


  override def receive: Receive = {
    case ExtractApartment(apartmentLink) => {
      log.info(s"ExtractApartment(...${apartmentLink.substring(apartmentLink.length - 20)})")

      implicit val htmlDocument = Jsoup.parse(scala.io.Source.fromURL(apartmentLink).mkString)

      sender ! ApartmentExtracted(Apartment(
        apartmentLink,
        description,
        apartmentLocation(apartmentCode(apartmentLink)),
        price,
        images
      ))
    }
  }

}