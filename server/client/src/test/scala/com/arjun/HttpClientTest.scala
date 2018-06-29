package com.arjun

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import argonaut.Parse
import cats.data.EitherT
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport
import org.scalatest.{BeforeAndAfterAll, EitherValues, Matchers, WordSpec}
import cats.instances.future._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import org.scalatest.concurrent.{Futures, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}
import MockData.{task, _}



class HttpClientTest extends WordSpec with Matchers with Routes with ScalaFutures with BeforeAndAfterAll with EitherValues  {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher





  override protected def afterAll(): Unit = {
    Await.ready(system.terminate(), 10 seconds)
  }

  val taskService = new MockTaskService
  val server = Http().bindAndHandle(routes, "localhost", 8080)

  val httpClient = new HttpClient

  override implicit val patienceConfig =
    PatienceConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))

  "http client" should {
    "list tasks" in {
      val result = for {
        _ <- server
        tasks <- httpClient.listTasks.value
      } yield tasks
      whenReady(result) { x =>
        x.right.value shouldBe List(task)

      }
    }

    "create task" in {
      val result = for {
        _ <- server
        task <- httpClient.addTask(task).value
      } yield task
      whenReady(result) { x =>
        x.right.value shouldBe task

      }
    }

    "update task" in {
      val result = for {
        _ <- server
        task <- httpClient.updateTask(taskId, task).value
      } yield task
      whenReady(result) { x =>
        x.right.value shouldBe task

      }
    }

    "delete task" in {
      val result = for {
        _ <- server
        result <- httpClient.deleteTask(taskId).value
      } yield result
      whenReady(result) { x =>
        x.right.value shouldBe ()

      }
    }

    "find task by id" in {
      val result = for {
        _ <- server
        task <- httpClient.getTaskByTaskId(taskId).value
      } yield task
      whenReady(result) { x =>
        x.right.value shouldBe task

      }
    }
  }


}
