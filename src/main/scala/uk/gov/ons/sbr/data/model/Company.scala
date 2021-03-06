/**
 * Company.scala
 * --------------
 * Author: websc
 * Date: 19/09/17 14:01
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.model

import play.api.libs.json.{JsObject, JsValue, Json, OWrites}
import scalikejdbc._

// for JSON with >22 case class fields
import ai.x.play.json.Jsonx

case class Company(
                    ref_period: Long,
                    companynumber: String,
                    companyname: Option[String] = None,
                    regaddress_careof: Option[String] = None,
                    regaddress_pobox: Option[String] = None,
                    regaddress_addressline1: Option[String] = None,
                    regaddress_addressline2: Option[String] = None,
                    regaddress_posttown: Option[String] = None,
                    regaddress_county: Option[String] = None,
                    regaddress_country: Option[String] = None,
                    regaddress_postcode: Option[String] = None,
                    companycategory: Option[String] = None,
                    companystatus: Option[String] = None,
                    countryoforigin: Option[String] = None,
                    dissolutiondate: Option[String] = None,
                    incorporationdate: Option[String] = None,
                    accounts_accountrefday: Option[String] = None,
                    accounts_accountrefmonth: Option[String] = None,
                    accounts_nextduedate: Option[String] = None,
                    accounts_lastmadeupdate: Option[String] = None,
                    accounts_accountcategory: Option[String] = None,
                    returns_nextduedate: Option[String] = None,
                    returns_lastmadeupdate: Option[String] = None,
                    mortgages_nummortcharges: Option[String] = None,
                    mortgages_nummortoutstanding: Option[String] = None,
                    mortgages_nummortpartsatisfied: Option[String] = None,
                    mortgages_nummortsatisfied: Option[String] = None,
                    siccode_sictext_1: Option[String] = None,
                    siccode_sictext_2: Option[String] = None,
                    siccode_sictext_3: Option[String] = None,
                    siccode_sictext_4: Option[String] = None,
                    limitedpartnerships_numgenpartners: Option[String] = None,
                    limitedpartnerships_numlimpartners: Option[String] = None,
                    uri: Option[String] = None,
                    previousname_1_condate: Option[String] = None,
                    previousname_1_companyname: Option[String] = None,
                    previousname_2_condate: Option[String] = None,
                    previousname_2_companyname: Option[String] = None,
                    previousname_3_condate: Option[String] = None,
                    previousname_3_companyname: Option[String] = None,
                    previousname_4_condate: Option[String] = None,
                    previousname_4_companyname: Option[String] = None,
                    previousname_5_condate: Option[String] = None,
                    previousname_5_companyname: Option[String] = None,
                    previousname_6_condate: Option[String] = None,
                    previousname_6_companyname: Option[String] = None,
                    previousname_7_condate: Option[String] = None,
                    previousname_7_companyname: Option[String] = None,
                    previousname_8_condate: Option[String] = None,
                    previousname_8_companyname: Option[String] = None,
                    previousname_9_condate: Option[String] = None,
                    previousname_9_companyname: Option[String] = None,
                    previousname_10_condate: Option[String] = None,
                    previousname_10_companyname: Option[String] = None,
                    confstmtnextduedate: Option[String] = None,
                    confstmtlastmadeupdate: Option[String] = None,
                    ubrn: Long
                  )
{
  // Allows you to save current instance directly
  def save()(implicit session: DBSession = LegalUnit.autoSession): Company = Company.save(this)(session)

  // deletes this instance from the database
  def destroy()(implicit session: DBSession = Company.autoSession): Unit =
    Company.destroy(this.ref_period, this.companynumber)(session)

}

object Company extends SQLSyntaxSupport[Company] {

  // This bit will allow us to convert to/from JSON (need to use Jsonx for >22 fields)
  implicit val companyFormat = Jsonx.formatCaseClass[Company]

  // This stuff is for converting to StatUnits
  val excludeFields: Seq[String] = List("companynumber", "ubrn", "ref_period")

  def variablesToMap(obj: Company): Map[String, String] = {
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

  override val tableName = "ch_2500"

  def apply(e: SyntaxProvider[Company])(rs: WrappedResultSet): Company = apply(e.resultName)(rs)

  def apply(c: ResultName[Company])(rs: WrappedResultSet): Company = new Company(
    rs.long(c.ref_period),
    rs.string(c.companynumber),
    rs.stringOpt(c.companyname),
    rs.stringOpt(c.regaddress_careof),
    rs.stringOpt(c.regaddress_pobox),
    rs.stringOpt(c.regaddress_addressline1),
    rs.stringOpt(c.regaddress_addressline2),
    rs.stringOpt(c.regaddress_posttown),
    rs.stringOpt(c.regaddress_county),
    rs.stringOpt(c.regaddress_country),
    rs.stringOpt(c.regaddress_postcode),
    rs.stringOpt(c.companycategory),
    rs.stringOpt(c.companystatus),
    rs.stringOpt(c.countryoforigin),
    rs.stringOpt(c.dissolutiondate),
    rs.stringOpt(c.incorporationdate),
    rs.stringOpt(c.accounts_accountrefday),
    rs.stringOpt(c.accounts_accountrefmonth),
    rs.stringOpt(c.accounts_nextduedate),
    rs.stringOpt(c.accounts_lastmadeupdate),
    rs.stringOpt(c.accounts_accountcategory),
    rs.stringOpt(c.returns_nextduedate),
    rs.stringOpt(c.returns_lastmadeupdate),
    rs.stringOpt(c.mortgages_nummortcharges),
    rs.stringOpt(c.mortgages_nummortoutstanding),
    rs.stringOpt(c.mortgages_nummortpartsatisfied),
    rs.stringOpt(c.mortgages_nummortsatisfied),
    rs.stringOpt(c.siccode_sictext_1),
    rs.stringOpt(c.siccode_sictext_2),
    rs.stringOpt(c.siccode_sictext_3),
    rs.stringOpt(c.siccode_sictext_4),
    rs.stringOpt(c.limitedpartnerships_numgenpartners),
    rs.stringOpt(c.limitedpartnerships_numlimpartners),
    rs.stringOpt(c.uri),
    rs.stringOpt(c.previousname_1_condate),
    rs.stringOpt(c.previousname_1_companyname),
    rs.stringOpt(c.previousname_2_condate),
    rs.stringOpt(c.previousname_2_companyname),
    rs.stringOpt(c.previousname_3_condate),
    rs.stringOpt(c.previousname_3_companyname),
    rs.stringOpt(c.previousname_4_condate),
    rs.stringOpt(c.previousname_4_companyname),
    rs.stringOpt(c.previousname_5_condate),
    rs.stringOpt(c.previousname_5_companyname),
    rs.stringOpt(c.previousname_6_condate),
    rs.stringOpt(c.previousname_6_companyname),
    rs.stringOpt(c.previousname_7_condate),
    rs.stringOpt(c.previousname_7_companyname),
    rs.stringOpt(c.previousname_8_condate),
    rs.stringOpt(c.previousname_8_companyname),
    rs.stringOpt(c.previousname_9_condate),
    rs.stringOpt(c.previousname_9_companyname),
    rs.stringOpt(c.previousname_10_condate),
    rs.stringOpt(c.previousname_10_companyname),
    rs.stringOpt(c.confstmtnextduedate),
    rs.stringOpt(c.confstmtlastmadeupdate),
    rs.long(c.ubrn)
  )


  // Seems to make it easier to use SQL DSL below
  val ch = Company.syntax("ch")

  def create(ch: Company)(implicit session: DBSession = autoSession): Company = {
    withSQL{
      insert.into(Company).namedValues(

        column.ref_period -> ch.ref_period,
        column.companynumber -> ch.companynumber,
        column.companyname -> ch.companyname,
        column.regaddress_careof -> ch.regaddress_careof,
        column.regaddress_pobox -> ch.regaddress_pobox,
        column.regaddress_addressline1 -> ch.regaddress_addressline1,
        column.regaddress_addressline2 -> ch.regaddress_addressline2,
        column.regaddress_posttown -> ch.regaddress_posttown,
        column.regaddress_county -> ch.regaddress_county,
        column.regaddress_country -> ch.regaddress_country,
        column.regaddress_postcode -> ch.regaddress_postcode,
        column.companycategory -> ch.companycategory,
        column.companystatus -> ch.companystatus,
        column.countryoforigin -> ch.countryoforigin,
        column.dissolutiondate -> ch.dissolutiondate,
        column.incorporationdate -> ch.incorporationdate,
        column.accounts_accountrefday -> ch.accounts_accountrefday,
        column.accounts_accountrefmonth -> ch.accounts_accountrefmonth,
        column.accounts_nextduedate -> ch.accounts_nextduedate,
        column.accounts_lastmadeupdate -> ch.accounts_lastmadeupdate,
        column.accounts_accountcategory -> ch.accounts_accountcategory,
        column.returns_nextduedate -> ch.returns_nextduedate,
        column.returns_lastmadeupdate -> ch.returns_lastmadeupdate,
        column.mortgages_nummortcharges -> ch.mortgages_nummortcharges,
        column.mortgages_nummortoutstanding -> ch.mortgages_nummortoutstanding,
        column.mortgages_nummortpartsatisfied -> ch.mortgages_nummortpartsatisfied,
        column.mortgages_nummortsatisfied -> ch.mortgages_nummortsatisfied,
        column.siccode_sictext_1 -> ch.siccode_sictext_1,
        column.siccode_sictext_2 -> ch.siccode_sictext_2,
        column.siccode_sictext_3 -> ch.siccode_sictext_3,
        column.siccode_sictext_4 -> ch.siccode_sictext_4,
        column.limitedpartnerships_numgenpartners -> ch.limitedpartnerships_numgenpartners,
        column.limitedpartnerships_numlimpartners -> ch.limitedpartnerships_numlimpartners,
        column.uri -> ch.uri,
        column.previousname_1_condate -> ch.previousname_1_condate,
        column.previousname_1_companyname -> ch.previousname_1_companyname,
        column.previousname_2_condate -> ch.previousname_2_condate,
        column.previousname_2_companyname -> ch.previousname_2_companyname,
        column.previousname_3_condate -> ch.previousname_3_condate,
        column.previousname_3_companyname -> ch.previousname_3_companyname,
        column.previousname_4_condate -> ch.previousname_4_condate,
        column.previousname_4_companyname -> ch.previousname_4_companyname,
        column.previousname_5_condate -> ch.previousname_5_condate,
        column.previousname_5_companyname -> ch.previousname_5_companyname,
        column.previousname_6_condate -> ch.previousname_6_condate,
        column.previousname_6_companyname -> ch.previousname_6_companyname,
        column.previousname_7_condate -> ch.previousname_7_condate,
        column.previousname_7_companyname -> ch.previousname_7_companyname,
        column.previousname_8_condate -> ch.previousname_8_condate,
        column.previousname_8_companyname -> ch.previousname_8_companyname,
        column.previousname_9_condate -> ch.previousname_9_condate,
        column.previousname_9_companyname -> ch.previousname_9_companyname,
        column.previousname_10_condate -> ch.previousname_10_condate,
        column.previousname_10_companyname -> ch.previousname_10_companyname,
        column.confstmtnextduedate -> ch.confstmtnextduedate,
        column.confstmtlastmadeupdate -> ch.confstmtlastmadeupdate,
        column.ubrn -> ch.ubrn
      )
    }.update.apply()
    ch
  }

  def save(ch: Company)(implicit session: DBSession = autoSession): Company = {
    withSQL{
      update(Company).set(
        column.companyname -> ch.companyname,
        column.regaddress_careof -> ch.regaddress_careof,
        column.regaddress_pobox -> ch.regaddress_pobox,
        column.regaddress_addressline1 -> ch.regaddress_addressline1,
        column.regaddress_addressline2 -> ch.regaddress_addressline2,
        column.regaddress_posttown -> ch.regaddress_posttown,
        column.regaddress_county -> ch.regaddress_county,
        column.regaddress_country -> ch.regaddress_country,
        column.regaddress_postcode -> ch.regaddress_postcode,
        column.companycategory -> ch.companycategory,
        column.companystatus -> ch.companystatus,
        column.countryoforigin -> ch.countryoforigin,
        column.dissolutiondate -> ch.dissolutiondate,
        column.incorporationdate -> ch.incorporationdate,
        column.accounts_accountrefday -> ch.accounts_accountrefday,
        column.accounts_accountrefmonth -> ch.accounts_accountrefmonth,
        column.accounts_nextduedate -> ch.accounts_nextduedate,
        column.accounts_lastmadeupdate -> ch.accounts_lastmadeupdate,
        column.accounts_accountcategory -> ch.accounts_accountcategory,
        column.returns_nextduedate -> ch.returns_nextduedate,
        column.returns_lastmadeupdate -> ch.returns_lastmadeupdate,
        column.mortgages_nummortcharges -> ch.mortgages_nummortcharges,
        column.mortgages_nummortoutstanding -> ch.mortgages_nummortoutstanding,
        column.mortgages_nummortpartsatisfied -> ch.mortgages_nummortpartsatisfied,
        column.mortgages_nummortsatisfied -> ch.mortgages_nummortsatisfied,
        column.siccode_sictext_1 -> ch.siccode_sictext_1,
        column.siccode_sictext_2 -> ch.siccode_sictext_2,
        column.siccode_sictext_3 -> ch.siccode_sictext_3,
        column.siccode_sictext_4 -> ch.siccode_sictext_4,
        column.limitedpartnerships_numgenpartners -> ch.limitedpartnerships_numgenpartners,
        column.limitedpartnerships_numlimpartners -> ch.limitedpartnerships_numlimpartners,
        column.uri -> ch.uri,
        column.previousname_1_condate -> ch.previousname_1_condate,
        column.previousname_1_companyname -> ch.previousname_1_companyname,
        column.previousname_2_condate -> ch.previousname_2_condate,
        column.previousname_2_companyname -> ch.previousname_2_companyname,
        column.previousname_3_condate -> ch.previousname_3_condate,
        column.previousname_3_companyname -> ch.previousname_3_companyname,
        column.previousname_4_condate -> ch.previousname_4_condate,
        column.previousname_4_companyname -> ch.previousname_4_companyname,
        column.previousname_5_condate -> ch.previousname_5_condate,
        column.previousname_5_companyname -> ch.previousname_5_companyname,
        column.previousname_6_condate -> ch.previousname_6_condate,
        column.previousname_6_companyname -> ch.previousname_6_companyname,
        column.previousname_7_condate -> ch.previousname_7_condate,
        column.previousname_7_companyname -> ch.previousname_7_companyname,
        column.previousname_8_condate -> ch.previousname_8_condate,
        column.previousname_8_companyname -> ch.previousname_8_companyname,
        column.previousname_9_condate -> ch.previousname_9_condate,
        column.previousname_9_companyname -> ch.previousname_9_companyname,
        column.previousname_10_condate -> ch.previousname_10_condate,
        column.previousname_10_companyname -> ch.previousname_10_companyname,
        column.confstmtnextduedate -> ch.confstmtnextduedate,
        column.confstmtlastmadeupdate -> ch.confstmtlastmadeupdate,
        column.ubrn -> ch.ubrn
      ).where
        .eq(column.ref_period, ch.ref_period).and.eq(column.companynumber, ch.companynumber)
    }.update.apply()
    ch
  }

  def find(ref_period: Long, companynumber: String)(implicit session: DBSession = autoSession): Option[Company] = withSQL {
    select.from(Company as ch)
      .where.eq(ch.ref_period, ref_period)
      .and.eq(ch.companynumber, companynumber)
  }.map(Company(ch)).single.apply()

  def findByLegalUnit(ref_period: Long, ubrn: Long)(implicit session: DBSession = autoSession): List[Company] = withSQL {
    select.from(Company as ch)
      .where.eq(ch.ref_period, ref_period)
      .and.eq(ch.ubrn, ubrn)
  }.map(Company(ch)).list.apply()

  def destroy(ref_period: Long, companynumber: String)(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(Company as ch)
      .where.eq(ch.ref_period, ref_period)
      .and.eq(ch.companynumber, companynumber)
  }.update.apply()

  def destroyAll()(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(Company as ch)
  }.update.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Company as ch)
  }.map(rs => rs.long(1)).single.apply().get

}

