package crylio.data.repositories

import crylio.common.akka.Implicits.RichFuture
import crylio.common.akka.NodeSingleton
import crylio.data.messages.SessionRepositoryMessages.{SessionAdded, AddSession}
import crylio.data.mongo.MongoRepository

object SessionRepository extends NodeSingleton[SessionRepository]

class SessionRepository extends MongoRepository {
  import crylio.data.mongo.collections.SessionCollection._

  def checkIndexes(): Unit = {
    ensureIndexes
  }

  override def receive: Receive = {
    case m @ AddSession(session) => add(session) map {
      case _ => SessionAdded(session.token)
    } answerTo(sender(), logFunction(m))
  }
}
