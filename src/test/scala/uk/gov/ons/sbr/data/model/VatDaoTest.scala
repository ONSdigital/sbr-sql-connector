package uk.gov.ons.sbr.data.model

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import uk.gov.ons.sbr.data.db.{DbSchemaService, SbrDatabase}

class VatDaoTest extends FlatSpec with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {

  // Set up DB...
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("test")
  val db = new SbrDatabase(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // Use same entity Repo object for all tests
  val entRepo = EnterpriseDao
  val unitRepo = LegalUnitDao
  val chRepo = CompanyDao
  val payeRepo = PayeDao
  val vatRepo = VatDao

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
    vatRepo.deleteAll

    payeRepo.deleteAll

    chRepo.deleteAll

    unitRepo.deleteAll

    entRepo.deleteAll
  }


  /** * TESTS START HERE ***/


  behavior of "VatRepo"

  it should "insert new Enterprise, Legal Unit and VAT, then query VAT correctly with Ref Period" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // Now create VAT for this LEU
    val ref = "VAT0001"
    val vat: Vat = Vat(ref_period = refperiod, vatref = ref, ubrn = ubrn)
    val newRec = vatRepo.insert(vat)

    // Now see if we can query it back
    val fetched = vatRepo.getVat(refperiod, ref)

    fetched shouldBe (Some(newRec))

  }

  it should "insert new Ent/LEU/VAT then delete VAT correctly" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entRepo.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitRepo.insert(leu)

    // Now create VAT for this LEU
    val ref = "VAT0001"
    val vat: Vat = Vat(ref_period = refperiod, vatref = ref, ubrn = ubrn)
    val newRec = vatRepo.insert(vat)


    // delete new record
    newRec.destroy()

    // Now see if we can query it back  (should be gone)
    val fetched = vatRepo.getVat(refperiod, ref)

    fetched shouldBe (None)

  }

  it should "query right number of VAT records for a given Legal Unit" in {

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
      val ref = s"VAT$id"
      val vat: Vat = Vat(ref_period = refperiod, vatref = ref, ubrn = ubrn)
      val newRec = vatRepo.insert(vat)
    }

    // count the records for this legal unit
    val fetched = vatRepo.getVatsForLegalUnit(refperiod, ubrn)

    // Check we got the right number of records
    fetched.size shouldBe numUnits
  }


  it should "query specific VAT correctly from several records" in {
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
      val ref = s"VAT$id"
      val vat: Vat = Vat(ref_period = refperiod, vatref = ref, name1 = Some(s"VAT-NAME-$ref"), ubrn = ubrn)
      val newRec = vatRepo.insert(vat)
    }

    // query a random record back for an ID between 1 and num (watch out for 0 coming back from random gen)
    val r = scala.util.Random
    val qid: String = r.nextInt(numUnits) match {
      case 0 => s"VAT1"
      case n => s"VAT$n"
    }

    // fetch the data
    val fetched = vatRepo.getVat(refperiod, qid)

    val expectedName = Some(s"VAT-NAME-$qid")
    // Remember name is an option as well
    val actualName = fetched.map(_.name1).flatten

    actualName shouldBe expectedName
  }

  it should "count VAT records correctly" in {

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
      val ref = s"VAT$id"
      val vat: Vat = Vat(ref_period = refperiod, vatref = ref, name1 = Some(s"VAT-NAME-$ref"), ubrn = ubrn1)
      val newRec = vatRepo.insert(vat)
    }

    val ubrn2 = 10002L
    // create Legal Unit for this Enterprise
    val leu2: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn2, entref = entref, businessname = Option(s"Legal Unit $ubrn2"))
    val newLeu2 = unitRepo.insert(leu2)

    // create several records for this LEU
    val start = numUnits + 1
    val end = numUnits + numUnits
    (start to end) foreach { id =>
      val ref = s"VAT$id"
      val vat: Vat = Vat(ref_period = refperiod, vatref = ref, name1 = Some(s"VAT-NAME-$ref"), ubrn = ubrn2)
      val newRec = vatRepo.insert(vat)
    }

    // count ALL records i.e. includes both LEUs
    val expected = numUnits + numUnits
    val counted = vatRepo.count()

    counted shouldBe expected

  }
}
