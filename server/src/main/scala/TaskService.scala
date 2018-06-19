import java.util.UUID

// Use H2Driver to connect to an H2 database_
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaskService(repositories: Db) {

  val db = Database.forConfig("h2mem1")

  def get(): Future[Seq[Task]] =
    for {
      _ <- db.run(repositories.taskTable.schema.create)
      _ <- db.run(
        repositories.taskTable += Task(Some(UUID.randomUUID()), true, "hoihoi"))
      tasks <- db.run(repositories.tasks.get())
    } yield tasks
  def getByTaskId(taskId: UUID): Future[Option[Task]] =
    db.run(repositories.tasks.getByTaskId(taskId))
  def getByUserId(userID: UUID): Future[Seq[Task]] =
    db.run(repositories.tasks.getByUserId(userID))
  def insert(task: Task): Future[Int] = {
    val taskId = UUID.randomUUID()
    val taskWithId = task.copy(id = Some(taskId))
    db.run(repositories.tasks.insert(taskWithId))
  }

}
