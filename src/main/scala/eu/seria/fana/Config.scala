package eu.seria.fana

import scala.concurrent.duration.FiniteDuration

case class Config(baseURL: String, apartmentsListingURL: String, updateInterval: FiniteDuration)
