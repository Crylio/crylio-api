package crylio.common.akka

import java.util.concurrent.TimeoutException

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import crylio.common.messages.CommonMessages.Start

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.Failure

object Implicits {

  def normalizeAskResult(msg: Any): Future[Any] = msg match {
    case Failure(exception) => Future.failed(exception)
    case Status.Failure(exception) => Future.failed(exception)
    case ReceiveTimeout => Future.failed(new TimeoutException())
    case result => Future.successful(result)
  }

  implicit class RichProps(props: Props) {

    def execute[T](implicit tag: ClassTag[T],
                   context: ActorContext,
                   executionContext: ExecutionContext,
                   timeout: Timeout): Future[T] =
      (context.actorOf(props) ? Start flatMap normalizeAskResult).mapTo[T]
  }

  implicit class RichFuture[T](val future: Future[T]) {
    def answerTo(destination: ActorRef, logFun: PartialFunction[Any, Unit])(implicit executionContext: ExecutionContext) = {
      future recover { case e =>
        Status.Failure(e)
      } map { result =>
        destination ! result
        if (logFun.isDefinedAt(result)) logFun(result)
      }
    }
  }

}
