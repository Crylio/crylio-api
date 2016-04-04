package crylio.common.akka

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable}

import scala.concurrent.duration.FiniteDuration

trait BaseActor extends Actor with ActorLogging {

  override def postStop() = {
    super.postStop()
    schedulers.foreach(_.cancel())
  }

  private [this] var schedulers: List[Cancellable] = Nil

  val emptyBehavior = Actor.emptyBehavior

  def become(behavior: Receive, discardOld: Boolean = true): Unit = context.become(behavior, discardOld)

  def unbecome(): Unit = context.unbecome()

  def watch(subject: ActorRef) = context.watch(subject)

  def unwatch(subject: ActorRef) = context.unwatch(subject)

  def scheduleOnce(delay: FiniteDuration,
                   message: Any,
                   receiver: ActorRef = self): Cancellable = {
    import context.dispatcher
    val cancellable = context.system.scheduler.scheduleOnce(delay, receiver, message)
    schedulers ::= cancellable
    cancellable
  }

  def schedule(initialDelay: FiniteDuration,
               interval: FiniteDuration,
               message: Any): Cancellable = {
    import context.dispatcher
    val cancellable = context.system.scheduler.schedule(initialDelay, interval, self, message)
    schedulers ::= cancellable
    cancellable
  }

  def schedule(interval: FiniteDuration,
               message: Any): Cancellable =
    schedule(interval, interval, message)

  def stop() = context.stop(self)

  def actorPath = context.self.path.toStringWithoutAddress

  override def aroundReceive(body: Receive, msg: Any): Unit = {
    val logMessage = s"$actorPath received ${if (body.isDefinedAt(msg)) "" else "un"}handled message $msg"
    if (body.isDefinedAt(msg)) log.debug(logMessage) else log.error(logMessage)
    try {
      super.aroundReceive(body, msg)
    } catch {
      case e: Throwable =>
        log.error(e, s"$actorPath handling $msg throws $e")
        throw e
    }
  }
}
