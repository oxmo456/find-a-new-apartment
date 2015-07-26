import java.util.logging.Logger

import eu.seria.fana.Server
import eu.seria.utils.ApplicationMode
import eu.seria.utils.ApplicationMode._
import scala.util.Try
import eu.seria.utils._

object Main {


  def main(arguments: Array[String]) {

    println(s"??? ${System.getenv("DOCKER_HOST")}")
    println(s"??? ${System.getenv("DEV_SERVER_IP")}")

    println(s"host:$hostname")

    println(s"ip:${hostIPs.mkString(" ")}")

    new Server(Try(ApplicationMode.withName(arguments.head)).getOrElse(DEV))

  }

}
