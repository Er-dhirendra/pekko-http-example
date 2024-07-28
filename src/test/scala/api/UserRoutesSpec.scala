package api

import api.UserRegistry.*
import model.User
import org.apache.pekko.actor
import org.apache.pekko.actor.testkit.typed.scaladsl.ActorTestKit
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import spray.json.*

class UserRoutesSpec extends AnyWordSpecLike with Matchers with ScalatestRouteTest {

  import utils.JsonFormats._

  lazy val testKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[_] = testKit.system
  override def createActorSystem(): actor.ActorSystem =
    testKit.system.classicSystem

  // Here we need to implement all the abstract members of UserRoutes.
  // We use the real UserRegistryActor to test it while we hit the Routes,
  // but we could "mock" it by implementing it in-place or by using a TestProbe
  // created with testKit.createTestProbe()
  val userRegistry = testKit.spawn(UserRegistry())
  lazy val routes = new UserRoutes(userRegistry).route

  "UserRoutes" should {

    "return an empty list of users on GET /users" in {
      Get("/users") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        responseAs[String] shouldEqual "[]"
      }
    }

    "create a new user and return ActionPerformed on POST /users" in {
      val newUser = User(1, "John Doe")
      val userEntity = HttpEntity(ContentTypes.`application/json`, newUser.toJson.prettyPrint)
      Post("/users", userEntity) ~> routes ~> check {
        status shouldEqual StatusCodes.Created
        contentType shouldEqual ContentTypes.`application/json`
        val responseJson = responseAs[String].parseJson
        val expectedJson = ActionPerformed("User 1 created.").toJson
        responseJson shouldEqual expectedJson
      }
    }

    "return a user on GET /users/{id}" in {
      val user = User(1, "John Doe")
      Get("/users/1") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        responseAs[String] shouldEqual user.toJson.prettyPrint
      }
    }

    "return NotFound for a non-existing user on GET /users/{id}" in {
      Get("/users/99") ~> routes ~> check {
        status shouldEqual StatusCodes.NotFound
        contentType shouldEqual ContentTypes.`application/json`
        val responseJson = responseAs[String].parseJson
        val expectedJson = ActionPerformed("User not found").toJson
        responseJson shouldEqual expectedJson
      }
    }

    "update an existing user and return ActionPerformed on PUT /users/{id}" in {
      val updatedUser = User(1, "Jane Doe")
      val userEntity = HttpEntity(ContentTypes.`application/json`, updatedUser.toJson.prettyPrint)
      Put("/users/1", userEntity) ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        val responseJson = responseAs[String].parseJson
        val expectedJson = ActionPerformed("User 1 updated.").toJson
        responseJson shouldEqual expectedJson
      }
    }

    "update an existing user with PATCH /users/{id}" in {
      val partialUser = User(1, "Jane Doe")
      val userEntity = HttpEntity(ContentTypes.`application/json`, partialUser.toJson.prettyPrint)
      Patch("/users/1", userEntity) ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        val responseJson = responseAs[String].parseJson
        val expectedJson = ActionPerformed("User 1 updated.").toJson
        responseJson shouldEqual expectedJson
      }
    }

    "delete an existing user and return NoContent on DELETE /users/{id}" in {
      Delete("/users/1") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        val responseJson = responseAs[String].parseJson
        val expectedJson = ActionPerformed("User 1 deleted.").toJson
        responseJson shouldEqual expectedJson
      }
    }

    "return OK on HEAD /users" in {
      Head("/users") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "return allowed methods on OPTIONS /users" in {
      Options("/users") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`text/plain(UTF-8)`
        responseAs[String] shouldEqual "GET, POST, PUT, PATCH, DELETE, OPTIONS"
      }
    }
  }
}
