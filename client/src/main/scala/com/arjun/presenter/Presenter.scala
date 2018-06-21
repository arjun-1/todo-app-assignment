package com.arjun.presenter

import java.util.UUID

import com.arjun.model.TaskFX
import com.arjun.view.View
import javafx.event.{ActionEvent, EventHandler}
import scalafx.collections.ObservableBuffer
import scalafx.event.subscriptions.Subscription

class Presenter(view: View, model: ObservableBuffer[TaskFX]) {

  def subscribeIsDone(task: TaskFX): Subscription = task.isDoneProperty.onChange(
    (_, _, newIsDone) => {
    val newTask = task.copy(isDone = newIsDone)
    subscribeIsDone(newTask)
    model.set(model.indexOf(task), newTask)
    println(model)
  })

  model.foreach(subscribeIsDone)

  val createHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    System.out.println("Create")
    val task = TaskFX(UUID.randomUUID(), isDone = false, text = "")
    subscribeIsDone(task)
    model += task
    println(model)
  }

  val deleteHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    println("Delete")
    val row = view.table.getFocusModel.getFocusedCell.getRow
    if (row >= 0) model.remove(row)
    println(model)
  }

  val editHandler: EventHandler[
    javafx.scene.control.TableColumn.CellEditEvent[TaskFX, String]] =
    (cellEditEvent: javafx.scene.control.TableColumn.CellEditEvent[TaskFX,
                                                                   String]) => {
      println("Edit")
      val task = cellEditEvent.getRowValue
      val row = cellEditEvent.getTablePosition.getRow
      val newText = cellEditEvent.getNewValue
      val newTask = task.copy(text = newText)
      subscribeIsDone(newTask)
      model.set(row, newTask)
      println(model)
    }

  view.createButton.onAction = createHandler
  view.deleteMenuItem.onAction = deleteHandler
  view.textColumn.onEditCommit = editHandler

}
