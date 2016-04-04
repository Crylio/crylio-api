package crylio.common.akka

import akka.actor._
import crylio.common.messages.CommonMessages.Start

import scala.concurrent.TimeoutException
import scala.concurrent.duration.DurationInt
import scala.util.Failure

trait StartupActor extends BaseActor {

  var originalSender: ActorRef = ActorRef.noSender
  val receiveTimeout = 5.seconds
  var started: Boolean = false

  def start(): Unit

  override def aroundReceive(receive : Receive, msg : Any): Unit = super.aroundReceive(handle(receive), msg)

  def handle(body: Receive): Receive = {
    case Start if !started =>
      originalSender = sender()
      context.setReceiveTimeout(receiveTimeout)
      started = true
      start()
    case msg if body.isDefinedAt(msg) => body(msg)
    case msg if exceptionExtractor.isDefinedAt(msg) => failure(exceptionExtractor(msg))
  }

  def complete(msg: Any) =  {
    answer(msg)
    stop()
  }

  def failure(exception: Throwable): Unit = complete(Status.Failure(exception))

  def failure(errorMessage: String): Unit = failure(new IllegalArgumentException(errorMessage))

  def answer(msg: Any) = {
    originalSender ! msg
  }

  def exceptionExtractor: PartialFunction[Any, Throwable] = {
    case Failure(exception) ⇒ exception
    case Status.Failure(exception) ⇒ exception
    case ReceiveTimeout ⇒ new TimeoutException(s"${this.getClass.getName}: Timeout has been reached")
  }
}

