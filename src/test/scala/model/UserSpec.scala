package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.*

class UserSpec extends AnyWordSpec with Matchers {

  import utils.JsonFormats.*

  "User" should {

    "correctly serialize to JSON" in {
      val user = User(1, "John Doe")
      val userJson = user.toJson
      val expectedJson = """{"id":1,"name":"John Doe"}"""

      userJson.toString shouldEqual expectedJson
    }

    "correctly deserialize from JSON" in {
      val jsonString = """{"id":1,"name":"John Doe"}"""
      val user = JsonParser(jsonString).convertTo[User]

      user shouldEqual User(1, "John Doe")
    }

    "correctly handle JSON serialization and deserialization" in {
      val user = User(1, "John Doe")
      val jsonString = user.toJson.toString
      val deserializedUser = JsonParser(jsonString).convertTo[User]

      deserializedUser shouldEqual user
    }

    "have the correct default values when created" in {
      val user = User(0, "")

      user.id shouldEqual 0
      user.name shouldEqual ""
    }

    "handle different user values correctly" in {
      val user1 = User(1, "Alice")
      val user2 = User(2, "Bob")

      user1.id shouldEqual 1
      user1.name shouldEqual "Alice"

      user2.id shouldEqual 2
      user2.name shouldEqual "Bob"
    }
  }
}

