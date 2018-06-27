package com.arjun

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import argonaut.Parse
import cats.data.EitherT
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport
import org.scalatest.{BeforeAndAfterAll, WordSpec}
import cats.instances.future._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import org.scalatest.concurrent.{Futures, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}



class HttpClientTest extends WordSpec with Routes with ScalaFutures with BeforeAndAfterAll  {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher



  val taskService = new MockTaskService


//  override protected def beforeAll(): Unit = {
//    Await.ready(Http().bindAndHandle(routes, "localhost", 8080), 10 seconds)
//  }
  override protected def afterAll(): Unit = {
    Await.ready(system.terminate(), 10 seconds)
  }

  val server = Http().bindAndHandle(routes, "localhost", 8080)

  val httpClient = new HttpClient

  override implicit val patienceConfig =
    PatienceConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))

  "Foo" should {
    "bar" in {
      val foo = for {
        _ <- server
        x <- httpClient.listTasks.value
      } yield x
      whenReady(foo) { x => println(x)

      }
    }
  }


//  val client = HttpClient
//
//    val task = Task(None, None, true, "")
//
//  val result1 = client.addTask(task)
//  println(Await.result(result1.value, 10 seconds))

//  val string =
//  """
//    |{"text":"sdfsdfs","isDone":true,"userId":"bbec2d34-e097-4294-9c35-6653caf1024a","id":"b689cc42-1f77-4c24-8ad3-514efc949cf1"}
//  """.stripMargin
//
//
//  import argonaut._, Argonaut._, ArgonautShapeless._
//
//
//  val task = Task(None, None, true, "")
//  val encode = EncodeJson.of[Task]
//  val json: Json = encode(task)
//  println(json.nospaces)
//
//  val decode = DecodeJson.of[Task]
//  val result = decode.decodeJson(json)
//  println(result)



}
