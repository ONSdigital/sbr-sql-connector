package uk.gov.ons.sbr.data.model


import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import uk.gov.ons.sbr.data.SbrDatabase
import uk.gov.ons.sbr.data.utils.DbSchemaService

class UnitLinksRepoTest extends FlatSpec with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {

  // Set up DB...
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("test")
  val db = new SbrDatabase(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // Use same entity Repo object for all tests
  val ukRepo = UnitLinksRepo

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

  behavior of "UnitLinksRepo"

  it should "insert a range of different Unit Links and return correct count for ID" in {

    val refperiod = 201708 // not default period

    // create several records for each Unit type
    val uts = List("ENT","LEU","CH","PAYE","VAT")

    val numUnits = 10
    val ids = (1 to numUnits)
    val dummy = for {
      id <- ids
      ut <- uts
      key = s"$id"
      rec = UnitLinks(refperiod, ut, key, None, None, None)
    } yield ukRepo.insert(rec)


    val results = ids.map {id => ukRepo.findById(refperiod, id.toString).size}
    // Should get count of 5 items for each of 10 IDs
    val expected = List.fill(10)(5)

    results shouldBe  expected

  }


  it should "insert a range of different Unit Links and return correct set of keys for ID" in {

    val refperiod = 201708 // not default period

    // create several records for each Unit type
    val uts = List("ENT","LEU","CH","PAYE","VAT")

    val numUnits = 10
    val ids = (1 to numUnits)
    val dummy = for {
      id <- ids
      ut <- uts
      key = s"$id"
      rec = UnitLinks(refperiod, ut, key, None, None, None)
    } yield ukRepo.insert(rec)

    // Pick an ID at random
    val r = scala.util.Random
    val searchId : String = r.nextInt(numUnits) match {
      case 0 => s"1"
      case n => s"$n"
    }

    // Query the records back
    val results = ukRepo.findById(refperiod, searchId)

    val expected = uts.map{ut =>  UnitLinks(refperiod, ut, searchId)}

    // Check the results are correct
    results should contain theSameElementsAs expected

  }

  it should "insert a Unit Links record and allow update to non-key fields" in {

    val refperiod = 201708 // not default period

    // create several records for each Unit type
    val ut = "LEU"
    val entref = 1000L
    val ubrn = 2000L
    val children = "{}"
    val rec = UnitLinks(refperiod, ut, s"$ubrn", Option(entref), Option(ubrn), Option(children) )
    val unitLink:UnitLinks = ukRepo.insert(rec)

    // Now update the record

    val entref2 = 1001L
    val ubrn2 = 2001L
    val children2 = "{CHANGED}"
    // Modify and save the record
    val updated: UnitLinks = unitLink.copy(pEnt = Some(entref2), pLeu = Some(ubrn2), children = Some(children2)).save()

    val results = ukRepo.findByKey(refperiod, ut, ubrn.toString)
    val expected = Option(updated)

    results shouldBe  expected

  }
}
