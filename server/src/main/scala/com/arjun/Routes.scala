package com.arjun

import akka.http.scaladsl.model.StatusCodes
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

  def completeResult[A](result: EitherT[Future, String, A])(
      implicit encoder: EncodeJson[A]) = onComplete(result.value) {
    case Success(Right(success)) => complete(StatusCodes.OK -> success)
    case Success(Left(error)) =>
      complete(StatusCodes.InternalServerError -> error)
    case Failure(error) =>
      complete(StatusCodes.InternalServerError -> error.getLocalizedMessage)
  }

  val route = {
    pathPrefix("tasks") {
      get {
        completeResult(taskService.get())
      }
    } ~ path(PathMatchers.JavaUUID) { taskId =>
      get {
        completeResult(taskService.getByTaskId(taskId))
      } ~ put {
        entity(as[Task]) { task =>
          completeResult(taskService.update(taskId, task))
        }
      } ~ delete {
        completeResult(taskService.delete(taskId))
      }
    } ~ post {
      entity(as[Task]) { task =>
        completeResult(taskService.insert(task))
      }
    } ~ path("users" / PathMatchers.JavaUUID / "tasks") { userId =>
      completeResult(taskService.getByUserId(userId))
    }
  }

}
