import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {

  val repositories = new Db
  val service = new TaskService(repositories)
  val foo = Await.result(service.get().value, 10 seconds)
  println(foo)

}
