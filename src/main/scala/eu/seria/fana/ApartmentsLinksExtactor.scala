package eu.seria.fana

import akka.actor.{ActorLogging, Props, Actor}
import org.jsoup.Jsoup
import eu.seria.utils.jsoup._
import collection.JavaConversions._
import org.jsoup.nodes.Document

case class ExtractApartmentsLinks()

case class ApartmentsLinksExtracted(apartmentsLinks: List[String])

object ApartmentsLinksExtractor {

  def props(config: FanaConfig): Props = Props(new ApartmentsLinksExtractor(config))

  val ApartmentsAnchors = "table.top-feature .description a, table.regular-ad .description a"

}

class ApartmentsLinksExtractor(config: FanaConfig) extends Actor with ActorLogging {

  import ApartmentsLinksExtractor._

  def apartmentsListingURL: String = config.baseUrl + config.apartmentsListingUrl

  implicit def htmlDocument: Document = Jsoup.parse(scala.io.Source.fromURL(apartmentsListingURL).mkString)

  def apartmentsLinks(implicit document: Document): List[String] = {
    document.select(ApartmentsAnchors).map(config.baseUrl + _.href).toList
  }

  override def receive: Receive = {
    case ExtractApartmentsLinks() => {
      //TODO handle failures...

      log.info("ExtractApartmentsLinks()")

      sender ! ApartmentsLinksExtracted(apartmentsLinks)
    }
  }
}