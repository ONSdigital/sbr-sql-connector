package uk.gov.ons.sbr.data.model


import ai.x.play.json.Jsonx
import play.api.libs.json.{JsObject, JsValue, Json}
import scalikejdbc._

case class Paye(
                 ref_period: Long,
                 payeref: String,
                 deathcode: Option[String] = None,
                 birthdate: Option[String] = None,
                 deathdate: Option[String] = None,
                 mfullemp: Option[String] = None,
                 msubemp: Option[String] = None,
                 ffullemp: Option[String] = None,
                 fsubemp: Option[String] = None,
                 unclemp: Option[String] = None,
                 unclsubemp: Option[String] = None,
                 dec_jobs: Option[String] = None,
                 mar_jobs: Option[String] = None,
                 june_jobs: Option[String] = None,
                 sept_jobs: Option[String] = None,
                 jobs_lastupd: Option[String] = None,
                 status: Option[String] = None,
                 prevpaye: Option[String] = None,
                 employer_cat: Option[String] = None,
                 stc: Option[String] = None,
                 crn: Option[String] = None,
                 actiondate: Option[String] = None,
                 addressref: Option[String] = None,
                 marker: Option[String] = None,
                 inqcode: Option[String] = None,
                 name1: Option[String] = None,
                 name2: Option[String] = None,
                 name3: Option[String] = None,
                 tradstyle1: Option[String] = None,
                 tradstyle2: Option[String] = None,
                 tradstyle3: Option[String] = None,
                 address1: Option[String] = None,
                 address2: Option[String] = None,
                 address3: Option[String] = None,
                 address4: Option[String] = None,
                 address5: Option[String] = None,
                 postcode: Option[String] = None,
                 mkr: Option[String] = None,
                 ubrn: Long) {
  // Allows you to save current instance directly
  def save()(implicit session: DBSession = Paye.autoSession): Paye = Paye.save(this)(session)

  // deletes this instance from the database
  def destroy()(implicit session: DBSession = Paye.autoSession): Unit = Paye.destroy(this.ref_period, this.payeref)(session)

}

object Paye extends SQLSyntaxSupport[Paye] {

  // This bit will allow us to convert to/from JSON (need to use Jsonx for >22 fields)
  implicit val payeFormat = Jsonx.formatCaseClass[Paye]

  // This stuff is for converting to StatUnits
  val excludeFields: Seq[String] = List("payeref", "ubrn", "ref_period")

  def variablesToMap(obj: Paye): Map[String, String] = {
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

  override val tableName = "paye_2500"

  def apply(e: SyntaxProvider[Paye])(rs: WrappedResultSet): Paye = apply(e.resultName)(rs)

  def apply(data: ResultName[Paye])(rs: WrappedResultSet): Paye = new Paye(
    rs.long(data.ref_period),
    rs.string(data.payeref),
    rs.stringOpt(data.deathcode),
    rs.stringOpt(data.birthdate),
    rs.stringOpt(data.deathdate),
    rs.stringOpt(data.mfullemp),
    rs.stringOpt(data.msubemp),
    rs.stringOpt(data.ffullemp),
    rs.stringOpt(data.fsubemp),
    rs.stringOpt(data.unclemp),
    rs.stringOpt(data.unclsubemp),
    rs.stringOpt(data.dec_jobs),
    rs.stringOpt(data.mar_jobs),
    rs.stringOpt(data.june_jobs),
    rs.stringOpt(data.sept_jobs),
    rs.stringOpt(data.jobs_lastupd),
    rs.stringOpt(data.status),
    rs.stringOpt(data.prevpaye),
    rs.stringOpt(data.employer_cat),
    rs.stringOpt(data.stc),
    rs.stringOpt(data.crn),
    rs.stringOpt(data.actiondate),
    rs.stringOpt(data.addressref),
    rs.stringOpt(data.marker),
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
  val p = Paye.syntax("p")

  // Standard database operations ...

  def create(paye: Paye)(implicit session: DBSession = autoSession): Paye = {
    withSQL {
      insert.into(Paye).namedValues(
        column.ref_period -> paye.ref_period,
        column.payeref -> paye.payeref,
        column.deathcode -> paye.deathcode,
        column.birthdate -> paye.birthdate,
        column.deathdate -> paye.deathdate,
        column.mfullemp -> paye.mfullemp,
        column.msubemp -> paye.msubemp,
        column.ffullemp -> paye.ffullemp,
        column.fsubemp -> paye.fsubemp,
        column.unclemp -> paye.unclemp,
        column.unclsubemp -> paye.unclsubemp,
        column.dec_jobs -> paye.dec_jobs,
        column.mar_jobs -> paye.mar_jobs,
        column.june_jobs -> paye.june_jobs,
        column.sept_jobs -> paye.sept_jobs,
        column.jobs_lastupd -> paye.jobs_lastupd,
        column.status -> paye.status,
        column.prevpaye -> paye.prevpaye,
        column.employer_cat -> paye.employer_cat,
        column.stc -> paye.stc,
        column.crn -> paye.crn,
        column.actiondate -> paye.actiondate,
        column.addressref -> paye.addressref,
        column.marker -> paye.marker,
        column.inqcode -> paye.inqcode,
        column.name1 -> paye.name1,
        column.name2 -> paye.name2,
        column.name3 -> paye.name3,
        column.tradstyle1 -> paye.tradstyle1,
        column.tradstyle2 -> paye.tradstyle2,
        column.tradstyle3 -> paye.tradstyle3,
        column.address1 -> paye.address1,
        column.address2 -> paye.address2,
        column.address3 -> paye.address3,
        column.address4 -> paye.address4,
        column.address5 -> paye.address5,
        column.postcode -> paye.postcode,
        column.mkr -> paye.mkr,
        column.ubrn -> paye.ubrn
      )
    }.update.apply()
    paye
  }


  def save(paye: Paye)(implicit session: DBSession = autoSession): Paye = {
    withSQL {
      update(Paye).set(
        column.deathcode -> paye.deathcode,
        column.birthdate -> paye.birthdate,
        column.deathdate -> paye.deathdate,
        column.mfullemp -> paye.mfullemp,
        column.msubemp -> paye.msubemp,
        column.ffullemp -> paye.ffullemp,
        column.fsubemp -> paye.fsubemp,
        column.unclemp -> paye.unclemp,
        column.unclsubemp -> paye.unclsubemp,
        column.dec_jobs -> paye.dec_jobs,
        column.mar_jobs -> paye.mar_jobs,
        column.june_jobs -> paye.june_jobs,
        column.sept_jobs -> paye.sept_jobs,
        column.jobs_lastupd -> paye.jobs_lastupd,
        column.status -> paye.status,
        column.prevpaye -> paye.prevpaye,
        column.employer_cat -> paye.employer_cat,
        column.stc -> paye.stc,
        column.crn -> paye.crn,
        column.actiondate -> paye.actiondate,
        column.addressref -> paye.addressref,
        column.marker -> paye.marker,
        column.inqcode -> paye.inqcode,
        column.name1 -> paye.name1,
        column.name2 -> paye.name2,
        column.name3 -> paye.name3,
        column.tradstyle1 -> paye.tradstyle1,
        column.tradstyle2 -> paye.tradstyle2,
        column.tradstyle3 -> paye.tradstyle3,
        column.address1 -> paye.address1,
        column.address2 -> paye.address2,
        column.address3 -> paye.address3,
        column.address4 -> paye.address4,
        column.address5 -> paye.address5,
        column.postcode -> paye.postcode,
        column.mkr -> paye.mkr,
        column.ubrn -> paye.ubrn
      ).where
        .eq(column.ref_period, paye.ref_period).and.eq(column.payeref, paye.payeref)
    }.update.apply()
    paye
  }

  def find(ref_period: Long, payeref: String)(implicit session: DBSession = autoSession): Option[Paye] = withSQL {
    select.from(Paye as p)
      .where.eq(p.ref_period, ref_period)
      .and.eq(p.payeref, payeref)
  }.map(Paye(p)).single.apply()

  def findByLegalUnit(ref_period: Long, ubrn: Long)(implicit session: DBSession = autoSession): List[Paye] = withSQL {
    select.from(Paye as p)
      .where.eq(p.ref_period, ref_period)
      .and.eq(p.ubrn, ubrn)
  }.map(Paye(p)).list.apply()

  def destroy(ref_period: Long, payeref: String)(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(Paye as p)
      .where.eq(p.ref_period, ref_period)
      .and.eq(p.payeref, payeref)
  }.update.apply()

  def destroyAll()(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(Paye as p)
  }.update.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Paye as p)
  }.map(rs => rs.long(1)).single.apply().get

}
