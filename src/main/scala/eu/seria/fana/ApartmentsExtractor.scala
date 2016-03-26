package eu.seria.fana

import akka.actor.{ActorLogging, Actor, Props}
import akka.pattern.{pipe, ask}
import scala.concurrent.duration._
import akka.routing.RoundRobinPool
import akka.util.Timeout
import scala.concurrent.Future

case class ApartmentsExtracted(apartments: List[Apartment])

case class ExtractApartments()

object ApartmentsExtractor {

  def props(config: FanaConfig): Props = Props(new ApartmentsExtractor(config))

}


class ApartmentsExtractor(config: FanaConfig) extends Actor with ActorLogging {

  import context.dispatcher
  import scala.language.postfixOps

  implicit val timeout = Timeout(30 seconds)

  lazy val apartmentsLinksExtractor = context.actorOf(ApartmentsLinksExtractor.props(config)
    .withRouter(RoundRobinPool(nrOfInstances = 4)), "apartments-links-extractor")

  lazy val apartmentExtractor = context.actorOf(ApartmentExtractor.props(config)
    .withRouter(RoundRobinPool(nrOfInstances = 30)), "apartment-extractor")

  def extractApartmentsLinks: Future[List[String]] = {
    ask(apartmentsLinksExtractor, ExtractApartmentsLinks()).mapTo[ApartmentsLinksExtracted].map(_.apartmentsLinks)
  }

  def extractApartments(apartmentsLinks: List[String]): Future[List[Apartment]] = {
    Future.sequence(for {
      apartmentLink <- apartmentsLinks
    } yield {
      ask(apartmentExtractor, ExtractApartment(apartmentLink)).mapTo[ApartmentExtracted].map(_.apartment)
    })
  }

  override def receive: Receive = {
    case ExtractApartments() => {
      log.info("ExtractApartments()")

      (for {
        apartmentsLinks <- extractApartmentsLinks
        apartments <- extractApartments(apartmentsLinks)
      } yield {
        ApartmentsExtracted(apartments)
      }) pipeTo sender

    }


  }
}
