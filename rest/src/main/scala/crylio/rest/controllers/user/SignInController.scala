package crylio.rest.controllers.user

import akka.actor.Props
import crylio.common.domain.Session
import crylio.common.dto.user.{SignInResultDTO, SignInDTO}
import crylio.data.messages.SessionRepositoryMessages.{SessionAdded, AddSession}
import crylio.data.messages.UserRepositoryMessages.{FindUserByEmail, UserByEmail, UserByEmailNotFound}
import crylio.data.repositories.{SessionRepository, UserRepository}
import crylio.rest.Errors.Unauthorized
import crylio.rest.controllers.Controller

object SignInController {
  def props(data: SignInDTO) = Props(classOf[SignInController], data.email, data.password)
}

class SignInController(email: String, password: String) extends Controller {
  override def start(): Unit = {
    UserRepository.endpoint ! FindUserByEmail(email)
  }

  override def receive: Receive = {
    case UserByEmailNotFound(`email`) => failure(Unauthorized.credentialsRejected)
    case UserByEmail(user) if user.isBlocked => failure(Unauthorized.credentialsRejected)
    case UserByEmail(user) if !user.checkPassword(password) => failure(Unauthorized.credentialsRejected)
    case UserByEmail(user) => SessionRepository.endpoint ! AddSession(Session.create(user.id))
    case SessionAdded(token) => complete(SignInResultDTO(token))
  }
}
