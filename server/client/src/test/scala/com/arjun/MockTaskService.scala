package com.arjun

import java.util.UUID

import cats.data.EitherT

import scala.concurrent.{ExecutionContext, Future}
import cats.instances.future._
import MockData._




class MockTaskService(implicit executionContext: ExecutionContext) extends TaskServiceTrait {


  def get(): EitherT[Future, TaskError, List[Task]] = EitherT.rightT[Future, TaskError](List(task))

  def getByTaskId(taskId: UUID): EitherT[Future, TaskError, Task] = EitherT.rightT[Future, TaskError](task)

  def getByUserId(userID: UUID): EitherT[Future, TaskError, List[Task]] = EitherT.rightT[Future, TaskError](List(task))

  def insert(userId: UUID, task: Task): EitherT[Future, TaskError, Task] = EitherT.rightT[Future, TaskError](task)

  def update(taskId: UUID,
             userId: UUID,
             task: Task): EitherT[Future, TaskError, Task] = EitherT.rightT[Future, TaskError](task)

  def delete(id: UUID): EitherT[Future, TaskError, Unit] = EitherT.rightT[Future, TaskError]()
}

