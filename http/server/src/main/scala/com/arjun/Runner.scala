package com.arjun

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway

object Runner extends App with Routes {

  val databaseConfig = pureconfig
    .loadConfig[Configs.Main](ConfigFactory.load())
    .fold(err => sys.error(s"Error while loading config: $err"), identity)
    .database

  val dataSource = new HikariDataSource()
//  dataSource.setJdbcUrl("jdbc:h2:mem:test1;DATABASE_TO_UPPER=false")
//  dataSource.setDriverClassName("org.h2.Driver")
//  dataSource.setUsername("")
//  dataSource.setPassword("")

  dataSource.setJdbcUrl(databaseConfig.url)
  dataSource.setDriverClassName(databaseConfig.driverClassName)
  dataSource.setUsername(databaseConfig.username)
  dataSource.setPassword(databaseConfig.password)

  val flyway = new Flyway()
  flyway.setDataSource(dataSource)
  flyway.migrate()

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val (repositories, db) =
    new Db(slick.jdbc.PostgresProfile) -> slick.jdbc.PostgresProfile.api.Database
      .forDataSource(dataSource, None)

  val taskService = new TaskService(repositories, db)
  Http().bindAndHandle(routes, "localhost", 8080)
}
