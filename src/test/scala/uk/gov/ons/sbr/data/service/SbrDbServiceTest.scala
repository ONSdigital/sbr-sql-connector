package uk.gov.ons.sbr.data.service

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json
import uk.gov.ons.sbr.data.model._

class SbrDbServiceTest extends FlatSpec with DaoTest with Matchers {

  behavior of "SbrDbServiceTest"

  // ==== set up

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

  // ==== end of set up

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


}
