package crylio.app

import akka.actor.Props
import akka.cluster.Cluster
import akka.io.IO
import akka.io.Tcp.Bound
import com.typesafe.config.ConfigFactory
import crylio.common.akka.BaseActor
import crylio.data.repositories.{SessionRepository, UserRepository}
import crylio.rest.{RestSettings, RestService}
import spray.can.Http
import spray.can.server.{ServerSettings, UHttp}

class AppGuardianActor extends BaseActor {

  lazy val cluster = Cluster(context.system)
  lazy val restSettings = RestSettings(context.system)
  lazy val ioHttp = IO(UHttp)(context.system)

  log.debug("Node roles: " + cluster.selfRoles.mkString(", "))

  startRepositories()
  startRest()

  def receive = {
    case _: Bound =>
  }

  lazy val configWithoutSSL = ConfigFactory
    .parseString("spray.can.server.ssl-encryption=off")
    .withFallback(context.system.settings.config)

  def startRest() = {
    val rest = context.actorOf(Props[RestService], "rest")
    ioHttp ! Http.Bind(rest, restSettings.interface, restSettings.port)
    val noSslSettings = ServerSettings(configWithoutSSL)
    ioHttp ! Http.Bind(rest, restSettings.interface, restSettings.portWithoutSSL, settings = Some(noSslSettings))
  }

  def startRepositories() = {
    UserRepository.create
    SessionRepository.create
  }
}
