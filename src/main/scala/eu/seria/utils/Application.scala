package eu.seria.utils

object Application extends Enumeration {
  type Mode = Value
  val DEV = Value("dev")
  val PROD = Value("prod")
}


