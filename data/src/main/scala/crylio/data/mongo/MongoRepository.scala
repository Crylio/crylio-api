package crylio.data.mongo

import akka.actor._
import com.typesafe.config.Config
import crylio.common.akka.BaseActor
import reactivemongo.api.MongoDriver

import scala.collection.JavaConversions._

trait MongoRepository extends BaseActor {
  implicit val dispatcher = context.dispatcher
  private[this] val settings = MongoSettings(context.system)
  private[this] val connection = connect()
  implicit val db = connection(settings.dbName)
  private[this] var receiveStartTime = 0L

  private[this] def connect() = {
    val driver = new MongoDriver()
    val connection = driver.connection(settings.hosts)
    connection
  }

  def logFunction(inputMessage: Any): PartialFunction[Any, Unit] = {
    case Status.Failure(e) => log.error(e, s"Mongo Storage error. Message: $inputMessage")
  }

  override def aroundReceive(body: Receive, msg: Any): Unit = {
    receiveStartTime = System.currentTimeMillis()
    super.aroundReceive(body, msg)
  }

  def checkIndexes(): Unit

  checkIndexes()
}

class MongoSettingsImpl(config: Config) extends Extension {
  private[this] val mongoSettings = config.getConfig("crylio.mongo")
  val hosts = mongoSettings.getStringList("hosts").toList
  val dbName = mongoSettings.getString("dbName")
}

object MongoSettings extends ExtensionId[MongoSettingsImpl] with ExtensionIdProvider {
  override def lookup() = MongoSettings

  override def createExtension(system: ExtendedActorSystem) = new MongoSettingsImpl(system.settings.config)
}
