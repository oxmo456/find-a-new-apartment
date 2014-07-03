name := "find-a-new-apartment"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.3",
  "org.jsoup" % "jsoup" % "1.7.3",
  "redis.clients" % "jedis" % "2.4.2",
  "net.databinder" %% "unfiltered-netty-server" % "0.8.0",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.1",
  "com.typesafe" % "config" % "1.2.1"
)

resolvers ++= Seq(
  "jboss repo" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)
