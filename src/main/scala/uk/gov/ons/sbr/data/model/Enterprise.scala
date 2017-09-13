package uk.gov.ons.sbr.data.model

import scalikejdbc._

// Use JSOn to convert class -> JSON -> Map easily
import play.api.libs.json._


case class Enterprise(ref_period: Long,
                      entref: Long,
                      ent_tradingstyle: Option[String],
                      ent_address1: Option[String] = None,
                      ent_address2: Option[String] = None,
                      ent_address3: Option[String] = None,
                      ent_address4: Option[String] = None,
                      ent_address5: Option[String] = None,
                      ent_postcode: Option[String] = None,
                      legalstatus: Option[String] = None,
                      paye_jobs: Option[String] = None,
                      employees: Option[String] = None,
                      standard_vat_turnover: Option[String] = None,
                      num_unique_payerefs: Option[String] = None,
                      num_unique_vatrefs: Option[String] = None,
                      contained_rep_vat_turnover: Option[String] = None
                     ) {
  // Allows you to save current instance directly
  def save()(implicit session: DBSession = Enterprise.autoSession): Enterprise = Enterprise.save(this)(session)

  // deletes this instance from teh databaser
  def destroy()(implicit session: DBSession = Enterprise.autoSession): Unit =
    Enterprise.destroy(this.ref_period, this.entref)(session)


}

object Enterprise extends SQLSyntaxSupport[Enterprise] {

  // This bit will allow us to convert to/from JSON
  implicit val enterpriseWrites = Json.writes[Enterprise]

  // This stuff is for converting to StatUnits

  val excludeFields: Seq[String] = List("entref", "ref_period")

  def variablesToMap(ent: Enterprise): Map[String, String] = {
    // convert to JSON
    val entJson: JsValue = Json.toJson(ent)

    // now convert to Map of String -> JsValue
    val entMap: Map[String, JsValue] = entJson match {
      case JsObject(fields) => fields.toMap
      case _ => Map.empty[String, JsValue]
    }
    // convert to Map of String -> String and exclude key fields
    val varMap: Map[String, String] =
      for {
        (k, v) <- entMap
        if (!(excludeFields.contains(k)))
      } yield (k, v.as[String])
    varMap
  }

  // Convert from a StatUnit (remember all data fields are in variables map):

  def apply(su: StatUnit): Enterprise = {
    if (su.unitType == UnitType.ENT.toString)
      Enterprise(
        su.refPeriod,
        su.key.toLong,
        su.variables.get("ent_tradingstyle"),
        su.variables.get("ent_address1"),
        su.variables.get("ent_address2"),
        su.variables.get("ent_address3"),
        su.variables.get("ent_address4"),
        su.variables.get("ent_address5"),
        su.variables.get("ent_postcode"),
        su.variables.get("legalstatus"),
        su.variables.get("paye_jobs"),
        su.variables.get("employees"),
        su.variables.get("standard_vat_turnover"),
        su.variables.get("num_unique_payerefs"),
        su.variables.get("num_unique_vatrefs"),
        su.variables.get("contained_rep_vat_turnover")
      )
      else
        throw new IllegalArgumentException(s"Requires Enterprise. Found StatUnit of type ${su.unitType}")
  }


  // This is where the DB voodoo happens

  override val tableName = "ent_2500"

  def apply(e: SyntaxProvider[Enterprise])(rs: WrappedResultSet): Enterprise = apply(e.resultName)(rs)

  def apply(e: ResultName[Enterprise])(rs: WrappedResultSet): Enterprise = new Enterprise(
    rs.long("ref_period"),
    rs.long("entref"),
    rs.stringOpt("ent_tradingstyle"),
    rs.stringOpt("ent_address1"),
    rs.stringOpt("ent_address2"),
    rs.stringOpt("ent_address3"),
    rs.stringOpt("ent_address4"),
    rs.stringOpt("ent_address5"),
    rs.stringOpt("ent_postcode"),
    rs.stringOpt("legalstatus"),
    rs.stringOpt("paye_jobs"),
    rs.stringOpt("employees"),
    rs.stringOpt("standard_vat_turnover"),
    rs.stringOpt("num_unique_payerefs"),
    rs.stringOpt("num_unique_vatrefs"),
    rs.stringOpt("contained_rep_vat_turnover")
  )

  // Seems to make it easier to use SQL DSL below
  val e = Enterprise.syntax("e")

  // Standard database operations ...

  def create(ent: Enterprise)(implicit session: DBSession = autoSession): Enterprise = {
    withSQL {
      insert.into(Enterprise).namedValues(
        column.ref_period -> ent.ref_period,
        column.entref -> ent.entref,
        column.ent_tradingstyle -> ent.ent_tradingstyle,
        column.ent_address1 -> ent.ent_address1,
        column.ent_address2 -> ent.ent_address2,
        column.ent_address3 -> ent.ent_address3,
        column.ent_address4 -> ent.ent_address4,
        column.ent_address5 -> ent.ent_address5,
        column.ent_postcode -> ent.ent_postcode,
        column.legalstatus -> ent.legalstatus,
        column.paye_jobs -> ent.paye_jobs,
        column.employees -> ent.employees,
        column.standard_vat_turnover -> ent.standard_vat_turnover,
        column.num_unique_payerefs -> ent.num_unique_payerefs,
        column.num_unique_vatrefs -> ent.num_unique_vatrefs,
        column.contained_rep_vat_turnover -> ent.contained_rep_vat_turnover
      )
    }.update.apply()

    ent
  }

  def save(ent: Enterprise)(implicit session: DBSession = autoSession): Enterprise = {
    withSQL {
      update(Enterprise).set(
        column.ent_tradingstyle -> ent.ent_tradingstyle,
        column.ent_address1 -> ent.ent_address1,
        column.ent_address2 -> ent.ent_address2,
        column.ent_address3 -> ent.ent_address3,
        column.ent_address4 -> ent.ent_address4,
        column.ent_address5 -> ent.ent_address5,
        column.ent_postcode -> ent.ent_postcode,
        column.legalstatus -> ent.legalstatus,
        column.paye_jobs -> ent.paye_jobs,
        column.employees -> ent.employees,
        column.standard_vat_turnover -> ent.standard_vat_turnover,
        column.num_unique_payerefs -> ent.num_unique_payerefs,
        column.num_unique_vatrefs -> ent.num_unique_vatrefs,
        column.contained_rep_vat_turnover -> ent.contained_rep_vat_turnover
      ).where.eq(column.ref_period, ent.ref_period).and.eq(column.entref, ent.entref)
    }.update.apply()
    ent
  }

  def find(ref_period: Long, entref: Long)(implicit session: DBSession = autoSession): Option[Enterprise] = withSQL {
    select.from(Enterprise as e).where.eq(e.ref_period, ref_period).and.eq(e.entref, entref)
  }.map(Enterprise(e)).single.apply()

  def destroy(ref_period: Long, entref: Long)(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(Enterprise as e).where.eq(e.ref_period, ref_period).and.eq(e.entref, entref)
  }.update.apply()

  def destroyAll()(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(Enterprise as e)
  }.update.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Enterprise as e)
  }.map(rs => rs.long(1)).single.apply().get

  def getAsStatUnit(ref_period: Long, entref: Long)(implicit session: DBSession = autoSession): Option[StatUnit] = {
    find(ref_period, entref)(session).map(StatUnit(_))
  }


}