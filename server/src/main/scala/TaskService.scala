import java.util.UUID

import cats.data.EitherT
import cats.instances.future._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaskService(repositories: Db) {

  val db = Database.forConfig("h2mem1")

  def get(): EitherT[Future, String, Seq[Task]] =
    for {
      _ <- EitherT.right(db.run(repositories.taskTable.schema.create))
      _ <- EitherT.right(
        db.run(
          repositories.taskTable += Task(Some(UUID.randomUUID()),
                                         true,
                                         "hoihoi")))
      tasks <- EitherT.right(db.run(repositories.tasks.get()))
    } yield tasks
  def getByTaskId(taskId: UUID): EitherT[Future, String, Task] =
    EitherT.fromOptionF(db.run(repositories.tasks.getByTaskId(taskId)), "no")
  def getByUserId(userID: UUID): EitherT[Future, String, Seq[Task]] =
    EitherT.right(db.run(repositories.tasks.getByUserId(userID)))
  def insert(task: Task): EitherT[Future, String, Task] = {

    for {
      _ <- if (task.id.isEmpty) EitherT.rightT[Future, String]()
      else EitherT.leftT[Future, Int]("no")
      taskId = UUID.randomUUID()
      taskWithId = task.copy(id = Some(taskId))
      _ <- EitherT.right(db.run(repositories.tasks.insert(taskWithId)))
    } yield taskWithId
  }

}
