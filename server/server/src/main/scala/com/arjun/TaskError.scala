package com.arjun

import java.util.UUID

sealed trait TaskError
object TaskError {
  final case class TaskNotFound(taskId: UUID) extends TaskError
  final case class InconsistentTaskId(taskId: UUID, otherTaskId: UUID)
      extends TaskError
  final case object TaskIdSupplied extends TaskError
}
