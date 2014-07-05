import eu.seria.fana.server.FindANewApartmentServer
import eu.seria.utils.ApplicationMode
import eu.seria.utils.ApplicationMode._
import scala.util.Try

object Main {


  def main(arguments: Array[String]) {

    new FindANewApartmentServer(Try(ApplicationMode.withName(arguments.head)).getOrElse(DEV))

  }

}
