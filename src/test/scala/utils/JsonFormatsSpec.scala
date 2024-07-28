package utils

import api.UserRegistry.ActionPerformed
import model._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.{JsonParser, _}

class JsonFormatsSpec extends AnyWordSpec with Matchers {

  import JsonFormats._

  // Define sample data
  val user = User(1, "John Doe")
  val actionPerformed = ActionPerformed("User 1 created.")

  "JsonFormats" should {

    "correctly serialize and deserialize User objects" in {
      // Serialize User to JSON
      val userJson = user.toJson
      val expectedUserJson = """{"id":1,"name":"John Doe"}"""

      println("expectedUserJson" + expectedUserJson)
      // Convert userJson to prettyPrint and compare
      userJson.toString shouldEqual expectedUserJson

      // Deserialize JSON to User
      val deserializedUser = JsonParser(expectedUserJson).convertTo[User]
      deserializedUser shouldEqual user
    }

    "correctly serialize and deserialize ActionPerformed objects" in {
      // Serialize ActionPerformed to JSON
      val actionJson = actionPerformed.toJson
      val expectedActionJson = """{"description":"User 1 created."}"""

      // Convert actionJson to prettyPrint and compare
      actionJson.toString shouldEqual expectedActionJson

      // Deserialize JSON to ActionPerformed
      val deserializedAction = JsonParser(expectedActionJson).convertTo[ActionPerformed]
      deserializedAction shouldEqual actionPerformed
    }
  }
}
