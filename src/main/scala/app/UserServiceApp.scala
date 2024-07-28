package app

import api.{UserRoutes, UserRegistry}
import org.apache.pekko
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route

import scala.concurrent.Future
import scala.util.{Failure, Success}

object UserServiceApp extends App {

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {

    import system.executionContext

    val futureBinding: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }


  private val rootBehavior = Behaviors.setup[Nothing] { context =>

    val userRegistryActor = context.spawn(UserRegistry(), "UserRegistry")
    context.watch(userRegistryActor)

    val routes = new UserRoutes(userRegistryActor)(context.system)
    startHttpServer(routes.route)(context.system)

    Behaviors.empty
  }
  val system = ActorSystem[Nothing](rootBehavior, "PekkoHttpServer")
}
