package crylio.common.akka

import akka.actor.{Props, ActorRef, ActorContext}

import scala.reflect.ClassTag

trait CustomNodeSingleton {
  protected var actor: ActorRef = null

  val dispatcherName: Option[String] = None

  protected def createActor(props: Props, name: String)(implicit context: ActorContext) = {
    val dispatchedProps = dispatcherName.fold(props)(props.withDispatcher)
    actor = context.actorOf(dispatchedProps, name)
    actor
  }

  protected def createTypedActor[TActor](args: Any*)(implicit actorTag: ClassTag[TActor], context: ActorContext) = {
    val props = Props(actorTag.runtimeClass, args: _*)
    val name = actorTag.runtimeClass.getSimpleName
    createActor(props, name)
  }

  def endpoint = {
    if (actor != null) actor
    else throw new RuntimeException(s"Node singleton actor ${this.getClass.getSimpleName} is not initialized")
  }
}

trait NodeSingleton[TActor] extends CustomNodeSingleton {
  def create(implicit actorTag: ClassTag[TActor], context: ActorContext) =
    createTypedActor()
}

trait NodeSingleton1[TActor, TArg] extends CustomNodeSingleton {
  def create(arg: TArg)(implicit actorTag: ClassTag[TActor], context: ActorContext) =
    createTypedActor(arg)
}

trait NodeSingleton2[TActor, TArg1, TArg2] extends CustomNodeSingleton {
  def create(arg1: TArg1, arg2: TArg2)(implicit actorTag: ClassTag[TActor], context: ActorContext) =
    createTypedActor(arg1, arg2)
}