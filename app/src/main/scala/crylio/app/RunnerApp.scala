package crylio.app

import akka.actor._
import com.typesafe.config.ConfigFactory

object RunnerApp extends App {
  println("Hello Crylio!")
  if (args.length >= 1) {
    val roles = args(0)
    val port = if (args.length >= 2) args(1).toInt else 0

    val config =
      ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
        .withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [$roles]"))
        //TODO: Add kryo serialization
        //.withFallback(KryoSerializationBindings.config)
        .withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[AppGuardianActor], name = "guardian")

  } else {
    println("Usage: runMain crylio.app.RunnerApp <nodeRoles> Option<port>")
    println("       available roles: ")
  }
}