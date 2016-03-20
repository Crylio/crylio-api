package crylio.app

import akka.actor.Props
import akka.cluster.Cluster
import akka.io.IO
import akka.io.Tcp.Bound
import com.typesafe.config.ConfigFactory
import crylio.akka.BaseActor
import crylio.common.Settings
import crylio.rest.RestService
import spray.can.Http
import spray.can.server.{ServerSettings, UHttp}

class AppGuardianActor extends BaseActor {

  lazy val cluster = Cluster(context.system)
  lazy val settings = Settings(context.system.settings.config)
  lazy val ioHttp = IO(UHttp)(context.system)

  log.debug("Node roles: " + cluster.selfRoles.mkString(", "))

  def receive = {
    case _: Bound =>
  }

  lazy val configWithoutSSL = ConfigFactory
    .parseString("spray.can.server.ssl-encryption=off")
    .withFallback(context.system.settings.config)

  def startRest() = {
    val rest = context.actorOf(Props[RestService], "rest")
    ioHttp ! Http.Bind(rest, settings.rest.interface, settings.rest.port)
    val noSslSettings = ServerSettings(configWithoutSSL)
    ioHttp ! Http.Bind(rest, settings.rest.interface, settings.rest.portWithoutSSL, settings = Some(noSslSettings))
  }
}
