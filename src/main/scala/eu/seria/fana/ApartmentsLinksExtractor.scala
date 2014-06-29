package eu.seria.fana

import akka.actor.{Props, Actor}
import org.jsoup.Jsoup
import eu.seria.utils.jsoup._
import collection.JavaConversions._
import org.jsoup.nodes.Document

case class ExtractApartmentsLinks()

case class ApartmentsLinksExtracted(apartmentsLinks: List[String])

object ApartmentsLinksExtractor {

  def props(config: Config): Props = Props(new ApartmentsLinksExtractor(config))

  val ApartmentsAnchors = "table.top-feature .description a, table.regular-ad .description a"

}

class ApartmentsLinksExtractor(config: Config) extends Actor {

  import ApartmentsLinksExtractor._

  def apartmentsListingURL: String = config.baseURL + config.apartmentsListingURL

  implicit def htmlDocument: Document = Jsoup.connect(apartmentsListingURL).get()

  def apartmentsLinks(implicit document: Document): List[String] = {
    htmlDocument.select(ApartmentsAnchors).map(config.baseURL + _.href).toList
  }

  override def receive: Receive = {
    case ExtractApartmentsLinks() => {
      //TODO handle failures...
      sender ! ApartmentsLinksExtracted(apartmentsLinks)
    }
  }
}
