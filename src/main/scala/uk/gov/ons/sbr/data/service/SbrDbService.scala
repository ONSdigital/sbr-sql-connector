package uk.gov.ons.sbr.data.service

import com.typesafe.config._
import uk.gov.ons.sbr.data.db.{DbSchema, SbrDatabase}

import javax.inject.Singleton

@Singleton
class SbrDbService(dbConfig: Config) {

  // Set up DB...
  val db = new SbrDatabase(dbConfig)
  val initSchema: Boolean= dbConfig.getBoolean("init")
  val loadSample: Boolean= dbConfig.getBoolean("load")

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // create tables?
  if (initSchema) DbSchema.createSchema

  if (loadSample) DbSchema.loadDataIntoSchema

}
