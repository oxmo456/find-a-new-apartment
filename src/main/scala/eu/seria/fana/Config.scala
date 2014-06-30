package eu.seria.fana

import scala.concurrent.duration.FiniteDuration

case class RedisConfig(host: String, port: Int)

case class Config(baseURL: String,
                  apartmentsListingURL: String,
                  updateInterval: FiniteDuration,
                  jedis: RedisConfig)
