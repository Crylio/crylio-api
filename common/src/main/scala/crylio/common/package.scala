package crylio

import java.security.MessageDigest
import java.util.UUID

import org.joda.time.{DateTimeZone, DateTime}

package object common {
  type Tagged[U] = {type Tag = U}
  type @@[T, U] = T with Tagged[U]

  def newUUID = UUID.randomUUID().toString

  def utcNow = DateTime.now(DateTimeZone.UTC)

  def sha256(text: String) = MessageDigest
    .getInstance("SHA-256")
    .digest(text.getBytes)
    .map("%02x".format(_))
    .mkString
}
