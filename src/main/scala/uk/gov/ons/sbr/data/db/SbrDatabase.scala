package uk.gov.ons.sbr.data.db

import javax.inject.Singleton

import com.typesafe.config._
import scalikejdbc._

@Singleton
class SbrDatabase(dbConfig: Config) {

  // logging etc
  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = false,
    singleLineMode = false,
    printUnprocessedStackTrace = false,
    stackTraceDepth= 15,
    logLevel = 'warn,
    warningEnabled = false,
    warningThresholdMillis = 3000L,
    warningLogLevel = 'warn
  )

  // Init DB
  val url = dbConfig.getString("url")
  val driver = dbConfig.getString("driver")
  val username = dbConfig.getString("username")
  val password = dbConfig.getString("password")

  // initialize JDBC driver & connection pool
  Class.forName(driver)
  ConnectionPool.singleton(url, username, password)

  // Make common session available
  implicit val session = AutoSession

}
