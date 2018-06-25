package com.arjun.presenter

import com.arjun.HttpClient
import com.arjun.Main.{stage, tasks, view}
import com.arjun.model.TaskFX
import com.arjun.view.View
import javafx.stage.Stage
import org.testfx.framework.junit.ApplicationTest
import scalafx.application.JFXApp.PrimaryStage
import org.testfx.assertions.api.Assertions.assertThat


import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene

class PresenterIntegrationTest extends ApplicationTest {


//  stage = new PrimaryStage {
//    title = "My application"
//    scene = new Scene {
//      root = view.vbox
//    }
//  }

  override def start(stage: Stage): Unit = {

    val tasks: ObservableBuffer[TaskFX] = ObservableBuffer[TaskFX]()
    val view = new View(tasks)
    val presenter = new Presenter(view, tasks, new HttpClient)

    val scene = new Scene {
      root = view.vbox
    }
    stage.setScene(scene)
    stage.show()
  }


}
