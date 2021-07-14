package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._

object ChatController extends Controller with ControllerHelpers with I18nSupport {
  val messagesApi: MessagesApi = Messages.Implicits.applicationMessagesApi(Play.current)  // need for internal support

  import services.AuthService
  import services.AuthServiceMessages._
  import services.ChatService

  case class ChatRequest(chatText: String)

  // TODO: Complete:
  //  - Create a form for a ChatRequest (defined above)
  val chatForm: Form[ChatRequest] = Form(mapping(
    "chatText" -> nonEmptyText
  )(ChatRequest.apply)(ChatRequest.unapply))

  // TODO: Complete:
  //  - Create a chat room template that accepts the following parameters:
  //     - A list of Messages
  //     - A chat form
  //  - Implement the controller below:
  //     - Check the user is logged in
  //        - If they are, display a web page containing the current messages
  //        - If they aren't, redirect to the login page
  def index: Action[AnyContent] = Action  {request =>
    request.cookies.get("ChatAuth").map { authCookie =>
      AuthService.whoami(authCookie.value) match {
        case Credentials(_, _) => Ok(views.html.chatroom(ChatService.messages, chatForm))
        case _ => Redirect(routes.AuthController.login())
      }
    }.getOrElse(Redirect(routes.AuthController.login()))
  }

  // TODO: Complete:
  //  - Check the user is logged in
  //     - If they are:
  //        - Parse the form data using the login form
  //           - If it's valid, call ChatService.chat and redirect to the chat room page
  //           - If it's invalid, display an appropriate error message
  //     - If they aren't, redirect to the login page
  def submitMessage: Action[AnyContent] = Action { implicit request =>
    request.cookies.get("ChatAuth").map { authCookie =>
      AuthService.whoami(authCookie.value) match {
        case Credentials(_, username) =>
          chatForm.bindFromRequest().fold(
            errorChatForm => BadRequest(views.html.chatroom(ChatService.messages, errorChatForm)),
            chatRequest => {
              ChatService.chat(username, chatRequest.chatText)
              Redirect(routes.ChatController.index())
            }
          )
        case _ => Redirect(routes.AuthController.login())
      }
    }getOrElse Redirect(routes.AuthController.login())
  }

//  private def withAuthenticatedUser(request: Request[AnyContent])(func: Credentials => Result): Result =
//    request.sessionCookieId match {
//      case Some(sessionId) =>
//        AuthService.whoami(sessionId) match {
//          case res: Credentials     => func(res)
//          case res: SessionNotFound => redirectToLogin
//        }
//      case None => redirectToLogin
//    }
//
//  private val redirectToLogin: Result =
//    Redirect(routes.AuthController.login())
}
