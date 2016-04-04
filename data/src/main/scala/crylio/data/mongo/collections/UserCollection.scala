package crylio.data.mongo.collections

import crylio.common.Identifiers.UserId
import crylio.common.domain.User
import crylio.data.mongo.MongoCollection
import reactivemongo.api.DB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}

object UserCollection extends MongoCollection[UserId, User]{
  import crylio.data.mongo.DefaultBsonProtocol._

  override def name: String = "users"

  override def ensureIndexes(implicit db: DB, ec: ExecutionContext) =
    Future.sequence(List(
      Index(Seq("email" -> IndexType.Ascending), unique = true)
    ) map items.indexesManager.ensure).map(_ => {})

  implicit object UserWriter extends BSONDocumentWriter[User] {
    def write(value: User) = $doc(
      "_id" -> value.id,
      "email" -> value.email.toLowerCase,
      "displayName" -> value.displayName,
      "salt" -> value.salt,
      "hashedPassword" -> value.hashedPassword)
  }

  implicit object UserReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument) = User(
      id = doc.getAs[UserId]("_id").get,
      email = doc.getAs[String]("email").get,
      displayName = doc.getAs[String]("displayName").get,
      salt = doc.getAs[String]("salt").get,
      hashedPassword = doc.getAs[String]("hashedPassword").get)
  }

  def findUserByEmail(email: String)(implicit db: DB, ec: ExecutionContext): Future[Option[User]] =
    items.find($doc("email" -> email.toLowerCase)).one[User]
}
