package uk.gov.ons.sbr.data.model

import play.api.libs.json.Json

// Every field is an Option so we can leave them out of the JSON/Maps
case class Children(   legalunits: Option[Seq[String]] = None,
                       ch: Option[String] = None,
                       paye: Option[Seq[String]] = None,
                       vat: Option[Seq[String]] = None)
{

  def asMap(): Map[String, String] = {
    // WARNING: This mapping (ID->Unit Type) is required for compatibility with mid-tier API,
    // but it is NOT reliable because we could have conflicting IDs of different types.

    val ch = this.ch.map(ref => (ref ->UnitType.CH.toString))

    val leus= this.legalunits match{
      case Some(xs: Seq[String]) => xs.map{ x => (x -> UnitType.LEU.toString)}
      case _ => Nil}


    val payes = this.paye match{
      case Some(xs: Seq[String]) => xs.map{ x => (x -> UnitType.PAYE.toString)}
      case _ => Nil}

    val vats = this.vat match{
      case Some(xs: Seq[String]) => xs.map{ x => (x -> UnitType.VAT.toString)}
      case _ => Nil}

    val data = (leus ++ payes ++ vats).toMap[String, String]

    if (!ch.isEmpty) data + ch.get
    else data
  }

}

object Children {
  // This bit will allow us to convert to/from JSON
  implicit val childrenWrites = Json.writes[Children]
  implicit val childrenReads = Json.reads[Children]

}