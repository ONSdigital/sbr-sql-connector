/**
 * LeuChildren.scala
 * --------------
 * Author: websc
 * Date: 19/09/17 14:01
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.model

import play.api.libs.json.Json


case class LeuChildren(ref_period: Long,
                       ubrn: Long,
                       companyNo: Option[String],
                       payeRefs: List[String] = Nil,
                       vatRefs: List[String] = Nil)
{
  def asMap(): Map[String, String] = {
    // WARNING: This mapping is required for compatibility with the mid-tier API,
    // but it is NOT reliable because we could have conflicting IDs of different types.

    val ch = this.companyNo.map(ref => (ref ->UnitType.CH.toString))

    val payes = this.payeRefs.map{ p => (p -> UnitType.PAYE.toString)}.toMap
    val vats = this.vatRefs.map{ v => (v -> UnitType.VAT.toString)}.toMap

    val data: Map[String, String] = payes ++ vats

    if (!ch.isEmpty) data + ch.get
    else data

  }
}

object LeuChildren {
  // This bit will allow us to convert to/from JSON
  implicit val leuChildrenWrites = Json.writes[LeuChildren]

}
