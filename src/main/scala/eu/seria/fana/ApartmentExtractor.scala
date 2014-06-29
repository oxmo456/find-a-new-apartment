package eu.seria.fana

import akka.actor.Actor

case class ExtractApartment(apartmentLink: String)

case class ApartmentExtracted()

class ApartmentExtractor extends Actor {
  override def receive: Receive = {
    case ExtractApartment(apartmentLink) =>
  }
}
