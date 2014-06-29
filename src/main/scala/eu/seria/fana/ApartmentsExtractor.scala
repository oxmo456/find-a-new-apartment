package eu.seria.fana

import akka.actor.{ActorRef, PoisonPill, Props, Actor}
import eu.seria.utils._


case class ApartmentsExtracted(apartments: List[Apartment])

case class ExtractApartments()

object ApartmentsExtractor {

  def props(config: Config, manager: ActorRef): Props = Props(new ApartmentsExtractor(config, manager))

}

class ApartmentsExtractor(config: Config, manager: ActorRef) extends Actor {

  import context._

  def apartmentsLinksExtractor: ActorRef = system.actorOf(ApartmentsLinksExtractor.props(config))

  def apartmentExtractor: ActorRef = system.actorOf(ApartmentExtractor.props(config))

  def handleExtractedApartments(remainingApartments: Counter, extractedApartments: List[Apartment]): Receive =
    if (remainingApartments > 0) {
      {
        case ApartmentExtracted(apartment) =>
          become(handleExtractedApartments(remainingApartments.decrease, apartment :: extractedApartments))
      }
    } else {
      manager ! ApartmentsExtracted(extractedApartments)
      noop
    }

  def handleExtractedApartmentsLinks: Receive = {
    case ApartmentsLinksExtracted(apartmentsLinks) => {
      apartmentsLinks.foreach(apartmentLink => {
        apartmentExtractor ! ExtractApartment(apartmentLink)
      })
      sender ! PoisonPill
      become(handleExtractedApartments(Counter(apartmentsLinks.size), Nil))
    }
  }

  override def receive: Receive = {
    case ExtractApartments() => {

      apartmentsLinksExtractor ! ExtractApartmentsLinks()
      become(handleExtractedApartmentsLinks)
    }
  }
}
