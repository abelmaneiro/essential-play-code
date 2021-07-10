package controllers

import models._
import play.api.mvc._
import play.twirl.api.Html

object TodoController extends Controller with TodoDataHelpers {
  def index: Action[AnyContent] = Action {
    Ok(renderTodoList(todoList))
  }

  def renderTodoList(todoList: TodoList): Html =
    // TODO: Call your template to render the todo list.
    views.html.todo(todoList)
}

trait TodoDataHelpers {
  var todoList: TodoList = TodoList(Seq(
    Todo("Dishes", complete = true),
    Todo("Laundry"),
    Todo("World Domination")
  ))
}