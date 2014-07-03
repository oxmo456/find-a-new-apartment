package eu.seria.fana

import akka.actor.{Props, Actor}
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

}

class ApartmentExtractor(config: FanaConfig) extends Actor {

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

  override def receive: Receive = {
    case ExtractApartment(apartmentLink) => {
      implicit val htmlDocument = Jsoup.parse(scala.io.Source.fromURL(apartmentLink).mkString)
      sender ! ApartmentExtracted(Apartment(
        apartmentLink,
        description,
        price,
        images
      ))
    }
  }

}
