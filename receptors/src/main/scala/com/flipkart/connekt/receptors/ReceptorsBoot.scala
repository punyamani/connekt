package com.flipkart.connekt.receptors

import java.util.concurrent.atomic.AtomicBoolean

import com.flipkart.connekt.commons.connections.ConnectionProvider
import com.flipkart.connekt.commons.core.BaseApp
import com.flipkart.connekt.commons.dao.DaoFactory
import com.flipkart.connekt.commons.factories.{ConnektLogger, LogFile, ServiceFactory}
import com.flipkart.connekt.commons.helpers.KafkaProducerHelper
import com.flipkart.connekt.commons.services.ConnektConfig
import com.flipkart.connekt.commons.sync.SyncManager
import com.flipkart.connekt.commons.utils.ConfigUtils
import com.flipkart.connekt.receptors.service.ReceptorsServer
import com.typesafe.config.ConfigFactory

/**
 *
 *
 * @author durga.s
 * @version 11/20/15
 */
object ReceptorsBoot extends BaseApp {

  private val initialized = new AtomicBoolean(false)


  def start() {
    if (!initialized.getAndSet(true)) {

      ConnektLogger(LogFile.SERVICE).info("Receptors initializing.")

      val configFile = ConfigUtils.getSystemProperty("logback.config").getOrElse("logback-receptors.xml")
      val logConfigFile = getClass.getClassLoader.getResourceAsStream(configFile)

      ConnektLogger(LogFile.SERVICE).info(s"Receptors Logging using $configFile")
      ConnektLogger.init(logConfigFile)

      ConnektConfig(configServiceHost, configServicePort)()

      SyncManager.create(ConnektConfig.getString("sync.zookeeper").get)

      DaoFactory.setUpConnectionProvider(new ConnectionProvider())

      val hConfig = ConnektConfig.getConfig("receptors.connections.hbase")
      DaoFactory.initHTableDaoFactory(hConfig.get)

      val mysqlConf = ConnektConfig.getConfig("receptors.connections.mysql").getOrElse(ConfigFactory.empty())
      DaoFactory.initMysqlTableDaoFactory(mysqlConf)

      val couchbaseCf = ConnektConfig.getConfig("receptors.connections.couchbase").getOrElse(ConfigFactory.empty())
      DaoFactory.initCouchbaseCluster(couchbaseCf)

      val specterConfig = ConnektConfig.getConfig("receptors.connections.specter").getOrElse(ConfigFactory.empty())
      DaoFactory.initSpecterSocket(specterConfig)

      val kafkaConnConf = ConnektConfig.getConfig("receptors.connections.kafka.producerConnProps").getOrElse(ConfigFactory.empty())
      val kafkaProducerPoolConf = ConnektConfig.getConfig("receptors.connections.kafka.producerPool").getOrElse(ConfigFactory.empty())
      val kafkaProducerHelper = KafkaProducerHelper.init(kafkaConnConf, kafkaProducerPoolConf)

      ServiceFactory.initPNMessageService(DaoFactory.getPNRequestDao, DaoFactory.getUserConfigurationDao, kafkaProducerHelper, null)
      ServiceFactory.initCallbackService(null, DaoFactory.getPNCallbackDao, DaoFactory.getPNRequestDao, null)
      ServiceFactory.initAuthorisationService(DaoFactory.getPrivDao, DaoFactory.getUserInfoDao)
      ServiceFactory.initStorageService(DaoFactory.getKeyChainDao)

      //Start up the receptors
      ReceptorsServer()

      ConnektLogger(LogFile.SERVICE).info("Receptors initialized.")
    }
  }

  def terminate() = {
    ConnektLogger(LogFile.DAO).info("Receptors terminating.")
    if (initialized.get()) {
      ReceptorsServer.shutdown()
      DaoFactory.shutdownHTableDaoFactory()
    }
  }

  def main(args: Array[String]) {
    System.setProperty("logback.config", "logback-test.xml")
    start()
  }
}
