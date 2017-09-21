package uk.gov.ons.sbr.data.model

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import uk.gov.ons.sbr.data.db.{DbSchema, SbrDatabase}

class PayeDaoTest extends FlatSpec with DaoTest with Matchers {


  /** * TESTS START HERE ***/


  behavior of "PayeRepo"

  it should "insert new Enterprise, Legal Unit and PAYE, then query PAYE correctly with Ref Period" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)

    // Now create PAYE for this LEU
    val pref = "PAYE0001"
    val paye: Paye = Paye(ref_period = refperiod, payeref = pref, ubrn = ubrn)
    val newPaye = payeDao.insert(paye)

    // Now see if we can query it back
    val fetched = payeDao.getPaye(refperiod, pref)

    fetched shouldBe (Some(newPaye))

  }


  it should "insert new Ent/LEU/PAYE then delete PAYE correctly" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)

    // Now create PAYE for this LEU
    val pref = "PAYE0001"
    val paye: Paye = Paye(ref_period = refperiod, payeref = pref, ubrn = ubrn)
    val newPaye = payeDao.insert(paye)


    // delete new PAYE
    newPaye.destroy()

    // Now see if we can query it back  (should be gone)
    val fetched = payeDao.getPaye(refperiod, pref)

    fetched shouldBe (None)

  }

  it should "query right number of PAYE records for a given Legal Unit" in {

    val entref = 101L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 12345L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)

    // create several records for this LEU
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val pref = s"PAYE$id"
      val paye: Paye = Paye(ref_period = refperiod, payeref = pref, ubrn = ubrn)
      val newPaye = payeDao.insert(paye)
    }

    // count the records for this legal unit
    val fetched = payeDao.getPayesForLegalUnit(refperiod, ubrn)

    // Check we got the right number of records
    fetched.size shouldBe numUnits
  }

  it should "query specific PAYE correctly from several records" in {
    val entref = 101L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 12345L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)

    // create several records for this LEU
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val pref = s"PAYE$id"
      val paye: Paye = Paye(ref_period = refperiod, payeref = pref, name1 = Some(s"PAYE-NAME-$pref"),ubrn = ubrn)
      val newPaye = payeDao.insert(paye)
    }

    // query a random record back for an ID between 1 and num (watch out for 0 coming back from random gen)
    val r = scala.util.Random
    val qid: String = r.nextInt(numUnits) match {
      case 0 => s"PAYE1"
      case n => s"PAYE$n"
    }

    // fetch the data
    val fetched = payeDao.getPaye(refperiod, qid)

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
    val newEnt: Enterprise = entDao.insert(ent)

    // create two  Legal Units for this enterprise (UBRNs must be unique)

    val ubrn1 = 10001L
    // create Legal Unit for this Enterprise
    val leu1: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn1, entref = entref, businessname = Option(s"Legal Unit $ubrn1"))
    val newLeu1 = unitDao.insert(leu1)

    // create several records for this LEU
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val pref = s"PAYE$id"
      val paye: Paye = Paye(ref_period = refperiod, payeref = pref, ubrn = ubrn1)
      val newPaye = payeDao.insert(paye)
    }

    val ubrn2 = 10002L
    // create Legal Unit for this Enterprise
    val leu2: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn2, entref = entref, businessname = Option(s"Legal Unit $ubrn2"))
    val newLeu2 = unitDao.insert(leu2)

    // create several records for this LEU
    val start = numUnits + 1
    val end = numUnits + numUnits
    (start to end) foreach { id =>
      val pref = s"PAYE$id"
      val paye: Paye = Paye(ref_period = refperiod, payeref = pref, ubrn = ubrn2)
      val newPaye = payeDao.insert(paye)
    }

    // count ALL records i.e. includes both LEUs
    val expected = numUnits + numUnits
    val counted = payeDao.count()

    counted shouldBe expected

  }
}