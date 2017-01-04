name := """dataconsumer"""
organization := "org.dw.consumer"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
libraryDependencies += ws
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.10.1.1"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.0"
libraryDependencies += "com.h2database" % "h2" % "1.4.193"
libraryDependencies += "joda-time" % "joda-time" % "2.9.7"

