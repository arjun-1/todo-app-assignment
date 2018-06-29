import sbt.Keys.libraryDependencies

lazy val commonSettings = Seq(scalaVersion := "2.12.6")

val akkaHttpVersion = "10.1.3"
val catsVersion = "1.0.1"
val argonautShapelessVersion = "1.2.0-M4"
val akkaHttpArgonautVersion = "1.21.0"
val scalaTestVersion = "3.0.5"

lazy val httpServer = (project in file("http/server"))
  .settings(
    commonSettings,
    name := "http-server",
    version := "0.1",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "de.heikoseeberger" %% "akka-http-argonaut" % akkaHttpArgonautVersion,
      "com.github.alexarchambault" %% "argonaut-shapeless_6.2" % argonautShapelessVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "com.typesafe.slick" %% "slick" % "3.2.3",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
      "com.h2database" % "h2" % "1.4.197",
      "org.postgresql" % "postgresql" % "42.2.2",
      "org.flywaydb" % "flyway-core" % "5.1.1",
      "com.github.pureconfig" %% "pureconfig" % "0.9.1"
    )
  )
lazy val httpClient = (project in file("http/client"))
  .settings(
    commonSettings,
    name := "http-client",
    version := "0.1",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "de.heikoseeberger" %% "akka-http-argonaut" % akkaHttpArgonautVersion,
      "com.github.alexarchambault" %% "argonaut-shapeless_6.2" % argonautShapelessVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    )
  )
  .dependsOn(httpServer)

lazy val guiClient = (project in file("gui-client"))
  .settings(
    commonSettings,
    name := "client",
    version := "0.1",
    Defaults.itSettings,
    libraryDependencies ++=
      Seq(
        "org.scalafx" %% "scalafx" % "8.0.144-R12",
        "org.scalatest" %% "scalatest" % "3.0.5" % Test,
        "org.scalamock" %% "scalamock" % "4.1.0" % Test
      ),
    fork in run := true
  )
  .dependsOn(httpClient)

lazy val root =
  (project in file(".")).aggregate(guiClient, httpServer, httpClient)

scalafmtOnCompile in ThisBuild := true
scalacOptions += "-Ypartial-unification"
