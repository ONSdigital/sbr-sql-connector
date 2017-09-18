package uk.gov.ons.sbr.data.model

import play.api.libs.json._

import scala.util.{Failure, Success, Try}

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


  def parseChildrenStrToMap(jsonStrOpt: Option[String]): Map[String, String] = {

    // Current web app API requires map like this:
    // [(123->PAYE),(456->PAYE),(789->VAT)...]
    // This is because we can have >1 PAYE ref in children.
    // This model will break with duplicate IDs.

    val combined: List[(String, JsValue)] =
      jsonStrOpt match {
        case Some(s: String) =>
          Try {
            // convert from string to JSON array
            val elems = Json.parse(s).as[Seq[JsValue]]
            // convert each elem in array to a separate map
            val mapStrJson = elems.map {
              case JsObject(fields) => fields.toMap
              case _ => List.empty [(String, JsValue)]
            }
            mapStrJson
          }
          match {
              // combine in
            case Success(ms) => ms.foldLeft(List.empty[(String, JsValue)])(_ ++ _)
            case Failure(x) => List.empty[(String, JsValue)]
          }

        case None => List.empty[(String, JsValue)]
      }
    // switch key-value around and return JsValue as String
    val output: Seq[(String, String)] = for {(k, v) <- combined
                                          vs = v.as[String]
    } yield (vs -> k)

    output.toMap
  }

  def apply(obj: UnitLinks): StatUnitLinks = {
    // Extract parents as key-value pairs
    val parents = Map[String, Long](
      (UnitType.ENT.toString -> obj.pEnt.getOrElse(-1)),
      (UnitType.LEU.toString -> obj.pLeu.getOrElse(-1))
    )
      .filter((v) => v._2 > 0)
      .map((v) => (v._1 -> v._2.toString))

    val children: Map[String, String] = parseChildrenStrToMap(obj.children)

    // Build SUL
    StatUnitLinks(
      refPeriod = obj.ref_period,
      key = obj.unitId,
      unitType = obj.unitType, //UnitType.withName(obj.unitType),
      parents = parents,
      children = children,
      childJsonString = obj.children.getOrElse(""))
  }
}
