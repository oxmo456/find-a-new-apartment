package eu.seria.fana

import akka.actor.{ActorRef, Props, Actor}
import eu.seria.utils._
import akka.routing.RoundRobinPool
import akka.event.Logging


case class ApartmentsExtracted(apartments: List[Apartment])

case class ExtractApartments()

object ApartmentsExtractor {

  def props(config: FanaConfig, manager: ActorRef): Props = Props(new ApartmentsExtractor(config, manager))

}

class ApartmentsExtractor(config: FanaConfig, manager: ActorRef) extends Actor {

  import context._

  val log = Logging(system, this)

  lazy val apartmentsLinksExtractor: ActorRef = context.actorOf(ApartmentsLinksExtractor.props(config)
    .withRouter(RoundRobinPool(nrOfInstances = 4)), "apartments-links-extractor")

  lazy val apartmentExtractor: ActorRef = context.actorOf(ApartmentExtractor.props(config)
    .withRouter(RoundRobinPool(nrOfInstances = 8)), "apartment-extractor")

  def handleExtractedApartments(remainingApartments: Counter, extractedApartments: List[Apartment]): Receive = {
    case ApartmentExtracted(apartment) =>
      log.info(s"$remainingApartments ApartmentExtracted(${apartment.sha1})")
      val remaining = remainingApartments.decrease
      if (remaining == 0) {
        manager ! ApartmentsExtracted(apartment :: extractedApartments)
        become(receive)
      } else {
        become(handleExtractedApartments(remaining, apartment :: extractedApartments))
      }
  }

  def handleExtractedApartmentsLinks: Receive = {
    case ApartmentsLinksExtracted(apartmentsLinks) => {
      log.info(s"ApartmentsLinksExtracted(${apartmentsLinks.length})")
      apartmentsLinks.foreach(apartmentLink => {
        apartmentExtractor ! ExtractApartment(apartmentLink)
      })
      become(handleExtractedApartments(Counter(apartmentsLinks.size), Nil))
    }
  }

  override def receive: Receive = {
    case ExtractApartments() => {
      log.info("ExtractApartments()")
      apartmentsLinksExtractor ! ExtractApartmentsLinks()
      become(handleExtractedApartmentsLinks)
    }
  }
}
