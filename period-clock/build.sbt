// Turn this project into a Scala.js project by importing these settings
enablePlugins(ScalaJSPlugin)

name := "PeriodClock"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.6"

persistLauncher in Compile := true

persistLauncher in Test := false

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.0.0",
  "org.scala-js" %%% "scalajs-dom" % "0.8.0",
  "org.scalatest" %%% "scalatest" % "3.0.0-M1"
)

jsDependencies += RuntimeDOM % "test"
