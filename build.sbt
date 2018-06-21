import sbt.Keys.libraryDependencies

lazy val commonSettings = Seq(scalaVersion := "2.12.6")

lazy val server = (project in file("server"))
  .settings(
    commonSettings,
    name := "server",
    version := "0.1",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.0.1",
      "de.heikoseeberger" %% "akka-http-argonaut" % "1.21.0",
      "com.github.alexarchambault" %% "argonaut-shapeless_6.2" % "1.2.0-M4",
      "com.typesafe.akka" %% "akka-http" % "10.1.3",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.3" % Test,
      "com.typesafe.slick" %% "slick" % "3.2.3",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
      "com.h2database" % "h2" % "1.4.197",
      "org.postgresql" % "postgresql" % "42.2.2",
      "org.flywaydb" % "flyway-core" % "5.1.1"
    )
  )

lazy val client = (project in file("client"))
  .settings(
    commonSettings,
    name := "client",
    version := "0.1",
    libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12",
    fork in run := true
  )
  .dependsOn(server)

lazy val root = (project in file(".")).aggregate(client, server)

scalafmtOnCompile in ThisBuild := true
scalacOptions += "-Ypartial-unification"
