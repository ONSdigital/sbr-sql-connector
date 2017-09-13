package uk.gov.ons.sbr.data.model

import uk.gov.ons.sbr.data.model.UnitType.UnitType

case class StatUnitLinks(refPeriod: Long,
                         key: String,
                         unitType: String,
                         parents: Map[UnitType, String] = Map.empty[UnitType, String],
                         children: Map[String, String] = Map.empty[String, String],
                         childJsonString: String = "") {

}

object StatUnitLinks {

  def apply(obj: UnitLinks): StatUnitLinks = {
    // Extract parents as key-value pairs
    val parents = Map[UnitType, Long](
        (UnitType.ENT -> obj.pEnt.getOrElse(-1)),
        (UnitType.LEU -> obj.pLeu.getOrElse(-1))
      )
      .filter((v) => v._2 > 0)
      .map((v) => (v._1 -> v._2.toString))

    // Build SUL
    StatUnitLinks(
      refPeriod = obj.ref_period,
      key = obj.unitId,
      unitType = obj.unitType, //UnitType.withName(obj.unitType),
      parents = parents,
      childJsonString = obj.children.getOrElse(""))
  }
}
