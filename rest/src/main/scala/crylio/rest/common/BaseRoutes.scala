package crylio.rest.common

import akka.actor.Actor
import akka.util.Timeout
import spray.routing.HttpService

trait BaseRoutes extends HttpService with Actor {
  implicit val timeout: Timeout
}
