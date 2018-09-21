/**
 * Children.scala
 * --------------
 * Author: websc
 * Date: 19/09/17 14:01
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.model

import play.api.libs.json.Json

// Every field is an Option so we can leave them out of the JSON/Maps
case class Children(leu: Option[Seq[String]] = None,
                    ch: Option[String] = None,
                    paye: Option[Seq[String]] = None,
                    vat: Option[Seq[String]] = None)
{

  def asMap[T](): Map[String, String] = {
    // WARNING: This mapping (ID->Unit Type) is required for compatibility with mid-tier API,
    // but it is NOT reliable because we could have conflicting IDs of different types.

    val ch: Option[(String, String)] = this.ch.map(ref => (ref ->UnitType.CH.toString))

    val leus: Seq[(String, String)] = this.leu match{
      case Some(xs: Seq[String]) => xs.map{ x => (x.toString -> UnitType.LEU.toString)}
      case _ => Nil}


    val payes: Seq[(String, String)] = this.paye match{
      case Some(xs: Seq[String]) => xs.map{ x => (x.toString -> UnitType.PAYE.toString)}
      case _ => Nil}

    val vats: Seq[(String, String)] = this.vat match{
      case Some(xs: Seq[String]) => xs.map{ x => (x.toString -> UnitType.VAT.toString)}
      case _ => Nil}

    val data: Map[String, String] = (leus ++ payes ++ vats).toMap[String, String]

    if (!ch.isEmpty) data + ch.get
    else data
  }

}

object Children {
  // This bit will allow us to convert to/from JSON as part of StatUnitLinks construction
  implicit val childrenWrites = Json.writes[Children]
  implicit val childrenReads = Json.reads[Children]

}
