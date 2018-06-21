package com.arjun

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers
import argonaut.Argonaut._
import argonaut.ArgonautShapeless._
import argonaut._
import cats.data.EitherT
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Routes extends ArgonautSupport {

  def taskService: TaskService

  def completeResult[A](statusCode: StatusCode, result: EitherT[Future, String, A])(
      implicit encoder: EncodeJson[A]) = onComplete(result.value) {
    case Success(Right(success)) => complete(statusCode -> success)
    case Success(Left(error)) =>
      complete(StatusCodes.InternalServerError -> error)
    case Failure(error) =>
      complete(StatusCodes.InternalServerError -> error.getLocalizedMessage)
  }

  val route = {
    path ("tasks") {
      get {
        completeResult(StatusCodes.OK, taskService.get())
      } ~ post {
        entity(as[Task]) { task =>
          completeResult(StatusCodes.OK, taskService.insert(task))
        }
      }
    } ~ path("tasks" / PathMatchers.JavaUUID) { taskId =>
      get {
        completeResult(StatusCodes.OK, taskService.getByTaskId(taskId))
      } ~ put {
        entity(as[Task]) { task =>
          completeResult(StatusCodes.OK, taskService.update(taskId, task))
        }
      } ~ delete {
        completeResult(StatusCodes.NoContent, taskService.delete(taskId))
      }
    } ~ path("users" / PathMatchers.JavaUUID / "tasks") { userId =>
      completeResult(StatusCodes.OK, taskService.getByUserId(userId))
    }
  }

}
