package com.arjun.model

import java.util.UUID

import scalafx.beans.property.{BooleanProperty, StringProperty}

case class TaskFX(id: UUID, isDone: Boolean, text: String) {
  val isDoneProperty =
    new BooleanProperty(bean = this, name = "isDone", initialValue = isDone)
  val textProperty =
    new StringProperty(bean = this, name = "text", initialValue = text)
}
