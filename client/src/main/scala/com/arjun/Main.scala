package com.arjun

import java.util.UUID

import com.arjun.model.TaskFX
import com.arjun.presenter.Presenter
import com.arjun.view.View
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene

object Main extends JFXApp {

  val tasks: ObservableBuffer[TaskFX] = ObservableBuffer[TaskFX](
    new TaskFX(UUID.randomUUID(), false, "hoi"),
    new TaskFX(UUID.randomUUID(), true, "doei"))
  val view = new View(tasks)

  val presenter = new Presenter(view, tasks)
  stage = new PrimaryStage {
    title = "My application"
    scene = new Scene {
      root = view.vbox
    }
  }
}
