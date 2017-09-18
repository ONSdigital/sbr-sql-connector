package uk.gov.ons.sbr.data.model

import play.api.libs.json._

case class StatUnitLinks(refPeriod: Long,
                         key: String,
                         unitType: String,
                         parents: Map[String, String] = Map.empty[String, String],
                         children: Map[String, String] = Map.empty[String, String],
                         childJsonString: String = "") {

}

object StatUnitLinks {
  // This bit will allow us to convert to/from JSON
  implicit val statUnitLinksWrites = Json.writes[StatUnitLinks]

  def apply(obj: UnitLinks): StatUnitLinks = {
    // Extract parents as key-value pairs
    val parents = Map[String, Long](
      (UnitType.ENT.toString -> obj.pEnt.getOrElse(-1)),
      (UnitType.LEU.toString -> obj.pLeu.getOrElse(-1))
    )
      .filter((v) => v._2 > 0)
      .map((v) => (v._1 -> v._2.toString))

    // Use our JSON-enabled Children class to handle this conversion
    val children: Children = Json.parse(obj.children.getOrElse("{}")).as[Children]

    // Build SUL
    StatUnitLinks(
      refPeriod = obj.ref_period,
      key = obj.unitId,
      unitType = obj.unitType,
      parents = parents,
      children = children.asMap(),
      childJsonString = obj.children.getOrElse(""))
  }
}
