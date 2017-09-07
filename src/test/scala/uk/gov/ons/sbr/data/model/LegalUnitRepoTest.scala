package uk.gov.ons.sbr.data.model

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import uk.gov.ons.sbr.data.SbrDatabase
import uk.gov.ons.sbr.data.utils.DbSchemaService

class LegalUnitRepoTest extends FlatSpec with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {

  // Set up DB...
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("test")
  val db = new SbrDatabase(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // Use same Repo object for all tests
  val entRepo = EnterpriseRepo
  val unitRepo = LegalUnitRepo

  override def beforeAll(): Unit = {
    super.beforeAll()
    // create DB schema (once only)
    DbSchemaService.createSchema
  }


  override def afterAll(): Unit = {
    super.afterAll()
    // drop DB schema
    DbSchemaService.dropSchema
  }


  // delete all data between tests
  override def beforeEach(): Unit = {
    unitRepo.deleteAll

    entRepo.deleteAll
  }

  /** * TESTS START HERE ***/

  behavior of "LegalUnitRepo"

  it should "insert new Enterprise and Legal Unit, and query Legal Unit correctly with Ref Period" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // Now see if we can query it back
    val fetched = unitRepo.getLegalUnit(refperiod, ubrn)

    fetched shouldBe (Some(newLeu))
  }

  it should "insert new Ent/LEU and delete Legal Unit correctly" in {

    val entref = 101L
    val refperiod = 201706

    // assume insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val newEnt: Enterprise = entRepo.insert(ent)


    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // delete new Legal Unit record
    newLeu.destroy()

    // see if Legal Unit still exists (query works - tested elsewhere)
    val fetched = unitRepo.getLegalUnit(refperiod, ubrn)

    fetched shouldBe (None)

  }

  it should "query specific Legal Unit correctly from several records" in {

    val entref = 101L
    val refperiod = 201706

    // assume Enterprise insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val newEnt: Enterprise = entRepo.insert(ent)

    // create several Legal Units

    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = id, entref = entref, businessname = Option(s"Legal Unit $id"))
      unitRepo.insert(leu)
    }

    // query a random record back for an ID between 1 and numEnts (watch out for 0 coming back from random gen)
    val r = scala.util.Random
    val qid: Long = r.nextInt(numUnits) match {
      case 0 => 1
      case n => n
    }

    // fetch the data
    val fetched = unitRepo.getLegalUnit(refperiod, qid)

    val expectedName = Some(s"Legal Unit $qid")
    // Remember name is an option as well
    val actualName = fetched.map(_.businessname).flatten

    actualName shouldBe expectedName
  }

  it should "query right number of Legal Unit records for a given Enterprise" in {

    val entref = 101L
    val refperiod = 201706

    // assume Enterprise insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val newEnt: Enterprise = entRepo.insert(ent)

    // create several Legal Units
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = id, entref = entref, businessname = Option(s"Legal Unit $id"))
      unitRepo.insert(leu)
    }

    // fetch the legal units for this enterprise
    val fetched = unitRepo.getLegalUnitsForEnterprise(refperiod, entref)

    // Check we got the right number of records
    fetched.size shouldBe numUnits
  }

  it should "count Legal Unit records correctly" in {

    val entref = 101L
    val refperiod = 201706
    val numUnits = 10

    // assume Enterprise insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val newEnt: Enterprise = entRepo.insert(ent)

    // create several Legal Units for this enterprise
    (1 to numUnits) foreach { id =>
      val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = id, entref = entref, businessname = Option(s"Legal Unit $id"))
      unitRepo.insert(leu)
    }

    // now add another Ent and same number of Legal Units
    val entref2 = entref + 1
    val ent2 = Enterprise(ref_period = refperiod, entref = entref2, ent_tradingstyle = Option(s"Entity $entref2"))
    entRepo.insert(ent2)

    // create several Legal Units for this enterprise (UBRNs must be unique)
    val start = numUnits + 1
    val end = numUnits + numUnits
    (start to end) foreach { id =>
      val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = id, entref = entref, businessname = Option(s"Legal Unit $id"))
      unitRepo.insert(leu)
    }

    // count ALL legal units
    val expected = numUnits + numUnits
    val counted = unitRepo.count()

    counted shouldBe expected

  }
}
