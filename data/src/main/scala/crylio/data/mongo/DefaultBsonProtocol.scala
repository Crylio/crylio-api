package crylio.data.mongo

import crylio.common.Identifiers._
import reactivemongo.bson.{BSONWriter, BSONString, BSONReader}

object DefaultBsonProtocol {
  implicit object UserIdReader extends BSONReader[BSONString, UserId] {
    def read(bson: BSONString): UserId = bson.value.as[UserId]
  }

  implicit object UserIdWriter extends BSONWriter[UserId, BSONString] {
    def write(value: UserId): BSONString = BSONString(value)
  }
}
