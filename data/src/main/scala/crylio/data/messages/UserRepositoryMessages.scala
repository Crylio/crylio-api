package crylio.data.messages

import crylio.common.domain.User

object UserRepositoryMessages {

  case class FindUserByEmail(email: String)

  case class UserByEmailNotFound(email: String)

  case class UserByEmail(user: User)

}
