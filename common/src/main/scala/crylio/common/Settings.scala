package crylio.common

import com.typesafe.config.{Config, ConfigFactory}

class Settings(config: Config) {
  lazy val crylio = config.getConfig("crylio")
  lazy val rest = RestSettings(crylio.getConfig("rest"))
}

object Settings {
  def apply(): Settings = new Settings(ConfigFactory.load())
  def apply(config: Config): Settings = new Settings(config.withFallback(ConfigFactory.load()))
}

case class RestSettings(config: Config) {
  lazy val interface = config.getString("interface")
  lazy val port = config.getInt("port")
  lazy val portWithoutSSL = config.getInt("portWithoutSSL")
}

