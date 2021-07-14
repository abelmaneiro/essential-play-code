package controllers

import play.api.mvc._

object LandingController extends Controller with ControllerHelpers {
  def index: Action[AnyContent] = Action { request =>
    request.sessionCookieId match {
      case Some(_)  => Redirect(routes.ChatController.index())
      case None     => Redirect(routes.AuthController.login())
    }
  }
}
