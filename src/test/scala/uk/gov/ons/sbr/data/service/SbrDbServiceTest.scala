package uk.gov.ons.sbr.data.service

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json
import uk.gov.ons.sbr.data.model._

class SbrDbServiceTest extends FlatSpec with DaoTest with Matchers {

  behavior of "SbrDbServiceTest"



  it should "insert new Ent and Legal Unit with Company/PAYE/VAT, and query LEU correctly as Stat Unit hierarchy" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)

    // Now create Company for this LEU
    val coNo = "COMPANY0001"
    val co: Company = Company(ref_period = refperiod, companynumber = coNo, companyname = Some(s"Test company $coNo"), ubrn = ubrn)
    val newCo = chDao.insert(co)

    val leuSU: StatUnit = StatUnit(leu)
    val coSU = StatUnit(co)

    // Now create PAYE for this LEU
    val pref = "PAYE0001"
    val paye: Paye = Paye(ref_period = refperiod, payeref = pref, ubrn = ubrn, name1 = Some(s"NAME1 for $pref"))
    val newPaye = payeDao.insert(paye)
    val payeSU = StatUnit(paye)


    // Now create VAT for this LEU
    val vref = "VAT0001"
    val vat: Vat = Vat(ref_period = refperiod, vatref = vref, ubrn = ubrn, name1 = Some(s"NAME1 for $vref"))
    val newRec = vatDao.insert(vat)
    val vatSU = StatUnit(vat)

    // Set LEU children
    val children = Seq(coSU) ++ Seq(payeSU) ++ Seq(vatSU)
    leuSU.children = children

    // Now see if we can query it back via Enterprise key
    val fetched = dbService.getLegalUnitAsStatUnit(refperiod, ubrn)

    fetched shouldBe (Some(leuSU))
  }


  it should "insert new Ent and Legal Unit with Company/PAYE/VAT, and query Ent correctly as Stat Units" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)
    val entSU: StatUnit = StatUnit(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)
    val leuSU: StatUnit = StatUnit(leu)

    // Now create Company for this LEU
    val coNo = "COMPANY0001"
    val co: Company = Company(ref_period = refperiod, companynumber = coNo, companyname = Some(s"Test company $coNo"), ubrn = ubrn)
    val newCo = chDao.insert(co)
    val coSU = StatUnit(co)

    // Now create PAYE for this LEU
    val pref = "PAYE0001"
    val paye: Paye = Paye(ref_period = refperiod, payeref = pref, ubrn = ubrn, name1 = Some(s"NAME1 for $pref"))
    val newPaye = payeDao.insert(paye)
    val payeSU = StatUnit(paye)

    // Now create VAT for this LEU
    val vref = "VAT0001"
    val vat: Vat = Vat(ref_period = refperiod, vatref = vref, ubrn = ubrn, name1 = Some(s"NAME1 for $vref"))
    val newRec = vatDao.insert(vat)
    val vatSU = StatUnit(vat)

    // Set LEU children
    val children = Seq(coSU) ++ Seq(payeSU) ++ Seq(vatSU)
    leuSU.children = children

    // Add LEU SU to ENTERPRISE SU children
    entSU.children = List(leuSU) ++ entSU.children

    // Now see if we can query it back via Enterprise key
    val fetched = dbService.getEnterpriseAsStatUnit(refperiod, entref)

    fetched shouldBe (Some(entSU))
  }


  it should "insert new Ent and Legal Unit with Company, and query Company correctly as Stat Unit" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)

    // Now create Company for this LEU
    val coNo = "COMPANY0001"
    val co: Company = Company(ref_period = refperiod, companynumber = coNo, companyname = Some(s"Test company $coNo"), ubrn = ubrn)
    val newCo = chDao.insert(co)
    val coSU = StatUnit(co)

    // Now see if we can query it back via key
    val fetched = dbService.getCompanyAsStatUnit(refperiod, coNo)

    fetched shouldBe (Some(coSU))
  }

  it should "insert new Ent and Legal Unit with PAYE, and query PAYE correctly as Stat Units" in {

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
    val paye: Paye = Paye(ref_period = refperiod, payeref = pref, ubrn = ubrn, name1 = Some(s"NAME1 for $pref"))
    val newPaye = payeDao.insert(paye)
    val payeSU = StatUnit(paye)

    // Now see if we can query it back via key
    val fetched = dbService.getPayeAsStatUnit(refperiod, pref)

    fetched shouldBe (Some(payeSU))
  }

  it should "insert new Ent and Legal Unit with VAT, and query VAT correctly as Stat Units" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)

    // create Legal Unit for this Enterprise
    val ubrn = 1234L
    val leu: LegalUnit = LegalUnit(ref_period = refperiod, ubrn = ubrn, entref = entref, businessname = Option(s"Legal Unit $ubrn"))
    val newLeu = unitDao.insert(leu)

    // Now create VAT for this LEU
    val vref = "VAT0001"
    val vat: Vat = Vat(ref_period = refperiod, vatref = vref, ubrn = ubrn, name1 = Some(s"NAME1 for $vref"))
    val newRec = vatDao.insert(vat)
    val vatSU = StatUnit(vat)

    // Now see if we can query it back via key
    val fetched = dbService.getVatAsStatUnit(refperiod, vref)

    fetched shouldBe (Some(vatSU))
  }


  it should "insert new Ent with default Ref Period and query Ent correctly without Ref Period" in {

    val entref = 100L
    val refperiod = dbService.defaultRefPeriod

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)
    val entSU = StatUnit(ent)

    // Now see if we can query it back via key
    val fetched = dbService.getEnterpriseAsStatUnit(entref)

    fetched shouldBe (Some(entSU))
  }

  it should "insert new Ent with non-default Ref Period and query Ent correctly without Ref Period" in {

    val entref = 100L
    val refperiod = dbService.defaultRefPeriod + 1

    // create parent Enterprise first (FK)
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    entDao.insert(ent)

    // Now see if we can query it back via key
    val fetched: Option[StatUnit] = dbService.getEnterpriseAsStatUnit(entref)

    fetched shouldBe None
  }

  // UnitLinks is query-only in the Alpha

  it should "insert a Unit Links and return data for Ref Period + ID" in {

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
    } yield linksDao.insert(rec)

    // Pick an ID at random
    val r = scala.util.Random
    val searchId : String = r.nextInt(numUnits) match {
      case 0 => s"1"
      case n => s"$n"
    }

    // Query the records back
    val results = dbService.getUnitLinks(refperiod, searchId)

    val expected = uts.map{ut =>  UnitLinks(refperiod, ut, searchId)}

    // Check the results are correct
    results should contain theSameElementsAs expected

  }

  it should "insert Unit Links with default Ref Period and return data for ID" in {

    val refperiod = dbService.defaultRefPeriod

    // create several records for each Unit type
    val uts = List("ENT","LEU","CH","PAYE","VAT")

    val numUnits = 10
    val ids = (1 to numUnits)
    val dummy = for {
      id <- ids
      ut <- uts
      key = s"$id"
      rec = UnitLinks(refperiod, ut, key, None, None, None)
    } yield linksDao.insert(rec)

    // Pick an ID at random
    val r = scala.util.Random
    val searchId : String = r.nextInt(numUnits) match {
      case 0 => s"1"
      case n => s"$n"
    }

    // Query the records back without providing Ref Period
    val results = dbService.getUnitLinks(searchId)

    val expected = uts.map{ut =>  UnitLinks(refperiod, ut, searchId)}

    // Check the results are correct
    results should contain theSameElementsAs expected

  }

  it should "insert Unit Links with non-default Ref Period and return no data for ID" in {

    val refperiod = dbService.defaultRefPeriod + 1

    // create several records for each Unit type
    val uts = List("ENT","LEU","CH","PAYE","VAT")

    val numUnits = 10
    val ids = (1 to numUnits)
    val dummy = for {
      id <- ids
      ut <- uts
      key = s"$id"
      rec = UnitLinks(refperiod, ut, key, None, None, None)
    } yield linksDao.insert(rec)

    // Pick an ID at random
    val r = scala.util.Random
    val searchId : String = r.nextInt(numUnits) match {
      case 0 => s"1"
      case n => s"$n"
    }

    // Query the records back without providing Ref Period
    val results: Seq[UnitLinks] = dbService.getUnitLinks(searchId)

    val expected = Nil

    // Check the results are correct
    results shouldBe expected

  }
}
