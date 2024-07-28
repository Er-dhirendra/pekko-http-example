package utils

import api.UserRegistry.ActionPerformed
import model._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonFormats {

  import DefaultJsonProtocol._

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat2(User.apply)
  implicit val actionPerformedFormat: RootJsonFormat[ActionPerformed] = jsonFormat1(ActionPerformed.apply)

}
