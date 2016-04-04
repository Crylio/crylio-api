package crylio.data.repositories

import crylio.common.akka.Implicits.RichFuture
import crylio.common.akka.NodeSingleton
import crylio.data.messages.UserRepositoryMessages._
import crylio.data.mongo.MongoRepository

object UserRepository extends NodeSingleton[UserRepository]

class UserRepository extends MongoRepository {
  import crylio.data.mongo.collections.UserCollection._

  def checkIndexes() = {
    ensureIndexes
  }

  def receive: Receive = {
    case m @ FindUserByEmail(email) => findUserByEmail(email) map {
      case Some(user) => UserByEmail(user)
      case None => UserByEmailNotFound(email)
    } answerTo(sender(), logFunction(m))
  }
}

