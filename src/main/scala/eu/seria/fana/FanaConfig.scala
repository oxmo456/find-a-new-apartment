package eu.seria.fana

import com.typesafe.config.Config
import scala.concurrent.duration._


case class FanaConfig(conf: Config) {

  val baseUrl = conf.getString("baseUrl")

  val apartmentsListingURL = conf.getString("apartmentsListingURL")

  val updateInterval = conf.getInt("updateInterval") seconds

  object redis {

    val host = conf.getString("redis.host")

    val port = conf.getInt("redis.port")

    val apartmentsSetKey = conf.getString("redis.apartmentsSetKey")

  }

}
