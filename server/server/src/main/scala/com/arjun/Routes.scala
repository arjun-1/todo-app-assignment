package com.arjun

import java.util.UUID

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers
import argonaut.Argonaut._
import argonaut.ArgonautShapeless._
import argonaut._
import cats.data.EitherT
import com.arjun.TaskError.TaskNotFound
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Routes extends ArgonautSupport {

  def taskService: TaskServiceTrait

  def errorMapper(taskError: TaskError): StatusCode = taskError match {
    case TaskError.TaskNotFound(_)          => StatusCodes.NotFound
    case TaskError.InconsistentTaskId(_, _) => StatusCodes.BadRequest
    case TaskError.TaskIdSupplied           => StatusCodes.BadRequest
  }

  def completeResult[A](
      statusCode: StatusCode,
      result: EitherT[Future, TaskError, A])(implicit encoder: EncodeJson[A]) =
    onComplete(result.value) {
      case Success(Right(success)) => complete(statusCode -> success)
      case Success(Left(error)) =>
        complete(errorMapper(error))
      case Failure(error) =>
        complete(StatusCodes.InternalServerError -> error.getLocalizedMessage)
    }

  val routes = {
    path("tasks") {
      get {
        completeResult(StatusCodes.OK, taskService.get())
      } ~ post {
        entity(as[Task]) { task =>
          // Todo: extract userId from auth header
          completeResult(
            StatusCodes.OK,
            taskService.insert(
              UUID.fromString("6d453fe9-09f7-442d-b50c-4487a8ea8db4"),
              task))
        }
      }
    } ~ path("tasks" / PathMatchers.JavaUUID) { taskId =>
      get {
        completeResult(StatusCodes.OK, taskService.getByTaskId(taskId))
      } ~ put {
        entity(as[Task]) { task =>
          completeResult(
            StatusCodes.OK,
            taskService.update(
              taskId,
              UUID.fromString("6d453fe9-09f7-442d-b50c-4487a8ea8db4"),
              task))
        }
      } ~ delete {
        completeResult(StatusCodes.NoContent, taskService.delete(taskId))
      }
    } ~ path("users" / PathMatchers.JavaUUID / "tasks") { userId =>
      completeResult(StatusCodes.OK, taskService.getByUserId(userId))
    }
  }

}
