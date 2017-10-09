
name := "crawling_app"

version := "0.1"

scalaVersion := "2.12.3"
libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.8.3",
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
