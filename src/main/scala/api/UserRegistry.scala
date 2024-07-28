package api

import model.User
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}

object UserRegistry {
  sealed trait Command
  final case class GetUsers(replyTo: ActorRef[Set[User]]) extends Command
  final case class CreateUser(user: User, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class UpdateUser(user: User, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class DeleteUser(id: Int, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(users: Set[User]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetUsers(replyTo) =>
        replyTo ! users
        Behaviors.same

      case CreateUser(user, replyTo) =>
        val updatedUsers = users + user
        replyTo ! ActionPerformed(s"User ${user.id} created.")
        registry(updatedUsers)

      case UpdateUser(user, replyTo) =>
        if (users.exists(_.id == user.id)) {
          val updatedUsers = users.filter(_.id != user.id) + user
          replyTo ! ActionPerformed(s"User ${user.id} updated.")
          registry(updatedUsers)
        } else {
          replyTo ! ActionPerformed(s"User ${user.id} not found.")
          Behaviors.same
        }

      case DeleteUser(id, replyTo) =>
        users.find(_.id == id) match {
          case Some(user) =>
            val updatedUsers = users - user
            replyTo ! ActionPerformed(s"User $id deleted.")
            registry(updatedUsers)
          case None =>
            replyTo ! ActionPerformed(s"User $id not found.")
            Behaviors.same
        }
    }
}
