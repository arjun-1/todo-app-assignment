import java.util.UUID

import javafx.event.{ActionEvent, EventHandler}
import models.{Task, TaskFX}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{BooleanProperty, StringProperty}
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.TableColumn._
import scalafx.scene.control._
import scalafx.scene.control.cell.{CheckBoxTableCell, TextFieldTableCell}
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
    isDoneProperty.onChange((source, oldValue, newValue) =>
      println(s"$source $oldValue $newValue"))
    textProperty.onChange((source, oldValue, newValue) =>
      println(s"$source $oldValue $newValue"))
  }
}

object Main extends JFXApp {
  val view = new View
  val presenter = new Presenter(view)
  stage = new PrimaryStage {
    title = "My application"
    scene = new Scene {
      root = view.vbox
    }
  }
}

class View {
  val tasks = ObservableBuffer[TaskFX](
    new TaskFX(UUID.randomUUID(), false, "hoi"),
    new TaskFX(UUID.randomUUID(), true, "doei"))

  val createButton = new Button("+")
  val saveButton = new Button("Save")
  val loadButton = new Button("Load")
  val deleteMenuItem = new MenuItem("Delete")
  val tableContextMenu: ContextMenu = new ContextMenu(deleteMenuItem)

  val table = new TableView[TaskFX](tasks) {
    columns ++= List(
      new TableColumn[TaskFX, java.lang.Boolean] {
        text = "Done"
        cellValueFactory = _.value.isDoneProperty
          .asInstanceOf[ObservableValue[java.lang.Boolean, java.lang.Boolean]]
        cellFactory = CheckBoxTableCell.forTableColumn(this)
      },
      new TableColumn[TaskFX, String] {
        text = "Text"
        cellFactory = TextFieldTableCell.forTableColumn[TaskFX]()
        cellValueFactory = { _.value.textProperty }
      }
    )
    contextMenu = tableContextMenu
    editable = true
    columnResizePolicy = TableView.ConstrainedResizePolicy
  }

  val hBox = new HBox(createButton, saveButton, loadButton)
  val vbox = new VBox(table, hBox)

}

class Presenter(view: View) {

  val createHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    System.out.println("Create")
    val task = new TaskFX(UUID.randomUUID(), isDone = false, text = "")
    view.tasks.+=(task)
  }

  val deleteHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    println("Delete")
    val row = view.table.getFocusModel.getFocusedCell.getRow
    println(row)
    if (row >= 0) view.tasks.remove(row)
  }

  val saveHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    System.out.println("Save")
  }

  val loadHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    System.out.println("Load")
  }

  view.createButton.onAction = createHandler
  view.deleteMenuItem.onAction = deleteHandler
  view.saveButton.onAction = saveHandler
  view.loadButton.onAction = loadHandler

}
