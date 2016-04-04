package crylio.rest

import akka.util.Timeout
import crylio.common.akka.BaseActor
import crylio.rest.Errors.RestException
import crylio.rest.Rejections.TooManyRequestsRejection
import crylio.rest.routes.UserRoutes
import spray.http.StatusCodes._
import spray.routing.{ExceptionHandler, HttpService, RejectionHandler}

import scala.concurrent._

class RestService extends BaseActor with HttpService
with UserRoutes {

  def actorRefFactory = context

  val settings = RestSettings(context.system)

  implicit val timeout: Timeout = Timeout(settings.defaultTimeout)

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
       _.complete(e.result.status -> exceptionMessage(e))
    case e: TimeoutException =>
      log.error(e, "TimeoutException")
      _.complete(RequestTimeout -> exceptionMessage(e))
    case e: IllegalArgumentException =>
      log.error(e, "IllegalArgumentException")
      _.complete(BadRequest -> exceptionMessage(e))
    case e: Throwable =>
      log.error(e, "Throwable")
      _.complete(InternalServerError -> exceptionMessage(e))
  }

  def exceptionMessage(e: Throwable) = if (settings.showExceptions) e.getMessage else ""


  def noopRoute =
    path("noop") {
      complete(OK)
    }

  def versionRoute01 = pathPrefix("0.1") {
    noopRoute ~ user01routes
  }
}
