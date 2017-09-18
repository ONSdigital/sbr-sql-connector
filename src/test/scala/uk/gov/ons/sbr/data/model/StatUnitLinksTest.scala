package uk.gov.ons.sbr.data.model

import org.scalatest.{FlatSpec, Matchers}

class StatUnitLinksTest extends FlatSpec with Matchers{

  /** * TESTS START HERE ***/

  behavior of "StatUnitLinks"

  it should "convert UnitLinks parents correctly to a Map" in {

    val refperiod = 201708 // not default period
    val ut = "CH"
    val entref = 1000L
    val ubrn = 2000L
    val cono = "COMPANY123"
    val children = """[{"PAYE":"PAYE123", "VAT":"VAT123"}]"""
    val ul = UnitLinks(refperiod, ut, s"$cono", Option(entref), Option(ubrn), Option(children) )

    // expected parents
    val expected = Map((UnitType.ENT.toString -> entref.toString),(UnitType.LEU.toString -> ubrn.toString))

    // actual parents
    val sul = StatUnitLinks(ul)

    sul.parents should contain theSameElementsAs (expected)

  }

  it should "convert UnitLinks children correctly to a Map" in {

    val refperiod = 201708 // not default period
    val ut = "CH"
    val entref = 1000L
    val ubrn = 2000L
    val cono = "COMPANY123"
    val children = """{"paye":["PAYE0001","PAYE0002"],"vat":["VAT0001","VAT0002"]}"""
    val ul = UnitLinks(refperiod, ut, s"$cono", Option(entref), Option(ubrn), Option(children) )

    // expected children - (unit ID -> unit Type) to allow for multiple PAYEs etc.
    val expected = Map("PAYE0001" -> "PAYE", "PAYE0002" -> "PAYE", "VAT0001" -> "VAT", "VAT0002" -> "VAT")

    // actual children
    val sul = StatUnitLinks(ul)

    sul.children should contain theSameElementsAs (expected)

  }

}
