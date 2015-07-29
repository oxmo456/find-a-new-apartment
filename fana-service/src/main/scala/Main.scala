import java.util.logging.Logger

import eu.seria.fana.Server
import eu.seria.utils.ApplicationMode
import eu.seria.utils.ApplicationMode._
import scala.util.Try
import eu.seria.utils._

object Main {


  def main(arguments: Array[String]) {

    println("YA...")

    new Server(Try(ApplicationMode.withName(arguments.head)).getOrElse(DEV))

  }

}
