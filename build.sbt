import sbt.Keys.libraryDependencies

lazy val commonSettings = Seq(scalaVersion := "2.12.6")

lazy val client = (project in file("client"))
  .settings(
    commonSettings,
    name := "client",
    version := "0.1",
    libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"
  )

lazy val server = (project in file("server"))
  .settings(
    commonSettings,
    name := "server",
    version := "0.1"
  )

lazy val root = (project in file(".")).aggregate(client, server)

scalafmtOnCompile in ThisBuild := true
