package com.arjun

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import argonaut.{DecodeJson, EncodeJson}
import cats.data.EitherT
import cats.instances.future._
import argonaut.Argonaut._
import argonaut.ArgonautShapeless._
import argonaut._
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport

import scala.concurrent.{Await, ExecutionContext, Future}

class HttpClient(implicit executionContext: ExecutionContext,
                 actorMaterializer: ActorMaterializer,
                 actorSystem: ActorSystem)
    extends ArgonautSupport
    with TaskClient {

  private def httpRequest(method: HttpMethod,
                          path: String,
                          body: RequestEntity) = {
    val uri =
      Uri.from(scheme = "http", host = "localhost", path = path, port = 8080)
    Http().singleRequest(
      request = HttpRequest(method = method, uri = uri, entity = body))
  }

  private def get[A](path: String)(
      implicit decoder: DecodeJson[A]): EitherT[Future, String, A] =
    for {
      response <- EitherT.right(
        httpRequest(HttpMethods.GET, path, HttpEntity.Empty))
      result <- unmarshall[A](response.entity)
    } yield result
  private def post[A](path: String, body: A)(
      implicit decoder: DecodeJson[A],
      encoder: EncodeJson[A]): EitherT[Future, String, A] =
    for {
      requestBody <- EitherT.right(Marshal(body).to[RequestEntity])
      response <- EitherT.right(
        httpRequest(
          HttpMethods.POST,
          path,
          requestBody.withContentType(ContentTypes.`application/json`)))
      result <- unmarshall[A](response.entity)
    } yield result

  private def put[A](path: String, body: A)(
      implicit decoder: DecodeJson[A],
      encoder: EncodeJson[A]): EitherT[Future, String, A] =
    for {
      requestBody <- EitherT.right(Marshal(body).to[RequestEntity])
      response <- EitherT.right(
        httpRequest(
          HttpMethods.PUT,
          path,
          requestBody.withContentType(ContentTypes.`application/json`)))
      result <- unmarshall[A](response.entity)
    } yield result

  private def unmarshall[A](entity: ResponseEntity)(
      implicit decoder: DecodeJson[A]) =
    EitherT.right[String](Unmarshal(entity).to[A]).recoverWith {
      case e => EitherT.leftT[Future, A](s"can not parse $e")
    }

  def listTasks: EitherT[Future, String, List[Task]] = get[List[Task]]("/tasks")

  def addTask(task: Task): EitherT[Future, String, Task] =
    post[Task]("/tasks", task)

  def updateTask(taskId: UUID, task: Task): EitherT[Future, String, Task] =
    put[Task](s"/tasks/$taskId", task)

  def deleteTask(taskId: UUID): EitherT[Future, String, Unit] =
    for {
      _ <- EitherT.right(
        httpRequest(HttpMethods.DELETE, s"/tasks/$taskId", HttpEntity.Empty))
    } yield ()

  def getTaskByTaskId(taskId: UUID): EitherT[Future, String, Task] = {
    for {
      response <- EitherT.right(
        httpRequest(HttpMethods.GET, s"/tasks/$taskId", HttpEntity.Empty))
      task <- unmarshall[Task](response.entity)
    } yield task
  }

  def listTasksByUserId(userId: UUID): EitherT[Future, String, List[Task]] = {
    for {
      response <- EitherT.right(
        httpRequest(HttpMethods.GET, s"/users/$userId/tasks", HttpEntity.Empty))
      tasks <- unmarshall[List[Task]](response.entity)
    } yield tasks
  }

}
