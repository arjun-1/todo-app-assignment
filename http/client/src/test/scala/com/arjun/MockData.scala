package com.arjun

import java.util.UUID

object MockData {

  val taskId = UUID.fromString("c8f9a8bd-6d10-4c91-a668-d4cd52265db2")
  val task = Task(Some(taskId), true, "hoi")
}
