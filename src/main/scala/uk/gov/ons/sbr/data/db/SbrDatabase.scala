/**
 * SbrDatabase.scala
 * --------------
 * Author: websc
 * Date: 08/09/17 14:33
 * Copyright (c) 2017  Office for National Statistics
 */

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
  private val url = dbConfig.getString("url")
  private val driver = dbConfig.getString("driver")
  private val username = dbConfig.getString("username")
  private val password = dbConfig.getString("password")

  // initialize JDBC driver & connection pool
  Class.forName(driver)
  ConnectionPool.singleton(url, username, password)

  // Make common session available
  implicit val session = AutoSession

}
