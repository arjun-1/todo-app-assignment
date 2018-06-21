package com.arjun

import java.util.UUID
//import slick.jdbc.H2Profile.api._
import slick.jdbc.PostgresProfile.api._

class Db {

  class Tasks(tag: Tag) extends Table[Task](tag, "tasks") {
    def id = column[UUID]("task_id", O.PrimaryKey)
    def userId = column[UUID]("user_id")
    def isDone = column[Boolean]("is_done")
    def text = column[String]("text")

    def * =
      (id.?, userId.?, isDone, text) <> ((Task.apply _).tupled, Task.unapply)
  }

  val taskTable = TableQuery[Tasks]

  object tasks {
    def get(): DBIO[Seq[Task]] = taskTable.result
    def getByTaskId(taskId: UUID): DBIO[Option[Task]] =
      taskTable.filter(_.id === taskId).result.headOption
    def getByUserId(userID: UUID): DBIO[Seq[Task]] =
      taskTable.filter(_.userId === userID).result
    def update(taskId: UUID, task: Task): DBIO[Int] =
      taskTable.filter(_.id === taskId).update(task)
    def insert(task: Task): DBIO[Int] = taskTable += task
    def delete(taskId: UUID): DBIO[Int] =
      taskTable.filter(_.id === taskId).delete
  }

}
