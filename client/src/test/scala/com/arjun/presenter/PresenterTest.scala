package com.arjun.presenter

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.data.EitherT
import com.arjun.{HttpClientTrait, Task}
import com.arjun.model.TaskFX
import com.arjun.view.View
import javafx.embed.swing.JFXPanel
import javafx.event.{Event, EventType}
import javafx.scene.control.TableColumn.CellEditEvent
import org.scalamock.scalatest.MockFactory
import scalafx.collections.ObservableBuffer
import org.scalatest._
import scalafx.application.JFXApp
import scalafx.event.ActionEvent
import javafx.scene.control.{TableColumn, TablePosition, TableView}
import org.scalatest.mockito.MockitoSugar
//import org.testfx.framework.junit.ApplicationTest
import cats.instances.future._
import org.scalatest.concurrent.Eventually

import scala.concurrent.Future

class PresenterTest
    extends WordSpec
    with Matchers
    with MockFactory
    with Eventually {

  import scala.concurrent.duration._
  override implicit val patienceConfig = PatienceConfig(
    timeout = scaled(10 seconds),
    interval = scaled(100 millis)
  )

  val uuid1 = UUID.fromString("899876ee-db27-41fc-bfee-3d75715fbab0")
  val uuid2 = UUID.fromString("4206ed51-fc07-4ef3-8521-c7939b327cd0")
  val uuid3 = UUID.fromString("646fdb0d-8c99-4f0f-8b64-524c69660e22")

  val uuidNew = UUID.fromString("9932d41d-ad09-4a79-b1a9-de28b941a7b9")
  val tasks = List(
    Task(id = Some(uuid1), None, true, "hoi"),
    Task(Some(uuid2), None, false, "doei"),
    Task(Some(uuid2), None, false, "doei2")
  )
  trait ModelFixture {
    val tasksFX = ObservableBuffer[TaskFX](
      tasks.map(_.toTaskFX)
    )
  }

  val _ = new JFXPanel
  val viewMock = mock[View]


  class SimpleHttpClient extends HttpClientTrait {
    import scala.concurrent.ExecutionContext.Implicits.global

    override def getTaskByTaskId(taskId: UUID): EitherT[Future, String, Task] =
      EitherT.rightT[Future, String](
        Task(id = Some(taskId), userId = None, isDone = true, text = ""))

    override def addTask(task: Task): EitherT[Future, String, Task] =
      EitherT.rightT[Future, String](task.copy(id = Some(uuidNew)))

    override def listTasksByUserId(
        userId: UUID): EitherT[Future, String, List[Task]] =
      EitherT.rightT[Future, String](tasks)

    override def updateTask(taskId: UUID,
                            task: Task): EitherT[Future, String, Task] =
      EitherT.rightT[Future, String](task.copy(id = Some(taskId)))

    override def listTasks: EitherT[Future, String, List[Task]] =
      EitherT.rightT[Future, String](tasks)

    override def deleteTask(taskId: UUID): EitherT[Future, String, Unit] =
      EitherT.rightT[Future, String]()
  }

  "createHandler" should {
    "create a new task" in new ModelFixture {
      val presenter = new Presenter(viewMock, tasksFX, new SimpleHttpClient)
      val event = new ActionEvent()
      presenter.createHandler.handle(event)
      eventually {
        tasksFX.toList should be(
          tasks.map(_.toTaskFX) :+ TaskFX(id = uuidNew,
                                          isDone = false,
                                          text = "")
        )
      }

    }
  }

  "deleteHandler" should {
    "delete a task" in new ModelFixture {
      (viewMock.getFocusedTableRow: () => Int).expects().returns(0)

      val presenter = new Presenter(viewMock, tasksFX, new SimpleHttpClient)
      val event = new ActionEvent()
      presenter.deleteHandler.handle(event)
      eventually {
        tasksFX.toList should be(
          tasks.map(_.toTaskFX).drop(1)
        )
      }
    }
  }

  "editHandler" should {
    "edit a task" in new ModelFixture {

      val newText = "my new value"
      val view = new View(tasksFX)
      val eventType = new EventType[TableColumn.CellEditEvent[TaskFX, String]](Event.ANY, "TABLE_COLUMN_EDIT1")
      val pos = new TablePosition[TaskFX, String](view.table, 0, view.textColumn)
      val cellEditEvent = new CellEditEvent[TaskFX, String](viewMock.table, pos, eventType, newText)

      val presenter = new Presenter(viewMock, tasksFX, new SimpleHttpClient)
      val event = new ActionEvent()
      presenter.editHandler.handle(cellEditEvent)
      eventually {
        tasksFX.toList should be(
          tasks.map(_.toTaskFX).updated(0, tasks.head.copy(text = newText).toTaskFX)
        )
      }

    }
  }

}
