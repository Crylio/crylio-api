package crylio.common

import crylio.common.Identifiers._

object Identifiers {

  sealed trait IDENTITY

  sealed trait USER_IDENTITY extends IDENTITY

  type UserId = String @@ USER_IDENTITY

  implicit class TaggedStringOpts(value: String) {
    def as[A <: String @@ _] = value.asInstanceOf[A]
  }

}

object UserId {
  def newId = newUUID.as[UserId]
}