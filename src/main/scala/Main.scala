import com.typesafe.config.ConfigFactory
import eu.seria.utils.Application
import eu.seria.utils.Application._
import scala.util.Try

object Main {

  def main(arguments: Array[String]) {

    val mode = Try(Application.withName(arguments.head)).getOrElse(DEV)

    println(s"mode $mode")

    val conf = ConfigFactory.load(mode.toString).withFallback(ConfigFactory.load())

    println(conf.getConfig("fana"))

    //new FindANewApartmentServer

  }

}
