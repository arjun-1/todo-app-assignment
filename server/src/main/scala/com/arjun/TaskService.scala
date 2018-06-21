package com.arjun

import java.util.UUID

import cats.data.EitherT
import cats.instances.future._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaskService(repositories: Db) {

//  val db: H2Profile.backend.Database = Database.forConfig("h2mem1")

  val db = Database.forURL("jdbc:postgresql://localhost:5432/postgres",
                           driver = "org.postgresql.Driver",
                           user = "postgres",
                           password = "password")

  def get(): EitherT[Future, String, List[Task]] =
    EitherT.right(db.run(repositories.tasks.get()).map(_.toList))
  def getByTaskId(taskId: UUID): EitherT[Future, String, Task] =
    EitherT.fromOptionF(db.run(repositories.tasks.getByTaskId(taskId)), "no")
  def getByUserId(userID: UUID): EitherT[Future, String, List[Task]] =
    EitherT.right(db.run(repositories.tasks.getByUserId(userID)).map(_.toList))
  def insert(task: Task): EitherT[Future, String, Task] =
    for {
      _ <- if (task.id.isEmpty) EitherT.rightT[Future, String]()
      else EitherT.leftT[Future, Unit]("no")
      taskId = UUID.randomUUID()
      taskWithId = task.copy(id = Some(taskId))
      _ <- EitherT.right(db.run(repositories.tasks.insert(taskWithId)))
    } yield taskWithId

  def update(id: UUID, task: Task): EitherT[Future, String, Task] =
    for {
      _ <- task.id
        .filter(_ != id)
        .map(id => EitherT.leftT[Future, Unit]("bad"))
        .getOrElse(EitherT.rightT[Future, String]())
      _ <- getByTaskId(id) // make sure it exists
      _ <- EitherT.right(db.run(repositories.tasks.update(id, task)))
    } yield task.copy(id = Some(id))

  def delete(id: UUID): EitherT[Future, String, Unit] = {
    for {
      _ <- getByTaskId(id)
      _ <- EitherT.right(db.run(repositories.tasks.delete(id)))
    } yield ()
  }

}
