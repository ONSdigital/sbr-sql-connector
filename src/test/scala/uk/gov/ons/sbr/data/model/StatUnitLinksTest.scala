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
    val children = """[{"PAYE":"PAYE123", "VAT":"VAT123"}]"""
    val ul = UnitLinks(refperiod, ut, s"$cono", Option(entref), Option(ubrn), Option(children) )

    // expected children
    val expected = Map("PAYE123" -> "PAYE", "VAT123" -> "VAT")

    // actual children
    val sul = StatUnitLinks(ul)

    sul.children should contain theSameElementsAs (expected)

  }

}
