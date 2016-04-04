package crylio.data.messages

import crylio.common.domain.Session

object SessionRepositoryMessages {

  case class AddSession(session: Session)

  case class SessionAdded(token: String)

}
