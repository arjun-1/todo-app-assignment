package com.arjun.presenter

import java.util.UUID

import com.arjun.model.TaskFX
import com.arjun.view.View
import javafx.event.{ActionEvent, EventHandler}
import scalafx.collections.ObservableBuffer
import scalafx.event.subscriptions.Subscription

import scala.annotation.tailrec

class Presenter(view: View, model: ObservableBuffer[TaskFX]) {

  def addIsDoneListener(task: TaskFX): Subscription = task.isDoneProperty.onChange(
    (source, oldValue, newValue) => {
    val newTask = task.copy(isDone = newValue)
    model.set(model.indexOf(task), newTask)
    println(s"$source $oldValue $newValue")
    addIsDoneListener(newTask)
  })

  model.foreach(addIsDoneListener)
  model.foreach(_.textProperty.onChange((source, oldValue, newValue) => {
    println(s"$source $oldValue $newValue")
  }))

  val createHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    System.out.println("Create")
    val task = TaskFX(UUID.randomUUID(), isDone = false, text = "")
    addIsDoneListener(task)

    task.textProperty.onChange((source, oldValue, newValue) =>
      println(s"$source $oldValue $newValue"))

    model.+=(task)
    println(model.toList)
  }

  val deleteHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    println("Delete")
    val row = view.table.getFocusModel.getFocusedCell.getRow
    println(row)
    if (row >= 0) model.remove(row)
  }

  val editHandler: EventHandler[
    javafx.scene.control.TableColumn.CellEditEvent[TaskFX, String]] =
    (cellEditEvent: javafx.scene.control.TableColumn.CellEditEvent[TaskFX,
                                                                   String]) => {
      val task = cellEditEvent.getRowValue
      val row = cellEditEvent.getTablePosition.getRow
      val newText = cellEditEvent.getNewValue

      model.set(row, task.copy(text = newText))
      println(model)
      System.out.println("edit done2")
    }

  view.createButton.onAction = createHandler
  view.deleteMenuItem.onAction = deleteHandler
  view.textColumn.onEditCommit = editHandler

}
