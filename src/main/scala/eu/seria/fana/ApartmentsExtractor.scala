package eu.seria.fana

import akka.actor.{ActorRef, PoisonPill, Props, Actor}
import eu.seria.utils._


case class ApartmentsExtracted()

case class ExtractApartments()

class ApartmentsExtractor extends Actor {

  import context._


  def apartmentsLinksExtractor: ActorRef = system.actorOf(Props[ApartmentsLinksExtractor])

  def apartmentExtractor: ActorRef = system.actorOf(Props[ApartmentExtractor])

  def handleExtractedApartments(apartmentsCount: Counter): Receive = if (apartmentsCount > 0) {
    {
      case ApartmentExtracted() => {

        become(handleExtractedApartments(apartmentsCount.decrease))
      }
    }
  } else {
    noop
  }

  def handleExtractedApartmentsLinks: Receive = {
    case ApartmentsLinksExtracted(apartmentsLinks) => {
      apartmentsLinks.foreach(apartmentLink => {
        apartmentExtractor ! ExtractApartment(apartmentLink)
      })
      sender ! PoisonPill
      become(handleExtractedApartments(Counter(apartmentsLinks.size)))
    }
  }

  override def receive: Receive = {
    case ExtractApartments() => {
      apartmentsLinksExtractor ! ExtractApartmentsLinks()
      become(handleExtractedApartmentsLinks)
    }
  }
}
