package com.arjun

import java.util.UUID

import akka.http.scaladsl.model.{HttpEntity, HttpMethods}
import cats.data.EitherT

import scala.concurrent.Future

trait HttpClientTrait {

  def listTasks: EitherT[Future, String, List[Task]]

  def addTask(task: Task): EitherT[Future, String, Task]

  def updateTask(taskId: UUID, task: Task): EitherT[Future, String, Task]

  def deleteTask(taskId: UUID): EitherT[Future, String, Unit]

  def getTaskByTaskId(taskId: UUID): EitherT[Future, String, Task]

  def listTasksByUserId(userId: UUID): EitherT[Future, String, List[Task]]
}
