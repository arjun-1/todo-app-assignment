import java.util.UUID

case class Task(id: Option[UUID], isDone: Boolean, text: String)
