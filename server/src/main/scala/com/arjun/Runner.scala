package com.arjun

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.flywaydb.core.Flyway

object Runner extends App with Routes {

  val flyway = new Flyway()
  flyway.setDataSource("jdbc:postgresql://localhost:5432/postgres",
                       "postgres",
                       "password")
  flyway.migrate()

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val repositories = new Db(slick.jdbc.PostgresProfile)

  import slick.jdbc.PostgresProfile.api._
  //  val db = Database.forConfig("h2mem1")
  val db = Database.forURL("jdbc:postgresql://localhost:5432/postgres",
    driver = "org.postgresql.Driver",
    user = "postgres",
    password = "password")
  val taskService = new TaskService(repositories, db)
  Http().bindAndHandle(route, "localhost", 8080)
}
