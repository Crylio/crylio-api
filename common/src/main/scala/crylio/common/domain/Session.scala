package crylio.common.domain

import crylio.common._
import crylio.common.Identifiers.UserId
import org.joda.time.DateTime

case class Session(token: String, userId: UserId, createdAt: DateTime)

object Session {
  def create(userId: UserId) = Session(newUUID, userId, utcNow)
}
