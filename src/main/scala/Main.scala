import eu.seria.fana.{Config, FindANewApartment}
import scala.concurrent.duration._

object Main {

  def main(arguments: Array[String]) {

    new FindANewApartment(Config("http://www.kijiji.ca",
      "/b-appartement-condo/ville-de-montreal/villeray/k0c37l1700281?origin=ps",
      500 milliseconds))

  }

}
