package eu.seria.fana

import akka.actor.Actor


case class ApartmentsFound()

case class FindApartments()

class ApartmentsFinder extends Actor {



  override def receive: Receive = {
    case FindApartments() => {




    }
  }
}
