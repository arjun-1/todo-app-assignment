package com.arjun.view

import java.lang

import com.arjun.model.TaskFX
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.TableColumn._
import scalafx.scene.control._
import scalafx.scene.control.cell.{CheckBoxTableCell, TextFieldTableCell}
import scalafx.scene.layout.{HBox, VBox}

class View(model: ObservableBuffer[TaskFX]) {

  val createButton = new Button("+")
  val deleteMenuItem = new MenuItem("Delete")
  val tableContextMenu: ContextMenu = new ContextMenu(deleteMenuItem)

  val doneColumn: TableColumn[TaskFX, lang.Boolean] =
    new TableColumn[TaskFX, java.lang.Boolean] {
      text = "Done"
      cellValueFactory = _.value.isDoneProperty
        .asInstanceOf[ObservableValue[java.lang.Boolean, java.lang.Boolean]]
      cellFactory = CheckBoxTableCell.forTableColumn(this)
    }
  val textColumn = new TableColumn[TaskFX, String] {
    text = "Text"
    cellFactory = TextFieldTableCell.forTableColumn[TaskFX]()
    cellValueFactory = { _.value.textProperty }
  }

  val table = new TableView[TaskFX](model) {
    columns ++= List(
      doneColumn,
      textColumn
    )
    contextMenu = tableContextMenu
    editable = true
    columnResizePolicy = TableView.ConstrainedResizePolicy
  }

  // For unit testing
  def getFocusedTableRow() = table.getFocusModel.getFocusedCell.getRow

  val hBox = new HBox(createButton)
  val vbox = new VBox(table, hBox)
}
