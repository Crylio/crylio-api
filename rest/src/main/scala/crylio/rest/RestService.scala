package crylio.rest

import crylio.akka.BaseActor
import crylio.rest.Errors.RestException
import crylio.rest.Rejections.TooManyRequestsRejection
import spray.http.StatusCodes._
import spray.routing.{ExceptionHandler, HttpService, RejectionHandler}

import scala.concurrent._

class RestService extends BaseActor with HttpService {
 
  def actorRefFactory = context

  def receive = runRoute(route)

  val rejectionHandler = RejectionHandler {
    case TooManyRequestsRejection(retryAfter) :: _ =>
      respondWithHeader(`Retry-After`(retryAfter)) {
        complete(TooManyRequests)
      }
  } orElse RejectionHandler.Default

  val route =
    compressResponseIfRequested() {
      handleExceptions(exceptionHandler) {
        handleRejections(rejectionHandler) {
          versionRoute01
        }
      }
    }

  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: RestException =>
      log.error(e, "RestException")
      // _.complete(e.statusCode -> ResponseMessageDTO.failed(e.code, e.message))
      _.complete("f")
    //TODO: Add ResponseMessageDTO
    case e: TimeoutException =>
      log.error(e, "TimeoutException")
      _.complete(RequestTimeout)
    case e: IllegalArgumentException =>
      log.error(e, "IllegalArgumentException")
      _.complete(BadRequest -> e.getMessage)
    case e: Throwable =>
      log.error(e, "Throwable")
      _.complete(InternalServerError)
  }

  def noopRoute =
    path("noop") {
      complete(OK)
    }

  def versionRoute01 = pathPrefix("0.1") {
    noopRoute
  }
}
