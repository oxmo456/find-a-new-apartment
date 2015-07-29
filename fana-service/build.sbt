name := "fana-service"

version := "1.0"

organization := "eu.seria"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.3",
  "org.jsoup" % "jsoup" % "1.7.3",
  "redis.clients" % "jedis" % "2.4.2",
  "net.databinder" %% "unfiltered-netty-server" % "0.8.0",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.1",
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.play" %% "play-json" % "2.2.1",
  "org.twitter4j" % "twitter4j-core" % "4.0.2"
)

resolvers ++= Seq(
  "jboss repo" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

assemblyJarName in assembly := "fana-service.jar"

assemblyMergeStrategy in assembly := {
  case PathList(path@_*) if path.last == "io.netty.versions.properties" => MergeStrategy.concat
  case x => val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

Revolver.settings
