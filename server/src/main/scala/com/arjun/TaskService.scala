package com.arjun

import java.util.UUID

import cats.data.EitherT
import cats.instances.future._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaskService(repositories: Db, db: JdbcProfile#API#Database) {

  def get(): EitherT[Future, String, List[Task]] =
    EitherT.right(db.run(repositories.tasks.get()).map(_.toList))
  def getByTaskId(taskId: UUID): EitherT[Future, String, Task] =
    EitherT.fromOptionF(db.run(repositories.tasks.getByTaskId(taskId)),
                        "task not found")
  def getByUserId(userID: UUID): EitherT[Future, String, List[Task]] =
    EitherT.right(db.run(repositories.tasks.getByUserId(userID)).map(_.toList))
  def insert(task: Task): EitherT[Future, String, Task] =
    for {
      _ <- if (task.id.isEmpty) EitherT.rightT[Future, String]()
      else EitherT.leftT[Future, Unit]("task id supplied")
      taskId = UUID.randomUUID()
      taskWithId = task.copy(id = Some(taskId))
      _ <- EitherT.right(db.run(repositories.tasks.insert(taskWithId)))
    } yield taskWithId

  def update(taskId: UUID, task: Task): EitherT[Future, String, Task] =
    for {
      _ <- task.id
        .filter(_ != taskId)
        .map(id =>
          EitherT.leftT[Future, Unit](s"inconsistent task id $id != $taskId"))
        .getOrElse(EitherT.rightT[Future, String]())
      _ <- getByTaskId(taskId) // make sure it exists
      _ <- EitherT.right(db.run(repositories.tasks.update(taskId, task)))
    } yield task.copy(id = Some(taskId))

  def delete(id: UUID): EitherT[Future, String, Unit] = {
    for {
      _ <- getByTaskId(id)
      _ <- EitherT.right(db.run(repositories.tasks.delete(id)))
    } yield ()
  }

}
