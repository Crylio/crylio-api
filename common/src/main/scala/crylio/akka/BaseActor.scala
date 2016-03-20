package crylio.akka

import akka.actor.{ActorLogging, Actor, ActorRef, Cancellable}

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

}
