package uk.gov.ons.sbr.data.model

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import uk.gov.ons.sbr.data.SbrDatabase
import uk.gov.ons.sbr.data.utils.DbSchemaService

class CompanyRepoTest extends FlatSpec with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {

  // Set up DB...
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("test")
  val db = new SbrDatabase(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // Use same Repo object for all tests
  val entRepo = EnterpriseRepo
  val unitRepo = LegalUnitRepo
  val chRepo = CompanyRepo

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
    chRepo.deleteAll

    unitRepo.deleteAll

    entRepo.deleteAll
  }


  /** * TESTS START HERE ***/

  behavior of "CompanyRepo"

  it should "insert new Enterprise, Legal Unit and Company, then query Company correctly with Ref Period" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // Now create Company for this LEU
    val coNo = "COMPANY0001"
    val co: Company = Company(ref_period = refperiod, companynumber = coNo, companyname = Some(s"Test company $coNo"), ubrn = ubrn)
    val newCo = chRepo.insert(co)

    // Now see if we can query it back
    val fetched = chRepo.getCompany(refperiod, coNo)

    fetched shouldBe (Some(newCo))

  }

  it should "insert new Ent/LEU and delete Company correctly" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // Now create Company for this LEU
    val coNo = "COMPANY0001"
    val co: Company = Company(ref_period = refperiod, companynumber = coNo, companyname = Some(s"Test company $coNo"), ubrn = ubrn)
    val newCo = chRepo.insert(co)

    // delete new Company
    newCo.destroy()

    // Now see if we can query it back  (should be gone)
    val fetched = chRepo.getCompany(refperiod, coNo)

    fetched shouldBe (None)

  }

  it should "query right number of Company records for a given Legal Unit" in {

    val entref = 101L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 12345L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // create several Companies for this LEU
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val coNo = s"COMPANY$id"
      val data: Company = Company(ref_period = refperiod, companynumber = coNo, companyname = Some(s"Test company $coNo"), ubrn = ubrn)
      chRepo.insert(data)
    }

    // count the records for this legal unit
    val fetched = chRepo.getCompaniesForLegalUnit(refperiod, ubrn)

    // Check we got the right number of records
    fetched.size shouldBe numUnits
  }

  it should "query specific Company correctly from several records" in {
    val entref = 101L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 12345L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // create several Companies for this LEU
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val coNo = s"COMPANY$id"
      val data: Company = Company(ref_period = refperiod, companynumber = coNo, companyname = Some(s"Test company $coNo"), ubrn = ubrn)
      chRepo.insert(data)
    }

    // query a random record back for an ID between 1 and num (watch out for 0 coming back from random gen)
    val r = scala.util.Random
    val qid: String = r.nextInt(numUnits) match {
      case 0 => s"COMPANY1"
      case n => s"COMPANY$n"
    }

    // fetch the data
    val fetched = chRepo.getCompany(refperiod, qid)

    val expectedName = Some(s"Test company $qid")
    // Remember name is an option as well
    val actualName = fetched.map(_.companyname).flatten

    actualName shouldBe expectedName
  }

  it should "count Company records correctly" in {

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

    // create several Companies for this LEU
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val coNo = s"COMPANY$id"
      val data: Company = Company(ref_period = refperiod, companynumber = coNo, companyname = Some(s"Test company $coNo"), ubrn = ubrn1)
      chRepo.insert(data)
    }

    val ubrn2 = 10002L
    // create Legal Unit for this Enterprise
    val leu2: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn2, entref = entref, businessname = Option(s"Legal Unit $ubrn2"))
    val newLeu2 = unitRepo.insert(leu2)

    // create several Companies for this LEU
    val start = numUnits + 1
    val end = numUnits + numUnits
    (start to end) foreach { id =>
      val coNo = s"COMPANY$id"
      val data: Company = Company(ref_period = refperiod, companynumber = coNo, companyname = Some(s"Test company $coNo"), ubrn = ubrn2)
      chRepo.insert(data)
    }

    // count ALL Companies i.e. includes both LEUs
    val expected = numUnits + numUnits
    val counted = chRepo.count()

    counted shouldBe expected

  }
}
