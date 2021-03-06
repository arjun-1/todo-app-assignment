package com

import com.arjun.Task
import com.arjun.model.TaskFX

package object arjun {

  implicit class RichTask(task: Task) {
    def toTaskFX =
      task.id.fold(sys.error("no task id"))(id =>
        TaskFX(id, task.isDone, task.text))
  }

  implicit class RichTaskFX(task: TaskFX) {
    def toTask = Task(id = Some(task.id), task.isDone, task.text)
  }
}
