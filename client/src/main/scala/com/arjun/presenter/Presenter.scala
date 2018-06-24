package com.arjun.presenter

import com.arjun.{HttpClient, HttpClientTrait, Task}
import com.arjun.model.TaskFX
import com.arjun.view.View
import javafx.event.{ActionEvent, EventHandler}
import scalafx.collections.ObservableBuffer
import scalafx.event.subscriptions.Subscription
import cats.instances.future._
import scalafx.application.Platform

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Presenter(view: View,
                model: ObservableBuffer[TaskFX],
                httpClient: HttpClientTrait) {
  def subscribeToIsDone(task: TaskFX): Subscription =
    task.isDoneProperty.onChange((_, _, newIsDone) => {
      println("Edit isDone")
      val futureTaskFX = httpClient
        .updateTask(task.id, task.copy(isDone = newIsDone).toTask)
        .map(_.toTaskFX)
        .fold(err => sys.error(s"while updating: $err"), identity)
      futureTaskFX.onComplete {
        case Success(taskFX) =>
          subscribeToIsDone(taskFX)
          Platform.runLater {
            model.set(model.indexOf(task), taskFX)
            println(model)
          }
        case Failure(err) =>
          sys.error(s"while updating: ${err.getLocalizedMessage}")
      }
    })

  def refreshTasks(): Unit = {
    println("List")
    val tasksFX = httpClient.listTasks
      .map(_.map(_.toTaskFX))
      .fold(err => sys.error(s"while fetching: $err"), identity)
    tasksFX.onComplete {
      case Success(tasksFX) =>
        tasksFX.foreach(subscribeToIsDone)
        Platform.runLater {
        model.setAll(tasksFX: _*)
//          model.foreach(subscribeToIsDone)
        println(model)
        }

      case Failure(err) =>
        sys.error(s"while fetching: ${err.getLocalizedMessage}")
    }
  }

//  Platform.runLater {
    refreshTasks()
//  }

  val createHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    System.out.println("Create")
    val task = Task(id = None, userId = None, isDone = false, text = "")

//    Platform.runLater {
      val futureTaskFX = httpClient
        .addTask(task)
        .map(_.toTaskFX)
        .fold(err => sys.error(s"while creating: $err"), identity)
      futureTaskFX.onComplete {
        case Success(taskFX) =>
          subscribeToIsDone(taskFX)
          Platform.runLater {
            model += taskFX
            println(model)
          }
        case Failure(err) =>
          sys.error(s"while creating: ${err.getLocalizedMessage}")
      }
//    }

  }

  val deleteHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    println("Delete")
    val row = view.getFocusedTableRow
    if (row >= 0) {
//      Platform.runLater {
        val futureTaskFX = httpClient
          .deleteTask(model.get(row).id)
          .fold(err => sys.error(s"while deleting: $err"), identity)
        futureTaskFX.onComplete {
          case Success(_) =>
          Platform.runLater {
            model.remove(row)
            println(model)
          }
          case Failure(err) =>
            sys.error(s"while deleting: ${err.getLocalizedMessage}")
        }
//      }
    }
  }

  val editHandler: EventHandler[
    javafx.scene.control.TableColumn.CellEditEvent[TaskFX, String]] =
    (cellEditEvent: javafx.scene.control.TableColumn.CellEditEvent[TaskFX,
                                                                   String]) => {
      println("Edit text")
      val task = cellEditEvent.getRowValue
      val row = cellEditEvent.getTablePosition.getRow
      val newText = cellEditEvent.getNewValue

//      Platform.runLater {
        val futureTaskFX = httpClient
          .updateTask(task.id, task.copy(text = newText).toTask)
          .map(_.toTaskFX)
          .fold(err => sys.error(s"while updating: $err"), identity)
        futureTaskFX.onComplete {
          case Success(taskFX) =>
            subscribeToIsDone(taskFX)
            Platform.runLater {
              model.set(row, taskFX)
              println(model)
            }
          case Failure(err) =>
            sys.error(s"while updating: ${err.getLocalizedMessage}")
        }
//      }
    }

  view.createButton.onAction = createHandler
  view.deleteMenuItem.onAction = deleteHandler
  view.textColumn.onEditCommit = editHandler

}
