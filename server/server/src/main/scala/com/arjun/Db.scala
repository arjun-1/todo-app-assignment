package com.arjun

import java.util.UUID

import slick.dbio.{DBIO => DB}
import slick.jdbc.JdbcProfile

class Db(profile: JdbcProfile) {
  import profile.api._

  private class Tasks(tag: Tag) extends Table[Task](tag, "tasks") {
    val id = column[UUID]("task_id", O.PrimaryKey)
    val isDone = column[Boolean]("is_done")
    val text = column[String]("text")

    def * =
      (id.?, isDone, text) <> ((Task.apply _).tupled, Task.unapply)
  }

  private val taskTable = TableQuery[Tasks]

  object tasks {
    def get(): DB[Seq[Task]] = taskTable.result
    def getByTaskId(taskId: UUID): DB[Option[Task]] =
      taskTable.filter(_.id === taskId).result.headOption
    def update(taskId: UUID, task: Task): DB[Int] =
      taskTable.filter(_.id === taskId).update(task)
    def insert(task: Task): DB[Int] = taskTable += task
    def delete(taskId: UUID): DB[Int] =
      taskTable.filter(_.id === taskId).delete
  }

}
