import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.flywaydb.core.Flyway

object Main extends App with Routes {

  val flyway = new Flyway()
  flyway.setDataSource("jdbc:postgresql://localhost:5432/postgres",
                       "postgres",
                       "password")
  flyway.migrate()

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val repositories = new Db
  val taskService = new TaskService(repositories)
  Http().bindAndHandle(route, "localhost", 8080)
}
