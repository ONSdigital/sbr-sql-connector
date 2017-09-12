package uk.gov.ons.sbr.data.model

import org.scalatest._

class StatUnitTest extends FlatSpec with Matchers {

  /** * TESTS START HERE ***/

  behavior of "StatUnit"

  it should "convert Enterprise to StatUnit with variables as Map" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    val ent = Enterprise(
      ref_period = refperiod,
      entref = entref,
      ent_tradingstyle = Option(s"Entity $entref"),
      ent_address1 = Some("Address 1"),
      ent_address2 = Some("Address 2"),
      ent_address3 = Some("Address 3"),
      ent_address4 = Some("Address 4"),
      ent_address5 = Some("Address 5"),
      ent_postcode = Some("AB1 2CD"),
      legalstatus = Some("L"),
      paye_jobs = Some("PAYE 123"),
      employees = Some("Emps"),
      standard_vat_turnover = Some("VAT T/O"),
      num_unique_payerefs = Some("PAYEREFS"),
      num_unique_vatrefs = Some("VATREFS"),
      contained_rep_vat_turnover = Some("CRVT")
    )

    val su: StatUnit = StatUnit(ent)

    val expectedVarMap = Map(
      "ent_tradingstyle" -> s"Entity $entref",
      "ent_address1" -> "Address 1",
      "ent_address2" -> "Address 2",
      "ent_address3" -> "Address 3",
      "ent_address4" -> "Address 4",
      "ent_address5" -> "Address 5",
      "ent_postcode" -> "AB1 2CD",
      "legalstatus" -> "L",
      "paye_jobs" -> "PAYE 123",
      "employees" -> "Emps",
      "standard_vat_turnover" -> "VAT T/O",
      "num_unique_payerefs" -> "PAYEREFS",
      "num_unique_vatrefs" -> "VATREFS",
      "contained_rep_vat_turnover" -> "CRVT"
    )

    val actualMap = su.variables

    actualMap should contain theSameElementsAs (expectedVarMap)
  }



  it should "convert Enterprise correctly to StatUnit with correct main attributes" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    val ent = Enterprise(
      ref_period = refperiod,
      entref = entref,
      ent_tradingstyle = Option(s"Entity $entref"),
      ent_address1 = Some("Address 1"),
      ent_address2 = Some("Address 2"),
      ent_address3 = Some("Address 3"),
      ent_address4 = Some("Address 4"),
      ent_address5 = Some("Address 5"),
      ent_postcode = Some("AB1 2CD"),
      legalstatus = Some("L"),
      paye_jobs = Some("PAYE 123"),
      employees = Some("Emps"),
      standard_vat_turnover = Some("VAT T/O"),
      num_unique_payerefs = Some("PAYEREFS"),
      num_unique_vatrefs = Some("VATREFS"),
      contained_rep_vat_turnover = Some("CRVT")
    )

    val su: StatUnit = StatUnit(ent)

    assert(su.refPeriod == refperiod && su.key == entref.toString && su.unitType.toString == "ENT")
  }

  it should "convert LegalUnit correctly to StatUnit with variables as Map" in {

    val entref = 100L
    val ubrn = 1234L
    val refperiod = 201708 // not default period

    val leu = LegalUnit(refperiod, ubrn, Some(s"LEU $ubrn"), Some("AB1 2CD"), Some("I"),
      Some("L"), Some("T"), Some("T/O"), Some("EB"), entref)


    val su: StatUnit = StatUnit(leu)

    val expectedVarMap = Map("employmentbands" -> "EB", "legalstatus" -> "L", "businessname" -> "LEU 1234",
      "postcode" -> "AB1 2CD", "industrycode" -> "I", "tradingstatus" -> "T", "turnover" -> "T/O")

    val actualMap = su.variables

    actualMap should contain theSameElementsAs (expectedVarMap)
  }

  it should "convert LegalUnit correctly to StatUnit with correct main attributes" in {

    val entref = 100L
    val ubrn = 1234L
    val refperiod = 201708 // not default period

    val leu = LegalUnit(refperiod, ubrn, Some(s"LEU $ubrn"), Some("AB1 2CD"), Some("I"),
      Some("L"), Some("T"), Some("T/O"), Some("EB"), entref)

    val su: StatUnit = StatUnit(leu)

    assert(su.refPeriod == refperiod && su.key == ubrn.toString && su.unitType.toString == "LEU")
  }

}
