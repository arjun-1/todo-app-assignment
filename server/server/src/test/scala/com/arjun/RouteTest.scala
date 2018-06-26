package com.arjun

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.arjun.Runner.{dataSource, databaseConfig}
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import argonaut.Argonaut._
import argonaut.ArgonautShapeless._
import argonaut._

import scala.concurrent.Await
import scala.concurrent.duration._

class RouteTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach with Routes {

  val databaseConfig = pureconfig
    .loadConfig[Configs.Main](ConfigFactory.load())
    .fold(err => sys.error(s"Error while loading config: $err"), identity)
    .database

  println(databaseConfig)

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
  override def beforeEach(): Unit = {
    flyway.setDataSource(dataSource)
    flyway.migrate()
  }
  override def afterEach(): Unit = flyway.clean()




  val (repositories, db) = new Db(slick.jdbc.H2Profile) -> slick.jdbc.H2Profile.api.Database.forDataSource(dataSource, None)

  val taskService = new TaskService(repositories, db)


  val taskId = UUID.fromString("c8f9a8bd-6d10-4c91-a668-d4cd52265db2")
  val userId = UUID.fromString("d124b278-1c79-4098-aad1-bdddd06c586b")
  val task = Task(Some(taskId), Some(userId), true, "hoi")
  "Foo" should {
    "list all tasks" in {
      Await.ready(db.run(repositories.tasks.insert(task)), 10 seconds)
      println(Await.result(db.run(repositories.tasks.get), 10 seconds))


      Get("/tasks") ~> routes ~> check {
        println("hoi")
        println(Await.result(responseEntity.toStrict(10 seconds), 10 seconds).data.utf8String)
        status shouldEqual StatusCodes.OK


        responseAs[List[Task]] shouldBe List(task)
      }
    }

    "create a task" in {
      Post("/tasks", task.copy(id = None)) ~> routes ~> check {
        println(Await.result(responseEntity.toStrict(10 seconds), 10 seconds).data.utf8String)

        status shouldEqual StatusCodes.OK
        responseAs[Task] shouldBe a [Task]
      }
    }

    "Get specific" in {
      Await.ready(db.run(repositories.tasks.insert(task)), 10 seconds)

      Get(s"/tasks/$taskId") ~> routes ~> check {
        println(Await.result(responseEntity.toStrict(10 seconds), 10 seconds).data.utf8String)

        status shouldEqual StatusCodes.OK
        responseAs[Task] shouldBe task
      }
    }

    "put specific" in {
      Await.ready(db.run(repositories.tasks.insert(task)), 10 seconds)
      val newTask = task.copy(text = "new text")

      Put(s"/tasks/$taskId", newTask) ~> routes ~> check {
        println(Await.result(responseEntity.toStrict(10 seconds), 10 seconds).data.utf8String)

        status shouldEqual StatusCodes.OK
        responseAs[Task] shouldBe newTask
      }
    }

    "delete" in {
      Await.ready(db.run(repositories.tasks.insert(task)), 10 seconds)

      Delete(s"/tasks/$taskId") ~> routes ~> check {
        println(Await.result(responseEntity.toStrict(10 seconds), 10 seconds).data.utf8String)
        status shouldEqual StatusCodes.NoContent
      }

      Get(s"/tasks/$taskId") ~> routes ~> check {
        println(Await.result(responseEntity.toStrict(10 seconds), 10 seconds).data.utf8String)
        status shouldEqual StatusCodes.NotFound
      }
    }






  }



}
