package uk.gov.ons.sbr.data.model

import org.scalatest.{FlatSpec, Matchers}
import uk.gov.ons.sbr.data.model.UnitType._

class LeuChildrenTest extends FlatSpec with Matchers{

  behavior of "LeuChildrenTest"

  it should "asMap" in {

    // build a dummy LEU Children object (not looking at DB here)
    val entref = 100L
    val refperiod = 201708 // not default period
    val ubrn = 1234L

    // need a Company
    val coNo = "COMPANY0001"
    // need some PAYEs
    val pref1 = "PAYE0001"
    val pref2 = "PAYE0002"
    val payes = List(pref1, pref2)
    // need some VATs
    val vref1 = "VAT0001"
    val vref2 = "VAT0002"
    val vats = List(vref1, vref2)

    // Make the LeuCHildren object
    val kidz = LeuChildren(refperiod, ubrn, Some(coNo), payes, vats)

    val expected: Map[String, UnitType] = Map("COMPANY0001" -> CH,
                                              "PAYE0002" -> PAYE, "PAYE0001" -> PAYE,
                                              "VAT0001" -> VAT,  "VAT0002" -> VAT)

    kidz.asMap() should contain theSameElementsAs (expected)
  }

}
