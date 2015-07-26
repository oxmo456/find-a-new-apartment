import sbt._
import Keys._

object FindANewApartmentBuild extends Build {

  lazy val root = Project(id = "find-a-new-apartment",
    base = file(".")) aggregate (fanaService)

  lazy val fanaService = Project(id = "fana-service",
    base = file("fana-service"))


}