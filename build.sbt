import sbt._
import Keys._


val freeStyleVersion = "0.3.1"
val circeVersion = "0.8.0"
val fs2Version = "0.9.7"

addCompilerPlugin("org.scalameta" %% "paradise" % "3.0.0-M9" cross CrossVersion.full)

val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "ch.qos.logback" % "logback-classic" % "1.1.7",
    "org.typelevel"  %% "cats-core" % "1.0.0-MF",
    "org.typelevel"  %% "cats-free" % "1.0.0-MF",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test",
    "co.fs2"   %% "fs2-core" % fs2Version,
    "co.fs2"   %% "fs2-cats" % "0.3.0",
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0",
    "com.rabbitmq" % "amqp-client" % "4.2.0"
  ),
  resolvers += Resolver.typesafeRepo("releases")
)

logBuffered in Test := false

crossScalaVersions in ThisBuild := Seq("2.11.11", "2.12.3", "2.13.0-M2")

scalaVersion in ThisBuild := "2.12.3"

lazy val root = project
  .in(file("."))
  .settings(
  name := "free-redis",
    moduleName := "free-redis",
    mainClass in (Compile, run) := Some("com.strad.evan.Main")
)
  .aggregate(client)
  .dependsOn(client)

lazy val client = project
  .settings(
  name := "client",
    moduleName := "client")
  .settings(sharedSettings)
