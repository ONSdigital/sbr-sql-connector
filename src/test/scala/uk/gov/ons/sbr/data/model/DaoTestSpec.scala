package uk.gov.ons.sbr.data.model

import com.typesafe.config.ConfigFactory
import org.scalatest._
import uk.gov.ons.sbr.data.db._
import uk.gov.ons.sbr.data.service.SbrDbService

trait DaoTest extends BeforeAndAfterAll with BeforeAndAfterEach { this: Suite =>

  // Set up DB...
  // Get DB config info
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("test")
  // Start DbService with this config
  val dbService = new SbrDbService(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = dbService.session


  // Use same entity Repo objects for all tests
  val entDao = dbService.entDao
  val unitDao = dbService.unitDao
  val chDao = dbService.chDao
  val payeDao = dbService.payeDao
  val vatDao = dbService.vatDao
  val linksDao = dbService.linksDao


  override def beforeAll(): Unit = {
    super.beforeAll()
    // create DB schema
    dbService.dbSchema.createSchema
  }

  override def afterAll(): Unit = {
    super.afterAll()
    // drop DB schema
    dbService.dbSchema.dropSchema
  }

  // delete all data between tests
  override def beforeEach(): Unit = {
    linksDao.deleteAll

    vatDao.deleteAll

    payeDao.deleteAll

    chDao.deleteAll

    unitDao.deleteAll

    entDao.deleteAll
  }

}
