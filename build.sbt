
name := "crawling_app"

version := "0.1"

scalaVersion := "2.12.3"
libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.8.3",
  "com.lihaoyi" %% "fastparse" % "0.4.4",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
