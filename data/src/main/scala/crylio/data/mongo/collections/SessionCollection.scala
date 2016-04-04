package crylio.data.mongo.collections

import crylio.common.Identifiers.UserId
import crylio.common.domain.Session
import crylio.data.mongo.MongoCollection
import org.joda.time.DateTime
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import reactivemongo.extensions.dao.Handlers._

object SessionCollection extends MongoCollection[String, Session]{
  import crylio.data.mongo.DefaultBsonProtocol._

  override def name: String = "sessions"

  implicit object UserWriter extends BSONDocumentWriter[Session] {
    def write(value: Session) = $doc(
      "_id" -> value.token,
      "userId" -> value.userId,
      "createdAt" -> value.createdAt)
  }

  implicit object UserReader extends BSONDocumentReader[Session] {
    def read(doc: BSONDocument) = Session(
      token = doc.getAs[String]("_id").get,
      userId = doc.getAs[UserId]("userId").get,
      createdAt = doc.getAs[DateTime]("createdAt").get)
  }
}
