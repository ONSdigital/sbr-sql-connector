package uk.gov.ons.sbr.data

import com.typesafe.config.ConfigFactory
import uk.gov.ons.sbr.data.db._
import uk.gov.ons.sbr.data.model._
import uk.gov.ons.sbr.data.service.SbrDbService

object DevDummyApp extends App {

  // Get DB config info
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("default")
  // Start DbService with this config
  val dbService = new SbrDbService(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = dbService.session

  // see if data got created
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
