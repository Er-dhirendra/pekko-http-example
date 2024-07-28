package api

import api.UserRegistry.ActionPerformed
import model.User
import org.apache.pekko.actor.testkit.typed.scaladsl.{ActorTestKit, ScalaTestWithActorTestKit}
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class UserRegistrySpec extends ScalaTestWithActorTestKit with AnyWordSpecLike with Matchers {

  "A UserRegistry actor" should {

    "create a new user and respond with ActionPerformed" in {
      val userRegistry = testKit.spawn(UserRegistry())
      val probe = testKit.createTestProbe[UserRegistry.ActionPerformed]()

      userRegistry.ref ! UserRegistry.CreateUser(User(1, "John Doe"), probe.ref)
      probe.expectMessage(UserRegistry.ActionPerformed("User 1 created."))
    }

    "return the list of users on GetUsers" in {
      val userRegistry = testKit.spawn(UserRegistry())
      val userCreatedProbe = testKit.createTestProbe[UserRegistry.ActionPerformed]()
      val usersProbe = testKit.createTestProbe[Set[User]]()

      userRegistry.ref ! UserRegistry.CreateUser(User(1, "John Doe"), userCreatedProbe.ref)
      userCreatedProbe.expectMessage(UserRegistry.ActionPerformed("User 1 created."))

      userRegistry.ref ! UserRegistry.GetUsers(usersProbe.ref)
      val users = usersProbe.expectMessageType[Set[User]]

      users should contain(User(1, "John Doe"))
    }

    "update an existing user and respond with ActionPerformed" in {
      val userRegistry = testKit.spawn(UserRegistry())
      val probe = testKit.createTestProbe[UserRegistry.ActionPerformed]()

      userRegistry.ref ! UserRegistry.CreateUser(User(1, "John Doe"), probe.ref)
      probe.expectMessage(UserRegistry.ActionPerformed("User 1 created."))

      userRegistry.ref ! UserRegistry.UpdateUser(User(1, "Jane Doe"), probe.ref)
      probe.expectMessage(UserRegistry.ActionPerformed("User 1 updated."))
    }

    "delete an existing user and respond with ActionPerformed" in {
      val userRegistry = testKit.spawn(UserRegistry())
      val probe = testKit.createTestProbe[UserRegistry.ActionPerformed]()

      userRegistry.ref ! UserRegistry.CreateUser(User(1, "John Doe"), probe.ref)
      probe.expectMessage(UserRegistry.ActionPerformed("User 1 created."))

      userRegistry.ref ! UserRegistry.DeleteUser(1, probe.ref)
      probe.expectMessage(UserRegistry.ActionPerformed("User 1 deleted."))
    }

    "respond with ActionPerformed when attempting to delete a non-existing user" in {
      val userRegistry = testKit.spawn(UserRegistry())
      val probe = testKit.createTestProbe[UserRegistry.ActionPerformed]()

      userRegistry.ref ! UserRegistry.DeleteUser(99, probe.ref)
      probe.expectMessage(UserRegistry.ActionPerformed("User 99 not found."))
    }
  }
}
