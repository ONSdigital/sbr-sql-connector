package uk.gov.ons.sbr.data.model


import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import uk.gov.ons.sbr.data.SbrDatabase
import uk.gov.ons.sbr.data.utils.DbSchemaService

class UnitKeyRepoTest extends FlatSpec with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {

  // Set up DB...
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("test")
  val db = new SbrDatabase(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // Use same entity Repo object for all tests
  val ukRepo = UnitKeyRepo

  override def beforeAll(): Unit = {
    super.beforeAll()
    // create DB schema
    DbSchemaService.createSchema
  }


  override def afterAll(): Unit = {
    super.afterAll()
    // drop DB schema
    DbSchemaService.dropSchema
  }


  // delete all data between tests
  override def beforeEach(): Unit = {
    ukRepo.deleteAll
  }


  /** * TESTS START HERE ***/

  behavior of "UnitKeyRepo"

  it should "insert a range of different Unit keys and return correct count for ID" in {

    val refperiod = 201708 // not default period

    // create several records for each Unit type
    val uts = List("ENT","LEU","CH","PAYE","VAT")

    val numUnits = 10
    val ids = (1 to numUnits)
    val dummy = for {
      id <- ids
      ut <- uts
      key = s"$id"
      rec = UnitKey(refperiod, ut, key)
    } yield ukRepo.insert(rec)


    val results = ids.map {id => ukRepo.findById(refperiod, id.toString).size}
    // Should get count of 5 items for each of 10 IDs
    val expected = List.fill(10)(5)

    results shouldBe  expected

  }


  it should "insert a range of different Unit keys and return correct set of keys for ID" in {

    val refperiod = 201708 // not default period

    // create several records for each Unit type
    val uts = List("ENT","LEU","CH","PAYE","VAT")

    val numUnits = 10
    val ids = (1 to numUnits)
    val dummy = for {
      id <- ids
      ut <- uts
      key = s"$id"
      rec = UnitKey(refperiod, ut, key)
    } yield ukRepo.insert(rec)

    // Pick an ID at random
    val r = scala.util.Random
    val searchId : String = r.nextInt(numUnits) match {
      case 0 => s"1"
      case n => s"$n"
    }

    // Query the records back
    val results = ukRepo.findById(refperiod, searchId)

    val expected = uts.map{ut =>  UnitKey(refperiod, ut, searchId)}

    // Check the results are correct
    results should contain theSameElementsAs expected

  }
}
