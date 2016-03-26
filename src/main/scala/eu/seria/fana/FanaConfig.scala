package eu.seria.fana

import com.typesafe.config.Config
import scala.concurrent.duration._
import scala.collection.JavaConversions._


case class FanaConfig(conf: Config) {

  import scala.language.postfixOps

  val baseUrl = conf.getString("baseUrl")

  val apartmentsListingUrls: List[String] = conf.getStringList("apartmentsListingUrls").toList

  val apartmentMapUrl = conf.getString("apartmentMapUrl")

  val updateInterval = conf.getInt("updateInterval") milliseconds

  val notificationsRecipients: List[String] = conf.getStringList("notificationsRecipients").toList

  val notificationsEnabled = conf.getBoolean("notificationsEnabled")

  val apartmentsViewUrl = conf.getString("apartmentsViewUrl")

  val userAgent = conf.getString("userAgent")

  object redis {

    val host = conf.getString("redis.host")

    val port = conf.getInt("redis.port")

  }

  object twitter {

    val consumerKey = conf.getString("twitter.consumerKey")
    val consumerSecret = conf.getString("twitter.consumerSecret")
    val accessToken = conf.getString("twitter.accessToken")
    val accessTokenSecret = conf.getString("twitter.accessTokenSecret")

  }

}
