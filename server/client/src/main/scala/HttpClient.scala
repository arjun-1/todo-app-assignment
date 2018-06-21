import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import argonaut.DecodeJson
import cats.data.EitherT
import com.arjun.Task
import cats.instances.future._
import argonaut.Argonaut._
import argonaut.ArgonautShapeless._
import argonaut._
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport

import scala.concurrent.Future

class HttpClient extends ArgonautSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private def httpRequest(method: HttpMethod,
                          path: String,
                          body: RequestEntity) = {
    val uri =
      Uri.from(scheme = "http", host = "localhost", path = path, port = 8080)
    Http().singleRequest(
      request = HttpRequest(method = HttpMethods.GET, uri = uri))
  }

  private def unmarshall[A](entity: ResponseEntity)(
      implicit decoder: DecodeJson[A]) =
    EitherT.right[String](Unmarshal(entity).to[A]).recoverWith {
      case e => EitherT.leftT[Future, A](s"can not parse $e")
    }

  def listTasks: EitherT[Future, String, List[Task]] =
    for {
      response <- EitherT.right(
        httpRequest(HttpMethods.GET, "/tasks", HttpEntity.Empty))
      tasks <- unmarshall[List[Task]](response.entity)
    } yield tasks

  def addTask(task: Task): EitherT[Future, String, Task] =
    for {
      requestBody <- EitherT.right(Marshal(task).to[RequestEntity])
      response <- EitherT.right(
        httpRequest(HttpMethods.POST, "/tasks", requestBody))
      task <- unmarshall[Task](response.entity)
    } yield task

  def updateTask(taskId: UUID, task: Task): EitherT[Future, String, Task] =
    for {
      requestBody <- EitherT.right(Marshal(task).to[RequestEntity])
      response <- EitherT.right(
        httpRequest(HttpMethods.PUT, "/tasks", requestBody))
      task <- unmarshall[Task](response.entity)
    } yield task

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
