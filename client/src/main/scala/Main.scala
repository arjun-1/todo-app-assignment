import java.util.UUID

import javafx.event.{ActionEvent, EventHandler}
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

class Note(id: UUID, isDone_ : Boolean, text_ : String) {
  val isDone =
    new BooleanProperty(bean = this, name = "isDone", initialValue = isDone_)
  val text =
    new StringProperty(bean = this, name = "text", initialValue = text_)
  isDone.onChange((source, oldValue, newValue) =>
    println(s"$source $oldValue $newValue"))
  text.onChange((source, oldValue, newValue) =>
    println(s"$source $oldValue $newValue"))
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
  val data = ObservableBuffer[Note](new Note(UUID.randomUUID(), false, "hoi"),
                                    new Note(UUID.randomUUID(), true, "doei"))

  val createButton = new Button("+")
  val saveButton = new Button("Save")
  val loadButton = new Button("Load")
  val deleteMenuItem = new MenuItem("delete")
  val tableContextMenu: ContextMenu = new ContextMenu(deleteMenuItem)

  val table = new TableView[Note](data) {
    columns ++= List(
      new TableColumn[Note, java.lang.Boolean] {
        text = "Done"
        cellValueFactory = _.value.isDone
          .asInstanceOf[ObservableValue[java.lang.Boolean, java.lang.Boolean]]
        cellFactory = CheckBoxTableCell.forTableColumn(this)
        editable = true

      },
      new TableColumn[Note, String] {
        text = "Text"
        cellFactory = TextFieldTableCell.forTableColumn[Note]()
        cellValueFactory = { _.value.text }
        contextMenu = tableContextMenu
        editable = true
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
    val note = new Note(UUID.randomUUID(), isDone_ = false, text_ = "")
    view.data.+=(note)
  }

  val deleteHandler: EventHandler[ActionEvent] = (_: ActionEvent) => {
    println("Delete")
    val row = view.table.getFocusModel.getFocusedCell.getRow
    println(row)
    view.data.remove(row)
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
