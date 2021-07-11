package controllers

import play.api._
import play.api.i18n._
import play.api.mvc._
import play.api.data.Form
import models._
import play.api.data.Forms.{ boolean, mapping, nonEmptyText, optional, text }
import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.pattern

// TODO: Create a Form[Todo]:
//  - build the basic form mapping;
//  - create constraint to ensure the label is non-empty.
//object TodoController extends Controller
//  with TodoFormHelpers
//  with TodoDataHelpers
//  with I18nSupport {
//  val messagesApi: MessagesApi = Messages.Implicits.applicationMessagesApi(Play.current)
object TodoController extends Controller with I18nSupport {  // I18nSupport needed for internal support
  val messagesApi: MessagesApi = Messages.Implicits.applicationMessagesApi(Play.current)  // need for internal support

  // create dummy TodoList data
  private var todoList: TodoList = TodoList(Seq(Todo("Dishes", complete = true), Todo("Laundry"), Todo("World Domination")))

  // Custom Constraint
  val uuidConstraint: Constraint[String] =
    pattern(
      regex = "(?i:[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})".r,
      name  = "UUID",
      error = "error.uuid"
    )
  // Map model data to Form
  val todoForm: Form[Todo] =
    Form(mapping(  // key must match field name in the case class Todo(id: Option[String], label: String, complete: Boolean)
      "id"        -> optional(text.verifying(uuidConstraint)),
      "label"     -> nonEmptyText,
      "complete"  -> boolean
    )(Todo.apply)(Todo.unapply))

  def index: Action[AnyContent] = Action {

 //   Ok(renderTodoList(editForms(todoList), todoForm))
    Ok(views.html.todoList(editForms(todoList, todoForm), todoForm))
  }

  def submitTodoForm: Action[AnyContent] = Action { implicit request =>
    // TODO: Write code to handle the form submission:
    //  - validate the form;
    //  - if form is valid:
    //     - add todo to todoList;
    //     - redirect to index;
    //  - else:
    //     - display errors.
    todoForm.bindFromRequest().fold(
      hasErrors = { errorForm =>
        errorForm("id").value match {
          case Some(id) if id.nonEmpty =>  // error while updating to one of the todos
            BadRequest(views.html.todoList(editForms(todoList, errorForm), todoForm))
            // BadRequest(renderTodoList(editForms(todoList, errorForm), todoForm))
          case _ =>  // error while creating a new  todo
            BadRequest(views.html.todoList(editForms(todoList, todoForm), errorForm))
          // BadRequest(renderTodoList(editForms(todoList), errorForm))
        }
      },
      success = { todo =>
        todoList = todoList.addOrUpdate(todo)
        Redirect(routes.TodoController.index())
      }
    )
  }

  def editForms(todoList: TodoList, currentForm: Form[Todo]): Seq[Form[Todo]] = {
    currentForm("id").value match {
      case Some(currentId) =>
        todoList.items map {
          case todo if todo hasId currentId =>
            currentForm
          case todo =>
            todoForm fill todo
        }
      case None =>
        todoList.items.map(todoForm.fill)
    }
  }

//  def renderTodoList(editForms: Seq[Form[Todo]], createForm: Form[Todo]): Html =
//    // TODO: Modify template to show form:
//    views.html.todoList(editForms, createForm)

}

//trait TodoFormHelpers {
//  import play.api.data.Forms.{ boolean, mapping, nonEmptyText, optional, text }
//  import play.api.data.validation.Constraint
//  import play.api.data.validation.Constraints.pattern
//
//  // Custom Constraint
//  val uuidConstraint: Constraint[String] =
//    pattern(
//      regex = "(?i:[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})".r,
//      name  = "UUID",
//      error = "error.uuid"
//    )
//
//  // Map model data to Form
//  val todoForm: Form[Todo] =
//    Form(mapping(
//      "id"        -> optional(text.verifying(uuidConstraint)),
//      "label"     -> nonEmptyText,
//      "complete"  -> boolean
//    )(Todo.apply)(Todo.unapply))
//}

//trait TodoDataHelpers {
//  var todoList = TodoList(Seq(
//    Todo("Dishes", true),
//    Todo("Laundry"),
//    Todo("World Domination")
//  ))
//}
