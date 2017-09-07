package uk.gov.ons.sbr.data

import com.typesafe.config.ConfigFactory
import uk.gov.ons.sbr.data.model._
import uk.gov.ons.sbr.data.utils.DbSchemaService

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
  var count = EnterpriseRepo.count()
  println(s"Enterprises: $count")

  count = LegalUnitRepo.count()
  println(s"Legal Units: $count")

  count = CompanyRepo.count()
  println(s"Companies: $count")

  count = PayeRepo.count()
  println(s"PAYE: $count")

  count = VatRepo.count()
  println(s"VAT: $count")

  count = UnitKeyRepo.count()
  println(s"Unit keys: $count")


}
