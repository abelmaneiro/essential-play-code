package controllers

import play.api.mvc._

object AuthController extends Controller with ControllerHelpers {
  import services.AuthService
  import services.AuthServiceMessages._

  // TODO: Complete:
  //  - Call AuthService.login
  //     - If it's LoginSuccess, return an Ok response that sets a cookie
  //     - If it's UserNotFound or PasswordIncorrect, return a BadRequest response
  //
  // NOTE: We don't know how to create HTML yet,
  // so populate each response with a plain text message.
  def login(username: Username, password: Password): Action[AnyContent] = Action {
    AuthService.login(LoginRequest(username, password)) match {
      //case LoginSuccess(sessionId) => Ok("Logged in").withCookies(Cookie("ChatAuth", sessionId))
      case LoginSuccess(sessionId) => Ok("Logged in").withSessionCookie(sessionId )
      case _: PasswordIncorrect    => BadRequest("User not found or password incorrect")
      case _: UserNotFound         => BadRequest("User not found or password incorrect")
    }
  }
}
