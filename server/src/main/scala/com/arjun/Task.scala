package com.arjun

import java.util.UUID

case class Task(id: Option[UUID],
                userId: Option[UUID],
                isDone: Boolean,
                text: String)
