package eu.seria.fana

import akka.actor.Actor

case class ExtractApartmentsLinks()

case class ApartmentsLinksExtracted(apartmentsLinks: List[String])

class ApartmentsLinksExtractor extends Actor {
  override def receive: Receive = {
    case ExtractApartmentsLinks() =>
  }
}
