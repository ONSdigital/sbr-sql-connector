package uk.gov.ons.sbr.data.model

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

class ChildrenTest extends FlatSpec with Matchers{

  behavior of "Children"

  // build a dummy Children object (not looking at DB here)
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

  it should "construct JSON correctly when every field is populated" in {

    // Construct Children
    val kidz = Children(Option(List(ubrn.toString)), Some(coNo), Some(payes), Some(vats))

    val expected = Json.parse("""{"leu":["1234"],"ch":"COMPANY0001","paye":["PAYE0001","PAYE0002"],"vat":["VAT0001","VAT0002"]}""")

    Json.toJson(kidz) shouldBe expected
  }

  it should "parse JSON correctly to Children object when every field is populated" in {
    // Make the Children object
    val expected = Children(Option(List(ubrn.toString)), Some(coNo), Some(payes), Some(vats))

    val jsonStr = Json.parse("""{"leu":["1234"],"ch":"COMPANY0001","paye":["PAYE0001","PAYE0002"],"vat":["VAT0001","VAT0002"]}""")

    val result: Children = jsonStr.as[Children]

    result shouldBe expected
  }


  it should "parse JSON correctly to Children object when only LEU is populated" in {

   // Construct Children
    val expected = Children(Option(List(ubrn.toString)), None, None, None)

    val jsonStr = Json.parse("""{"leu":["1234"]}""")

    val result: Children = jsonStr.as[Children]

    result shouldBe expected
  }


  it should "construct JSON correctly when LEU is NOT populated" in {

    // Construct Children
    val kidz = Children(None, Some(coNo), Some(payes), Some(vats))

    val expected = Json.parse("""{"ch":"COMPANY0001","paye":["PAYE0001","PAYE0002"],"vat":["VAT0001","VAT0002"]}""")

    Json.toJson(kidz) shouldBe expected
  }

  it should "construct JSON correctly when LEU and CH is NOT populated" in {

     // Construct Children
    val kidz = Children(None, None, Some(payes), Some(vats))

    val expected = Json.parse("""{"paye":["PAYE0001","PAYE0002"],"vat":["VAT0001","VAT0002"]}""")

    Json.toJson(kidz) shouldBe expected
  }

  it should "construct JSON correctly when LEU and CH, PAYE are NOT populated" in {

    // Construct Children
    val kidz = Children(None, None, None, Some(vats))

    val expected = Json.parse("""{"vat":["VAT0001","VAT0002"]}""")

    Json.toJson(kidz) shouldBe expected
  }

  it should "construct JSON correctly when LEU and CH, VAT are NOT populated" in {

    // Construct Children
    val kidz = Children(None, None, Some(payes), None)

    val expected = Json.parse("""{"paye":["PAYE0001","PAYE0002"]}""")

    Json.toJson(kidz) shouldBe expected
  }

  it should "construct JSON correctly when LEU, CH, PAYE, VAT are NOT populated" in {

    // Construct Children
    val kidz = Children(None, None, None, None)

    val expected = Json.parse("""{}""")

    Json.toJson(kidz) shouldBe expected
  }

  it should "construct JSON correctly when LEU and PAYE is NOT populated" in {

    // Construct Children
    val kidz = Children(None, Some(coNo), None, Some(vats))

    val expected = Json.parse("""{"ch":"COMPANY0001","vat":["VAT0001","VAT0002"]}""")

    Json.toJson(kidz) shouldBe expected
  }

  it should "construct JSON correctly when LEU and VAT is NOT populated" in {

    // Construct Children
    val kidz = Children(None, Some(coNo), Some(payes), None)

    val expected = Json.parse("""{"ch":"COMPANY0001","paye":["PAYE0001","PAYE0002"]}""")

    Json.toJson(kidz) shouldBe expected
  }

  it should "construct JSON correctly when LEU, PAYE and VAT are NOT populated" in {

     // Construct Children
    val kidz = Children(None, Some(coNo), None, None)

    val expected = Json.parse("""{"ch":"COMPANY0001"}""")

    Json.toJson(kidz) shouldBe expected
  }

  it should "convert full set of data to Map correctly via asMap()" in {
     // Make the Children object
    val kidz = Children(Option(List(ubrn.toString)), Some(coNo), Some(payes), Some(vats))

    val expected: Map[String, String] =  Map(
      "1234" -> "LEU",
      "COMPANY0001" -> "CH",
      "PAYE0002" -> "PAYE",
      "PAYE0001" -> "PAYE",
      "VAT0001" -> "VAT",
      "VAT0002" -> "VAT"
    )

    kidz.asMap() should contain theSameElementsAs (expected)
  }

}
