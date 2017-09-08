package uk.gov.ons.sbr.data

import com.typesafe.config.ConfigFactory
import uk.gov.ons.sbr.data.db.{DbSchemaService, SbrDatabase}
import uk.gov.ons.sbr.data.model._

object DevDummyApp extends App {

  // Set up DB...
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("default")
  val db = new SbrDatabase(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // create table...
  DbSchemaService.createSchema

  // load demo data...
  DbSchemaService.loadDataIntoSchema

  // see if it got created
  var count = EnterpriseDao.count()
  println(s"Enterprises: $count")

  count = LegalUnitDao.count()
  println(s"Legal Units: $count")

  count = CompanyDao.count()
  println(s"Companies: $count")

  count = PayeDao.count()
  println(s"PAYE: $count")

  count = VatDao.count()
  println(s"VAT: $count")

  count = UnitLinksDao.count()
  println(s"Unit links: $count")

}
