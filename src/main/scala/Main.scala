import eu.seria.fana.Server
import eu.seria.utils.ApplicationMode
import eu.seria.utils.ApplicationMode._
import scala.util.Try

object Main {


  def main(arguments: Array[String]) {

    new Server(Try(ApplicationMode.withName(arguments.head)).getOrElse(DEV))

  }

}
