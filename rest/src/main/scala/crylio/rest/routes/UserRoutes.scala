package crylio.rest.routes

import crylio.common.akka.Implicits.RichProps
import crylio.common.dto.user.{SignInResultDTO, SignInDTO}
import crylio.rest.common.BaseRoutes
import crylio.rest.controllers.user.SignInController
import crylio.rest.serializers.UserRoutesJson
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller

trait UserRoutes extends BaseRoutes with UserRoutesJson {
  import context.dispatcher

  def user01routes = signInRoute

  def signInRoute = path("users" / "signin") {
    post {
      entity(as[SignInDTO]) { entity =>
        complete {
          SignInController.props(entity).execute[SignInResultDTO]
        }
      }
    }
  }
}
