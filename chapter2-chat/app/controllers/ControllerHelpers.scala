package controllers

import play.api.mvc.{AnyContent, Cookie, Request, Result, Results}
import services._

// Example of an implicit class
trait ControllerHelpers extends Results {  // this can be ant class, trait or object
  implicit class RequestCookieOps(request: Request[AnyContent]) {  // must have only one constructor parameter
    def sessionCookieId: Option[String] = request.cookies.get("ChatAuth").map(_.value)
  }
  implicit class ResultCookieOps(result: Result) {
    def withSessionCookie(sessionId: String) = result.withCookies(Cookie("ChatAuth", sessionId))
  }
}



//package controllers
//
//import play.api.mvc._
//
//import services._
//
//trait ControllerHelpers extends Results {
//  implicit class RequestCookieOps(request: Request[AnyContent]) {
//    def sessionCookieId: Option[String] =
//      request.cookies.get("ChatAuth").map(_.value)
//  }
//
//  implicit class ResultCookieOps(result: Result) {
//    def withSessionCookie(sessionId: String) =
//      result.withCookies(Cookie("ChatAuth", sessionId))
//  }
//}
