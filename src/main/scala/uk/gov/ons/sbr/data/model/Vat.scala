/**
 * Vat.scala
 * --------------
 * Author: websc
 * Date: 14/09/17 13:24
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.model


import ai.x.play.json.Jsonx
import play.api.libs.json.{JsObject, JsValue, Json}
import scalikejdbc._

case class Vat(
                ref_period : Long,
                vatref : String,
                deathcode : Option[String] = None ,
                birthdate : Option[String] = None ,
                deathdate : Option[String] = None ,
                sic92 : Option[String] = None ,
                turnover : Option[String] = None ,
                turnover_date : Option[String] = None ,
                record_type : Option[String] = None ,
                status : Option[String] = None ,
                actiondate : Option[String] = None ,
                crn : Option[String] = None ,
                marker : Option[String] = None ,
                addressref : Option[String] = None ,
                inqcode : Option[String] = None ,
                name1 : Option[String] = None ,
                name2 : Option[String] = None ,
                name3 : Option[String] = None ,
                tradstyle1 : Option[String] = None ,
                tradstyle2 : Option[String] = None ,
                tradstyle3 : Option[String] = None ,
                address1 : Option[String] = None ,
                address2 : Option[String] = None ,
                address3 : Option[String] = None ,
                address4 : Option[String] = None ,
                address5 : Option[String] = None ,
                postcode : Option[String] = None ,
                mkr : Option[String] = None ,
                ubrn : Long)
{
  // Allows you to save current instance directly
  def save()(implicit session: DBSession = Vat.autoSession): Vat = Vat.save(this)(session)

  // deletes this instance from the database
  def destroy()(implicit session: DBSession = Vat.autoSession): Unit = Vat.destroy(this.ref_period, this.vatref)(session)

}

object Vat extends SQLSyntaxSupport[Vat] {

  // This bit will allow us to convert to/from JSON (need to use Jsonx for >22 fields)
  implicit val vatFormat = Jsonx.formatCaseClass[Vat]

  // This stuff is for converting to StatUnits
  val excludeFields: Seq[String] = List("vatref", "ubrn", "ref_period")

  def variablesToMap(obj: Vat): Map[String, String] = {
    // convert to JSON
    val objJson: JsValue = Json.toJson(obj)
    // now convert to Map of String -> JsValue
    val objMap: Map[String, JsValue] = objJson match {
      case JsObject(fields) => fields.toMap
      case _ => Map.empty[String, JsValue]
    }
    // convert to Map of String -> String and exclude key fields
    val varMap: Map[String, String] =
      for {
        (k, v) <- objMap
        if (!(excludeFields.contains(k)))
      } yield (k, v.as[String])
    varMap
  }
  // This is where the DB voodoo happens

  override val tableName = "vat_2500"

  def apply(e: SyntaxProvider[Vat])(rs: WrappedResultSet): Vat = apply(e.resultName)(rs)

  def apply(data: ResultName[Vat])(rs: WrappedResultSet): Vat = new Vat(
    rs.long(data.ref_period),
    rs.string(data.vatref),
    rs.stringOpt(data.deathcode),
    rs.stringOpt(data.birthdate),
    rs.stringOpt(data.deathdate),
    rs.stringOpt(data.sic92),
    rs.stringOpt(data.turnover),
    rs.stringOpt(data.turnover_date),
    rs.stringOpt(data.record_type),
    rs.stringOpt(data.status),
    rs.stringOpt(data.actiondate),
    rs.stringOpt(data.crn),
    rs.stringOpt(data.marker),
    rs.stringOpt(data.addressref),
    rs.stringOpt(data.inqcode),
    rs.stringOpt(data.name1),
    rs.stringOpt(data.name2),
    rs.stringOpt(data.name3),
    rs.stringOpt(data.tradstyle1),
    rs.stringOpt(data.tradstyle2),
    rs.stringOpt(data.tradstyle3),
    rs.stringOpt(data.address1),
    rs.stringOpt(data.address2),
    rs.stringOpt(data.address3),
    rs.stringOpt(data.address4),
    rs.stringOpt(data.address5),
    rs.stringOpt(data.postcode),
    rs.stringOpt(data.mkr),
    rs.long(data.ubrn)
  )

  // Seems to make it easier to use SQL DSL below
  val v = Vat.syntax("v")

  // Standard database operations ...


  def create(vat: Vat)(implicit session: DBSession = autoSession): Vat = {
    withSQL{
      insert.into(Vat).namedValues(

        column.ref_period -> vat.ref_period,
        column.vatref -> vat.vatref,
        column.deathcode -> vat.deathcode,
        column.birthdate -> vat.birthdate,
        column.deathdate -> vat.deathdate,
        column.sic92 -> vat.sic92,
        column.turnover -> vat.turnover,
        column.turnover_date -> vat.turnover_date,
        column.record_type -> vat.record_type,
        column.status -> vat.status,
        column.actiondate -> vat.actiondate,
        column.crn -> vat.crn,
        column.marker -> vat.marker,
        column.addressref -> vat.addressref,
        column.inqcode -> vat.inqcode,
        column.name1 -> vat.name1,
        column.name2 -> vat.name2,
        column.name3 -> vat.name3,
        column.tradstyle1 -> vat.tradstyle1,
        column.tradstyle2 -> vat.tradstyle2,
        column.tradstyle3 -> vat.tradstyle3,
        column.address1 -> vat.address1,
        column.address2 -> vat.address2,
        column.address3 -> vat.address3,
        column.address4 -> vat.address4,
        column.address5 -> vat.address5,
        column.postcode -> vat.postcode,
        column.mkr -> vat.mkr,
        column.ubrn -> vat.ubrn
      )
    }.update.apply()
    vat
  }


  def save(vat: Vat)(implicit session: DBSession = autoSession): Vat = {
    withSQL{
      update(Vat).set(

        column.deathcode -> vat.deathcode,
        column.birthdate -> vat.birthdate,
        column.deathdate -> vat.deathdate,
        column.sic92 -> vat.sic92,
        column.turnover -> vat.turnover,
        column.turnover_date -> vat.turnover_date,
        column.record_type -> vat.record_type,
        column.status -> vat.status,
        column.actiondate -> vat.actiondate,
        column.crn -> vat.crn,
        column.marker -> vat.marker,
        column.addressref -> vat.addressref,
        column.inqcode -> vat.inqcode,
        column.name1 -> vat.name1,
        column.name2 -> vat.name2,
        column.name3 -> vat.name3,
        column.tradstyle1 -> vat.tradstyle1,
        column.tradstyle2 -> vat.tradstyle2,
        column.tradstyle3 -> vat.tradstyle3,
        column.address1 -> vat.address1,
        column.address2 -> vat.address2,
        column.address3 -> vat.address3,
        column.address4 -> vat.address4,
        column.address5 -> vat.address5,
        column.postcode -> vat.postcode,
        column.mkr -> vat.mkr,
        column.ubrn -> vat.ubrn
      ).where
        .eq(column.ref_period, vat.ref_period).and.eq(column.vatref, vat.vatref)
    }.update.apply()
    vat
  }

  def find(ref_period: Long, vatref: String)(implicit session: DBSession = autoSession): Option[Vat] = withSQL {
    select.from(Vat as v)
      .where.eq(v.ref_period, ref_period)
      .and.eq(v.vatref, vatref)
  }.map(Vat(v)).single.apply()

  def findByLegalUnit(ref_period: Long, ubrn: Long)(implicit session: DBSession = autoSession): List[Vat] = withSQL {
    select.from(Vat as v)
      .where.eq(v.ref_period, ref_period)
      .and.eq(v.ubrn, ubrn)
  }.map(Vat(v)).list.apply()

  def destroy(ref_period: Long, vatref: String)(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(Vat as v)
      .where.eq(v.ref_period, ref_period)
      .and.eq(v.vatref, vatref)
  }.update.apply()

  def destroyAll()(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(Vat as v)
  }.update.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Vat as v)
  }.map(rs => rs.long(1)).single.apply().get

}

