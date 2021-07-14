package services

object ChatService {
  import ChatServiceMessages._

  private var postedMessages = Vector[Message](Message("Chat User1", "Chat message1"), Message("Chat User2", "Chat message2"))

  def clear(): Unit =
    postedMessages = Vector[Message]()

  def messages: Seq[Message] =
    postedMessages

  def chat(author: String, text: String): Message = {
    val message = Message(author, text)
    postedMessages = postedMessages :+ message
    message
  }
}
