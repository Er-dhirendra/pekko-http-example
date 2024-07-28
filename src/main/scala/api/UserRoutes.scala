package api


import api.UserRegistry.{ActionPerformed, GetUsers}
import model.User
import org.apache.pekko
import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem}
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.util.Timeout
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.util.{Failure, Success}

class UserRoutes(userRegistry: ActorRef[UserRegistry.Command])(implicit val system: ActorSystem[_]) {

  import pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import utils.JsonFormats._

  private implicit val timeout: Timeout = Timeout(system.settings.config.getDuration("my-app.routes.ask-timeout").toMillis.millis)

  val route: Route =
    concat(
      // GET /users
      path("users") {
        get {
          val allUsersFuture: Future[Set[User]] = userRegistry ? (ref => UserRegistry.GetUsers(ref))
          onComplete(allUsersFuture) {
            case Success(users) => complete(HttpEntity(ContentTypes.`application/json`, users.toJson.prettyPrint))
            case Failure(exception) => complete(StatusCodes.InternalServerError, ActionPerformed(s"Failed to fetch users: ${exception.getMessage}").toJson.prettyPrint)
          }
        } ~
          post {
            entity(as[User]) { user =>
              val creationResponseFuture: Future[UserRegistry.ActionPerformed] = userRegistry ? (ref => UserRegistry.CreateUser(user, ref))
              onComplete(creationResponseFuture) {
                case Success(response) => complete(StatusCodes.Created, HttpEntity(ContentTypes.`application/json`, response.toJson.prettyPrint))
                case Failure(exception) => complete(StatusCodes.InternalServerError, ActionPerformed(s"Failed to create user: ${exception.getMessage}").toJson.prettyPrint)
              }
            }
          }
      },

      // GET /users/{id}
      path("users" / IntNumber) { id =>
        get {
          val userFuture: Future[Set[User]] = userRegistry ? (ref => UserRegistry.GetUsers(ref))
          onComplete(userFuture) {
            case Success(users) =>
              users.find(_.id == id) match {
                case Some(user) => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, user.toJson.prettyPrint))
                case None => complete(StatusCodes.NotFound, HttpEntity(ContentTypes.`application/json`, ActionPerformed("User not found").toJson.prettyPrint))
              }
            case Failure(exception) => complete(StatusCodes.InternalServerError, HttpEntity(ContentTypes.`application/json`, ActionPerformed(s"Failed to fetch user: ${exception.getMessage}").toJson.prettyPrint))
          }
        }
      },

      // PUT /users/{id}
      path("users" / IntNumber) { id =>
        put {
          entity(as[User]) { updatedUser =>
            val updateResponseFuture: Future[UserRegistry.ActionPerformed] = userRegistry ? (ref => UserRegistry.UpdateUser(updatedUser.copy(id = id), ref))
            onComplete(updateResponseFuture) {
              case Success(response) => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, response.toJson.prettyPrint))
              case Failure(exception) => complete(StatusCodes.InternalServerError, ActionPerformed(s"Failed to update user: ${exception.getMessage}").toJson.prettyPrint)
            }
          }
        }
      },

      // PATCH /users/{id}
      path("users" / IntNumber) { id =>
        patch {
          entity(as[User]) { partialUser =>
            val updateResponseFuture: Future[UserRegistry.ActionPerformed] = userRegistry ? (ref => UserRegistry.UpdateUser(partialUser.copy(id = id), ref))
            onComplete(updateResponseFuture) {
              case Success(response) => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, response.toJson.prettyPrint))
              case Failure(exception) => complete(StatusCodes.InternalServerError, ActionPerformed(s"Failed to update user: ${exception.getMessage}").toJson.prettyPrint)
            }
          }
        }
      },

      // DELETE /users/{id}
      path("users" / IntNumber) { id =>
        delete {
          val deleteResponseFuture: Future[UserRegistry.ActionPerformed] = userRegistry ? (ref => UserRegistry.DeleteUser(id, ref))
          onComplete(deleteResponseFuture) {
            case Success(response) => complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, response.toJson.prettyPrint))
            case Failure(exception) => complete(StatusCodes.InternalServerError, HttpEntity(ContentTypes.`application/json`, ActionPerformed(s"Failed to delete user: ${exception.getMessage}").toJson.prettyPrint))
          }
        }
      },

      // HEAD /users
      path("users") {
        head {
          complete(StatusCodes.OK)
        }
      },

      // OPTIONS /users
      path("users") {
        options {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "GET, POST, PUT, PATCH, DELETE, OPTIONS"))
        }
      }
    )
}