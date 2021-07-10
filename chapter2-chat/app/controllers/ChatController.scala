package controllers

import play.api.mvc._

object ChatController extends Controller with ControllerHelpers {
  import services.AuthServiceMessages._
  import services.{AuthService, ChatService}

  // TODO: Complete:
  //  - Check if the user is logged in
  //     - If they are, return an Ok response containing a list of messages
  //     - If they aren't, redirect to the login page
  //
  // NOTE: We don't know how to create HTML yet,
  // so populate each response with a plain text message.
  def index1: Action[AnyContent] = Action { request =>
    request.cookies.get("ChatAuth").map { sessionIDCookie =>
      AuthService.whoami(sessionIDCookie.value) match {
        case Credentials(_, _) => Ok(ChatService.messages.mkString("/n"))
        case _ => BadRequest("Not logged in")
      }
    }.getOrElse(BadRequest("Not logged in"))
  }

  def index: Action[AnyContent] = Action { implicit request =>
    withAuthenticatedUser { _ =>
      Ok(ChatService.messages.mkString("/n"))
    }
  }

  // TODO: Complete:
  //  - Check if the user is logged in
  //     - If they are, create a message from the relevant author
  //     - If they aren't, redirect to the login page
  //
  // NOTE: We don't know how to create HTML yet,
  // so populate each response with a plain text message.
  def submitMessage1(text: String): Action[AnyContent] = Action { request =>
    request.cookies.get("ChatAuth") match {
      case Some(sessionIDCookie) => AuthService.whoami(sessionIDCookie.value) match {
        case Credentials(_, username) =>
          ChatService.chat(username, text)
          Ok(ChatService.messages.mkString("/n"))
        case _ => BadRequest("Not logged in")
      }
      case _ => BadRequest("Not logged in")
    }
  }

  def submitMessage(text: String): Action[AnyContent] = Action { implicit request =>
    withAuthenticatedUser { cred =>
      ChatService.chat(cred.username, text)
      Redirect(routes.ChatController.index())
    }
  }

  // If user logged in do the following else bad request
  private def withAuthenticatedUser1 (func: Credentials => Result)(implicit request: Request[AnyContent]): Result = {
    request.cookies.get("ChatAuth").map(_.value) match {
      case Some(sessionId) => AuthService.whoami(sessionId) match {
        case cred: Credentials => func(cred)
        case _ => BadRequest("Not logged in")
      }
      case _ => BadRequest("Not logged in")
    }
  }
  private def withAuthenticatedUser(func: Credentials => Result)(implicit request: Request[AnyContent]) : Result = {
    request.sessionCookieId match {
      case Some(sessionId) => AuthService.whoami(sessionId) match {
        case cred: Credentials => func(cred)
        case _ => BadRequest("Not logged in")
      }
      case _ => BadRequest("Not logged in")
    }
  }


  }
