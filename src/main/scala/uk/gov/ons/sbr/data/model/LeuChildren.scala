package uk.gov.ons.sbr.data.model

import play.api.libs.json.Json
import uk.gov.ons.sbr.data.model
import uk.gov.ons.sbr.data.model.UnitType._


case class LeuChildren(ref_period: Long,
                       ubrn: Long,
                       companyNo: Option[String],
                       payeRefs: List[String] = Nil,
                       vatRefs: List[String] = Nil)
{
  def asMap(): Map[String, UnitType] = {
    // WARNING: This mapping is required for comatibility with the mid-tier API,
    // but it is NOT reliable because we could have comflicting IDs of different types.

    val ch: Option[(String, model.UnitType.Value)] = this.companyNo.map(ref => (ref ->UnitType.CH))
    val payes = this.payeRefs.map{ p => (p -> UnitType.PAYE)}.toMap
    val vats = this.vatRefs.map{ v => (v -> UnitType.VAT)}.toMap

    val data: Map[String, UnitType] = payes ++ vats

    if (!ch.isEmpty) data + ch.get
    else data

  }
}

object LeuChildren {
  // This bit will allow us to convert to/from JSON
  implicit val leuChildrenWrites = Json.writes[LeuChildren]

}