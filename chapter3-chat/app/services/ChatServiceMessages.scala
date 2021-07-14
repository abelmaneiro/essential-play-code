package services

object ChatServiceMessages {
  case class Message(author: String, text: String)
  case class ChatText(text: String)
}
