package crylio.rest

import spray.http.StatusCode

object Errors {
  type ResultCode = String

  case class RestException(statusCode: StatusCode, code: ResultCode, message: String)
    extends RuntimeException(s"${statusCode.value} $code $message")
}
