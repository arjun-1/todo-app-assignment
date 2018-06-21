package com.arjun

import java.util.UUID
import java.util.concurrent.{Callable, CompletionStage, Executor, FutureTask}

import cats.instances.future._
import com.arjun.model.TaskFX
import com.arjun.presenter.Presenter
import com.arjun.view.View
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene

import scala.concurrent._
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}
import scala.util.{Failure, Success}

object Main extends JFXApp {

//  val tasks: ObservableBuffer[TaskFX] = ObservableBuffer[TaskFX](
//    new TaskFX(UUID.randomUUID(), false, "hoi"),
//    new TaskFX(UUID.randomUUID(), true, "doei"))

  val tasks: ObservableBuffer[TaskFX] = ObservableBuffer[TaskFX]()

//  val completionStage = toJava(fooTasksFX)

  import scala.collection.JavaConversions._
  import scala.collection.mutable.ListBuffer

  val view = new View(tasks)

  val presenter = new Presenter(view, tasks)
  stage = new PrimaryStage {
    title = "My application"
    scene = new Scene {
      root = view.vbox
    }
  }

}
