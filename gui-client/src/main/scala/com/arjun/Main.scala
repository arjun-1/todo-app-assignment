package com.arjun

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.arjun.model.TaskFX
import com.arjun.presenter.Presenter
import com.arjun.view.View
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene

object Main extends JFXApp {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val tasks: ObservableBuffer[TaskFX] = ObservableBuffer[TaskFX]()
  val view = new View(tasks)

  val presenter = new Presenter(view, tasks, new HttpTaskClient)
  stage = new PrimaryStage {
    title = "My application"
    scene = new Scene {
      root = view.vbox
    }
  }

}
