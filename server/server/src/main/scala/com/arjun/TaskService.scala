package com.arjun

import java.util.UUID

import cats.data.EitherT
import cats.instances.future._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class TaskService(repositories: Db, db: JdbcProfile#API#Database)(
    implicit executionContext: ExecutionContext)
    extends TaskServiceTrait {

  def get(): EitherT[Future, TaskError, List[Task]] =
    EitherT.right(db.run(repositories.tasks.get()).map(_.toList))
  def getByTaskId(taskId: UUID): EitherT[Future, TaskError, Task] =
    EitherT.fromOptionF(db.run(repositories.tasks.getByTaskId(taskId)),
                        TaskError.TaskNotFound(taskId))
  def getByUserId(userID: UUID): EitherT[Future, TaskError, List[Task]] =
    EitherT.right(db.run(repositories.tasks.getByUserId(userID)).map(_.toList))
  def insert(userId: UUID, task: Task): EitherT[Future, TaskError, Task] =
    for {
      _ <- if (task.id.isEmpty) EitherT.rightT[Future, TaskError]()
      else EitherT.leftT[Future, Unit](TaskError.TaskIdSupplied)
      taskId = UUID.randomUUID()
      taskWithIds = task.copy(id = Some(taskId), userId = Some(userId))
      _ <- EitherT.right(db.run(repositories.tasks.insert(taskWithIds)))
    } yield taskWithIds

  def update(taskId: UUID,
             userId: UUID,
             task: Task): EitherT[Future, TaskError, Task] =
    for {
      _ <- task.id
        .filter(_ != taskId)
        .map(id =>
          EitherT.leftT[Future, Unit](TaskError.InconsistentTaskId(id, taskId)))
        .getOrElse(EitherT.rightT[Future, TaskError]())
      _ <- getByTaskId(taskId) // make sure it exists
      _ <- EitherT.right(
        db.run(repositories.tasks
          .update(taskId, task.copy(id = Some(taskId), userId = Some(userId)))))
    } yield task.copy(id = Some(taskId), userId = Some(userId))

  def delete(id: UUID): EitherT[Future, TaskError, Unit] = {
    for {
      _ <- getByTaskId(id)
      _ <- EitherT.right(db.run(repositories.tasks.delete(id)))
    } yield ()
  }

}
