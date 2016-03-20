package crylio.rest

import spray.http.{Rendering, HttpHeader}

import scala.concurrent.duration.FiniteDuration

object `Retry-After` {
  val name = "Retry-After"
}

case class `Retry-After`(retryAfter: FiniteDuration) extends HttpHeader {
  val name = `Retry-After`.name
  val value = retryAfter.toSeconds.toString
  val lowercaseName = name.toLowerCase
  def render[R <: Rendering](r: R): r.type = r ~~ name ~~ ':' ~~ ' ' ~~ value
}