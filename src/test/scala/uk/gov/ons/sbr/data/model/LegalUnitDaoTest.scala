package uk.gov.ons.sbr.data.model

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import uk.gov.ons.sbr.data.db.{DbSchema, SbrDatabase}

class LegalUnitDaoTest extends FlatSpec with DaoTest with Matchers {
  
  /** * TESTS START HERE ***/

  behavior of "LegalUnitRepo"

  it should "insert new Enterprise and Legal Unit, and query Legal Unit correctly with Ref Period" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)

    // Now see if we can query it back
    val fetched = unitDao.getLegalUnit(refperiod, ubrn)

    fetched shouldBe (Some(newLeu))
  }

  it should "insert new Ent/LEU and delete Legal Unit correctly" in {

    val entref = 101L
    val refperiod = 201706

    // assume insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val newEnt: Enterprise = entDao.insert(ent)


    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)

    // delete new Legal Unit record
    newLeu.destroy()

    // see if Legal Unit still exists (query works - tested elsewhere)
    val fetched = unitDao.getLegalUnit(refperiod, ubrn)

    fetched shouldBe (None)

  }

  it should "query specific Legal Unit correctly from several records" in {

    val entref = 101L
    val refperiod = 201706

    // assume Enterprise insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val newEnt: Enterprise = entDao.insert(ent)

    // create several Legal Units

    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = id, entref = entref, businessname = Option(s"Legal Unit $id"))
      unitDao.insert(leu)
    }

    // query a random record back for an ID between 1 and numEnts (watch out for 0 coming back from random gen)
    val r = scala.util.Random
    val qid: Long = r.nextInt(numUnits) match {
      case 0 => 1
      case n => n
    }

    // fetch the data
    val fetched = unitDao.getLegalUnit(refperiod, qid)

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
    val newEnt: Enterprise = entDao.insert(ent)

    // create several Legal Units
    val numUnits = 10
    val ids = (1 to numUnits)
    ids foreach { id =>
      val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = id, entref = entref, businessname = Option(s"Legal Unit $id"))
      unitDao.insert(leu)
    }

    // fetch the legal units for this enterprise
    val fetched = unitDao.getLegalUnitsForEnterprise(refperiod, entref)

    // Check we got the right number of records
    fetched.size shouldBe numUnits
  }

  it should "count Legal Unit records correctly" in {

    val entref = 101L
    val refperiod = 201706
    val numUnits = 10

    // assume Enterprise insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val newEnt: Enterprise = entDao.insert(ent)

    // create several Legal Units for this enterprise
    (1 to numUnits) foreach { id =>
      val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = id, entref = entref, businessname = Option(s"Legal Unit $id"))
      unitDao.insert(leu)
    }

    // now add another Ent and same number of Legal Units
    val entref2 = entref + 1
    val ent2 = Enterprise(ref_period = refperiod, entref = entref2, ent_tradingstyle = Option(s"Entity $entref2"))
    entDao.insert(ent2)

    // create several Legal Units for this enterprise (UBRNs must be unique)
    val start = numUnits + 1
    val end = numUnits + numUnits
    (start to end) foreach { id =>
      val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = id, entref = entref, businessname = Option(s"Legal Unit $id"))
      unitDao.insert(leu)
    }

    // count ALL legal units
    val expected = numUnits + numUnits
    val counted = unitDao.count()

    counted shouldBe expected

  }
}
