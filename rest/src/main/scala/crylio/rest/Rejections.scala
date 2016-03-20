package crylio.rest

import spray.routing.Rejection

import scala.concurrent.duration.FiniteDuration

object Rejections {
  case class TooManyRequestsRejection(retryAfter: FiniteDuration) extends Rejection
}

