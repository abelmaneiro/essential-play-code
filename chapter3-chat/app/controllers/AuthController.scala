package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._

object AuthController extends Controller with ControllerHelpers with I18nSupport {
  val messagesApi: MessagesApi = Messages.Implicits.applicationMessagesApi(Play.current)  // need for internal support

  import services.AuthService
  import services.AuthServiceMessages._

  // TODO: Complete:
  //  - Create a form for a LoginRequest
  val loginForm: Form[LoginRequest] = Form(mapping(  // keys must much below
    "username" -> nonEmptyText,                 // final case class LoginRequest(username: Username, password: Password)
    "password" -> nonEmptyText
  )(LoginRequest.apply)(LoginRequest.unapply))

  // TODO: Complete:
  //  - Create a login page template:
  //     - Accepts a login form as a parameter
  //     - Displays the form and a submit button
  def login: Action[AnyContent] = Action.apply { implicit request =>
    Ok.apply(views.html.login.apply(loginForm))
  }

  // TODO: Complete:
  //  - Process a submitted login form:
  //     - If it is valid, call AuthService.login:
  //        - If the login was successful, set a cookie and redirect to the chat page
  //        - If the user was not found, return a login form with an appropriate error message     [*]
  //        - If the password was incorrect, return a login form with an appropriate error message [*]
  //     - If it is invalid, a login form with an appropriate error message
  //
  // NOTE: You will have to specify the error messages marked with a [*] manually.
  // You can do this with the following code:
  //
  //     loginForm.withError("username", "User not found") // returns a new login form
  def submitLogin: Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest().fold(
      hasErrors = { errorLoginForm:  Form[LoginRequest] => BadRequest(views.html.login(errorLoginForm))
      },
      success = { successLogin: LoginRequest =>
        AuthService.login(successLogin) match {
          case _: UserNotFound =>
            BadRequest(views.html.login(loginForm.withError("username", "Username not found")))
          case PasswordIncorrect(_) =>
            BadRequest(views.html.login(loginForm.withError("password","Password incorrect")))
          case LoginSuccess(sessionId) =>
            Redirect(routes.ChatController.index()).withCookies(Cookie("ChatAuth", sessionId))
        }
      }
    )
  }

//  def loginRedirect(res: LoginSuccess): Result =
//    Redirect(routes.ChatController.index()).withSessionCookie(res.sessionId)
}
