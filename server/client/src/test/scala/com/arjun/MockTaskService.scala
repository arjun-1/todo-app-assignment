package com.arjun

import java.util.UUID

import cats.data.EitherT

import scala.concurrent.{ExecutionContext, Future}
import cats.instances.future._





class MockTaskService(implicit executionContext: ExecutionContext) extends TaskServiceTrait {
  val taskId = UUID.fromString("c8f9a8bd-6d10-4c91-a668-d4cd52265db2")
  val userId = UUID.fromString("d124b278-1c79-4098-aad1-bdddd06c586b")
  val task = Task(Some(taskId), Some(userId), true, "hoi")

  def get(): EitherT[Future, TaskError, List[Task]] = EitherT.rightT[Future, TaskError](List(task))

  def getByTaskId(taskId: UUID): EitherT[Future, TaskError, Task] = EitherT.rightT[Future, TaskError](task)

  def getByUserId(userID: UUID): EitherT[Future, TaskError, List[Task]] = EitherT.rightT[Future, TaskError](List(task))

  def insert(userId: UUID, task: Task): EitherT[Future, TaskError, Task] = EitherT.rightT[Future, TaskError](task)

  def update(taskId: UUID,
             userId: UUID,
             task: Task): EitherT[Future, TaskError, Task] = EitherT.rightT[Future, TaskError](task)

  def delete(id: UUID): EitherT[Future, TaskError, Unit] = EitherT.rightT[Future, TaskError]()
}

