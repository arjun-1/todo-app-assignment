import java.util.UUID

import javafx.event.{ActionEvent, EventHandler}
import models.{Task, TaskFX}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{BooleanProperty, StringProperty}
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer
import scalafx.event.EventHandlerDelegate
import scalafx.scene.Scene
import scalafx.scene.control.TableColumn._
import scalafx.scene.control._
import scalafx.scene.control.cell.{CheckBoxTableCell, TextFieldTableCell}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}

object models {
  case class Task(id: UUID, isDone: Boolean, text: String)

  implicit class RichTask(task: Task) {
    def toTaskFX = new TaskFX(task.id, task.isDone, task.text)
  }

  class TaskFX(id: UUID, isDone: Boolean, text: String)
      extends Task(id, isDone, text) {
    val isDoneProperty =
      new BooleanProperty(bean = this, name = "isDone", initialValue = isDone)
    val textProperty =
      new StringProperty(bean = this, name = "text", initialValue = text)
//    isDoneProperty.onChange((source, oldValue, newValue) =>
//      println(s"$source $oldValue $newValue"))
//    textProperty.onChange((source, oldValue, newValue) =>
//      println(s"$source $oldValue $newValue"))
  }
}

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

class View(model: ObservableBuffer[TaskFX]) {
  import Main.tasks

  val createButton = new Button("+")
//  val saveButton = new Button("Save")
//  val loadButton = new Button("Load")
  val deleteMenuItem = new MenuItem("Delete")
  val tableContextMenu: ContextMenu = new ContextMenu(deleteMenuItem)

  val blaHandler: EventHandler[
    javafx.scene.control.TableColumn.CellEditEvent[TaskFX, java.lang.Boolean]] =
    (_: javafx.scene.control.TableColumn.CellEditEvent[TaskFX,
                                                       java.lang.Boolean]) => {
      System.out.println("edit done1")
    }

  val doneColumn = new TableColumn[TaskFX, java.lang.Boolean] {
    text = "Done"
    cellValueFactory = _.value.isDoneProperty
      .asInstanceOf[ObservableValue[java.lang.Boolean, java.lang.Boolean]]
    cellFactory = CheckBoxTableCell.forTableColumn(this)
    onEditCommit = blaHandler
  }
  val textColumn = new TableColumn[TaskFX, String] {
    text = "Text"
    cellFactory = TextFieldTableCell.forTableColumn[TaskFX]()
    cellValueFactory = { _.value.textProperty }
  }

  val table = new TableView[TaskFX](tasks) {
    columns ++= List(
      doneColumn,
      textColumn
    )
    contextMenu = tableContextMenu
    editable = true
    columnResizePolicy = TableView.ConstrainedResizePolicy
  }

  val hBox = new HBox(createButton)//, saveButton, loadButton)
  val vbox = new VBox(table, hBox)

}

class Presenter(view: View, model: ObservableBuffer[TaskFX]) {

  model.foreach(task => task.isDoneProperty.onChange((source, oldValue, newValue) =>
      {
        model.set(model.indexOf(task), new TaskFX(task.id, newValue, task.text))
        println(s"$source $oldValue $newValue")
      }
    )
  )

  model.foreach(_.textProperty.onChange((source, oldValue, newValue) =>
    {
      println(s"$source $oldValue $newValue")
    }
  ))


  val createHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    System.out.println("Create")
    val task = new TaskFX(UUID.randomUUID(), isDone = false, text = "")
    task.isDoneProperty.onChange((source, oldValue, newValue) =>
      println(s"$source $oldValue $newValue"))
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
    (x: javafx.scene.control.TableColumn.CellEditEvent[TaskFX, String]) => {
      val task = x.getRowValue
      val row = x.getTablePosition.getRow
      val newText = x.getNewValue

      model.set(row, new TaskFX(task.id, task.isDone, newText))
      System.out.println("edit done2")
    }

//  val saveHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
//    System.out.println("Save")
//  }
//
//  val loadHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
//    System.out.println("Load")
//  }

  view.createButton.onAction = createHandler
  view.deleteMenuItem.onAction = deleteHandler
//  view.saveButton.onAction = saveHandler
//  view.loadButton.onAction = loadHandler
  view.textColumn.onEditCommit = editHandler

}
