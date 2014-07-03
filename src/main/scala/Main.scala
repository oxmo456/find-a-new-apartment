import com.typesafe.config.ConfigFactory
import eu.seria.fana.server.FindANewApartmentServer
import eu.seria.utils.Application
import eu.seria.utils.Application._
import scala.util.Try

object Main {

  def main(arguments: Array[String]) {


    new FindANewApartmentServer(Try(Application.withName(arguments.head)).getOrElse(DEV))

  }

}
