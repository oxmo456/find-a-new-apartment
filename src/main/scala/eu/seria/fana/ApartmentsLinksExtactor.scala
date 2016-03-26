package eu.seria.fana

import akka.actor.{ActorLogging, Props, Actor}
import org.jsoup.Jsoup
import eu.seria.utils.jsoup._
import collection.JavaConversions._
import org.jsoup.nodes.Document

import scala.util.{Failure, Success, Try}

case class ExtractApartmentsLinks()

case class ApartmentsLinksExtracted(apartmentsLinks: List[String])

object ApartmentsLinksExtractor {

  def props(config: FanaConfig): Props = Props(new ApartmentsLinksExtractor(config))

  val ApartmentsAnchors = "table.top-feature .description a, table.regular-ad .description a"

}

class ApartmentsLinksExtractor(config: FanaConfig) extends Actor with ActorLogging {

  import ApartmentsLinksExtractor._

  def apartmentsListingURLs: List[String] = config.apartmentsListingUrls.map(config.baseUrl + _)

  implicit def htmlDocuments: List[Try[Document]] = apartmentsListingURLs.map((apartmentsListingURL) => {
    Try {
      val response = Jsoup.connect(apartmentsListingURL)
        .userAgent(config.userAgent)
        .timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute()
      if (response.statusCode() == 307) {
        Jsoup.connect(response.header("Location")).userAgent(config.userAgent).timeout(5000).execute().parse()
      } else {
        response.parse()
      }
    }
  })

  def apartmentsLinks(implicit documents: List[Try[Document]]): List[String] = {
    documents.flatMap(document => {
      document match {
        case Success(document) => {
          document.select(ApartmentsAnchors).map(config.baseUrl + _.href).toList
        }
        case Failure(e) => {
          log.error(e, s"Error while trying to extract apartments listing")
          Nil
        }
      }
    })
  }

  override def receive: Receive = {
    case ExtractApartmentsLinks() => {
      log.info(s"ExtractApartmentsLinks() $apartmentsLinks")
      sender ! ApartmentsLinksExtracted(apartmentsLinks)
    }
  }
}