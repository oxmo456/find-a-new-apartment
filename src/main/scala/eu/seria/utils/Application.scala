package eu.seria.utils

object Application extends Enumeration {
  type DeploymentMode = Value
  val DEV = Value("dev")
  val PROD = Value("prod")
}


