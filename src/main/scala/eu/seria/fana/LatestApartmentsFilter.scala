package eu.seria.fana

import akka.actor.{Props, Actor}

case class FilterLatestApartments(apartments: List[Apartment])

case class LatestApartmentsFiltered(apartments: List[Apartment])

object LatestApartmentsFilter {

  def props: Props = Props(new LatestApartmentsFilter())

}

class LatestApartmentsFilter extends Actor {
  override def receive: Receive = {
    case FilterLatestApartments(apartments) => {



    }
  }
}
