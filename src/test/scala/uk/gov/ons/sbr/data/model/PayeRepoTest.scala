package uk.gov.ons.sbr.data.model

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import uk.gov.ons.sbr.data.SbrDatabase
import uk.gov.ons.sbr.data.utils.DbSchemaService

class PayeRepoTest extends FlatSpec with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {

  // Set up DB...
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("test")
  val db = new SbrDatabase(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // Use same entity Repo object for all tests
  val entRepo = EnterpriseRepo
  val unitRepo = LegalUnitRepo
  val chRepo = CompanyRepo
  val payeRepo = PayeRepo

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
    payeRepo.deleteAll

    chRepo.deleteAll

    unitRepo.deleteAll

    entRepo.deleteAll
  }


  /** * TESTS START HERE ***/


  behavior of "PayeRepo"

  it should "insert new Enterprise, Legal Unit and PAYE, then query PAYE correctly with Ref Period" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // Now create PAYE for this LEU
    val pref = "PAYE0001"
    val paye: Paye = Paye(ref_period = refperiod, payeref = pref, leu_id = ubrn)
    val newPaye = payeRepo.insert(paye)

    // Now see if we can query it back
    val fetched = payeRepo.getPaye(refperiod, pref)

    fetched shouldBe (Some(newPaye))

  }


  it should "insert new Ent/LEU/PAYE then delete PAYE correctly" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // Now create PAYE for this LEU
    val pref = "PAYE0001"
    val paye: Paye = Paye(ref_period = refperiod, payeref = pref, leu_id = ubrn)
    val newPaye = payeRepo.insert(paye)


    // delete new PAYE
    newPaye.destroy()

    // Now see if we can query it back  (should be gone)
    val fetched = payeRepo.getPaye(refperiod, pref)

    fetched shouldBe (None)

  }

  it should "query right number of PAYE records for a given Legal Unit" in {

    val entref = 101L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 12345L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // create several records for this LEU
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val pref = s"PAYE$id"
      val paye: Paye = Paye(ref_period = refperiod, payeref = pref, leu_id = ubrn)
      val newPaye = payeRepo.insert(paye)
    }

    // count the records for this legal unit
    val fetched = payeRepo.getPayesForLegalUnit(refperiod, ubrn)

    // Check we got the right number of records
    fetched.size shouldBe numUnits
  }

  it should "query specific PAYE correctly from several records" in {
    val entref = 101L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 12345L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // create several records for this LEU
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val pref = s"PAYE$id"
      val paye: Paye = Paye(ref_period = refperiod, payeref = pref, name1 = Some(s"PAYE-NAME-$pref"),leu_id = ubrn)
      val newPaye = payeRepo.insert(paye)
    }

    // query a random record back for an ID between 1 and num (watch out for 0 coming back from random gen)
    val r = scala.util.Random
    val qid: String = r.nextInt(numUnits) match {
      case 0 => s"PAYE1"
      case n => s"PAYE$n"
    }

    // fetch the data
    val fetched = payeRepo.getPaye(refperiod, qid)

    val expectedName = Some(s"PAYE-NAME-$qid")
    // Remember name is an option as well
    val actualName = fetched.map(_.name1).flatten

    actualName shouldBe expectedName
  }


  it should "count PAYE records correctly" in {

    val entref = 101L
    val refperiod = 201706

    // assume Enterprise insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val newEnt: Enterprise = entRepo.insert(ent)

    // create two  Legal Units for this enterprise (UBRNs must be unique)

    val ubrn1 = 10001L
    // create Legal Unit for this Enterprise
    val leu1: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn1, entref = entref, businessname = Option(s"Legal Unit $ubrn1"))
    val newLeu1 = unitRepo.insert(leu1)

    // create several records for this LEU
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val pref = s"PAYE$id"
      val paye: Paye = Paye(ref_period = refperiod, payeref = pref, leu_id = ubrn1)
      val newPaye = payeRepo.insert(paye)
    }

    val ubrn2 = 10002L
    // create Legal Unit for this Enterprise
    val leu2: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn2, entref = entref, businessname = Option(s"Legal Unit $ubrn2"))
    val newLeu2 = unitRepo.insert(leu2)

    // create several records for this LEU
    val start = numUnits + 1
    val end = numUnits + numUnits
    (start to end) foreach { id =>
      val pref = s"PAYE$id"
      val paye: Paye = Paye(ref_period = refperiod, payeref = pref, leu_id = ubrn2)
      val newPaye = payeRepo.insert(paye)
    }

    // count ALL records i.e. includes both LEUs
    val expected = numUnits + numUnits
    val counted = payeRepo.count()

    counted shouldBe expected

  }
}