package com.arjun

import java.util.UUID

object MockData {

  val taskId = UUID.fromString("c8f9a8bd-6d10-4c91-a668-d4cd52265db2")
  val userId = UUID.fromString("d124b278-1c79-4098-aad1-bdddd06c586b")
  val task = Task(Some(taskId), Some(userId), true, "hoi")
}
