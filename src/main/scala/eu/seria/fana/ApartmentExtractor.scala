package eu.seria.fana

import akka.actor.{ActorLogging, Props, Actor}
import org.jsoup.nodes.Document
import org.jsoup.Jsoup
import eu.seria.utils.jsoup._

import collection.JavaConversions._
import scala.util.{Success, Failure, Try}

case class ExtractApartment(apartmentLink: String)

case class ApartmentExtracted(apartment: Apartment)

object ApartmentExtractor extends {

  def props(config: FanaConfig) = Props(new ApartmentExtractor(config))

  val MetaDescription = "head meta[name=description]"
  val Address = ".ad-attributes tr:has(th:contains(Adresse)) td"
  val Title = "span[itemprop=name] h1"
  val Price = "span[itemprop=price] strong"
  val Images = "#ImageThumbnails ul li img"
  val ImageName = """\$_\d{2}.JPG""".r
  val ExtractPrice = """\d+,?""".r
  val ExtractLatLng = """LatLng\((-?\d+\.\d+),\s*(-?\d+\.\d+)\)""".r
  val ExtractCode = """(?<=/)\d+(?=[?#]|$)""".r

  val NotAvailable = "n/a"

}

class ApartmentExtractor(config: FanaConfig) extends Actor with ActorLogging {

  import ApartmentExtractor._

  def description(implicit htmlDocument: Document): String = {
    Try {
      htmlDocument.select(MetaDescription).first().content
    }.getOrElse(NotAvailable)
  }

  def title(implicit htmlDocument: Document): String = {
    Try {
      htmlDocument.select(Title).first().text
    }.getOrElse(NotAvailable)
  }

  def price(implicit htmlDocument: Document): Option[Float] = {
    Try {
      val price = htmlDocument.select(Price).first().textNodes().head.toString
      ExtractPrice.findAllIn(price).mkString.replace(',', '.').toFloat
    }.toOption
  }


  def images(implicit htmlDocument: Document): List[String] = {
    htmlDocument.select(Images).map(element => {
      ImageName.replaceAllIn(element.src, """\$_20.JPG""")
    }).toList
  }

  def address(implicit htmlDocument: Document): String = {
    Try {
      htmlDocument.select(Address).first().textNodes().head.toString
    }.getOrElse(NotAvailable)
  }

  def apartmentCode(apartmentLink: String): Option[String] = {
    ExtractCode.findFirstIn(apartmentLink)
  }

  def apartmentLocation(apartmentCode: Option[String]): Option[ApartmentLocation] = {
    apartmentCode.fold[Option[ApartmentLocation]](None) {
      code => {
        //TODO refactor document loading
        val apartmentMapPage = htmlDocument(config.baseUrl + config.apartmentMapUrl + code).getOrElse("").toString
        ExtractLatLng.findAllIn(apartmentMapPage).matchData.foldLeft[Option[ApartmentLocation]](None) {
          (result, regExpMatch) => {
            Option(ApartmentLocation(regExpMatch.group(1).toFloat, regExpMatch.group(2).toFloat))
          }
        }
      }
    }
  }

  //TODO refactor document loading
  def htmlDocument(link: String): Try[Document] = Try {
    val response = Jsoup.connect(link)
      .userAgent(config.userAgent)
      .timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute()
    if (response.statusCode() == 307) {
      Jsoup.connect(response.header("Location")).userAgent(config.userAgent).timeout(5000).execute().parse()
    } else {
      response.parse()
    }
  }

  override def receive: Receive = {
    case ExtractApartment(apartmentLink) => {
      log.info(s"ExtractApartment(...${apartmentLink.substring(apartmentLink.length - 20)})")

      htmlDocument(apartmentLink) match {
        case Success(document) => sender ! ApartmentExtracted(Apartment(
          apartmentLink,
          title(document),
          description(document),
          address(document),
          apartmentLocation(apartmentCode(apartmentLink)),
          price(document),
          images(document)
        ))
        case Failure(e) => log.error(e, s"Error while loading $apartmentLink")
      }


    }
  }

}