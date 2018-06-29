package com.arjun

import java.util.UUID

import cats.data.EitherT

import scala.concurrent.Future

trait TaskServiceTrait {

  def get(): EitherT[Future, TaskError, List[Task]]
  def getByTaskId(taskId: UUID): EitherT[Future, TaskError, Task]
  def insert(task: Task): EitherT[Future, TaskError, Task]
  def update(taskId: UUID, task: Task): EitherT[Future, TaskError, Task]
  def delete(id: UUID): EitherT[Future, TaskError, Unit]
}
