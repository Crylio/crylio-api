package crylio.common.domain

import crylio.common._
import crylio.common.Identifiers.UserId

case class User(id: UserId,
                email: String,
                displayName: String,
                salt: String,
                hashedPassword: String,
                isBlocked: Boolean = false) {
  def checkPassword(password: String) =  hashedPassword == User.hashPassword(salt, password)
}

object User {
  def hashPassword(salt: String, password: String) = sha256(salt + password)

}
