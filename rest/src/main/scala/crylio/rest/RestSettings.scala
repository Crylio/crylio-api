package crylio.rest

import java.util.concurrent.TimeUnit

import akka.actor.{Extension, ExtendedActorSystem, ExtensionIdProvider, ExtensionId}
import com.typesafe.config.Config

import scala.concurrent.duration.DurationLong

case class RestSettingsImpl(config: Config) extends Extension {
  private[this] val restSettings = config.getConfig("crylio.rest")

  lazy val interface = restSettings.getString("interface")
  lazy val port = restSettings.getInt("port")
  lazy val portWithoutSSL = restSettings.getInt("portWithoutSSL")
  lazy val showExceptions = restSettings.getBoolean("showExceptions")
  lazy val defaultTimeout = restSettings.getDuration("defaultTimeout", TimeUnit.MILLISECONDS).milliseconds
}

object RestSettings extends ExtensionId[RestSettingsImpl] with ExtensionIdProvider {
  override def lookup() = RestSettings

  override def createExtension(system: ExtendedActorSystem) = new RestSettingsImpl(system.settings.config)
}
