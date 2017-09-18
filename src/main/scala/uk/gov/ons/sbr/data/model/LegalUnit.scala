package uk.gov.ons.sbr.data.model


import play.api.libs.json.{JsObject, JsValue, Json}
import scalikejdbc._
import uk.gov.ons.sbr.data.db.{CompanyDao, PayeDao, VatDao}

case class LegalUnit(
                      ref_period: Long,
                      ubrn: Long,
                      businessname: Option[String] = None,
                      postcode: Option[String] = None,
                      industrycode: Option[String] = None,
                      legalstatus: Option[String] = None,
                      tradingstatus: Option[String] = None,
                      turnover: Option[String] = None,
                      employmentbands: Option[String] = None,
                      entref: Long)
{
  // Allows you to save current instance directly
  def save()(implicit session: DBSession = LegalUnit.autoSession): LegalUnit = LegalUnit.save(this)(session)

  // deletes this instance from the databaser
  def destroy()(implicit session: DBSession = LegalUnit.autoSession): Unit =
    LegalUnit.destroy(this.ref_period, this.ubrn)(session)

}


object LegalUnit extends SQLSyntaxSupport[LegalUnit] {

  // This bit will allow us to convert to/from JSON
  implicit val leuWrites = Json.writes[LegalUnit]

  // This stuff is for converting to StatUnits

  val excludeFields: Seq[String] = List("entref", "ubrn", "ref_period")

  def variablesToMap(obj: LegalUnit): Map[String, String] = {
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

  override val tableName = "leu_2500"

  def apply(e: SyntaxProvider[LegalUnit])(rs: WrappedResultSet): LegalUnit = apply(e.resultName)(rs)

  def apply(data: ResultName[LegalUnit])(rs: WrappedResultSet): LegalUnit = new LegalUnit(
    rs.long(data.ref_period),
    rs.long(data.ubrn),
    rs.stringOpt(data.businessname),
    rs.stringOpt(data.postcode),
    rs.stringOpt(data.industrycode),
    rs.stringOpt(data.legalstatus),
    rs.stringOpt(data.tradingstatus),
    rs.stringOpt(data.turnover),
    rs.stringOpt(data.employmentbands),
    rs.long(data.entref)
  )

  // Seems to make it easier to use SQL DSL below
  val u = LegalUnit.syntax("u")

  // Standard database operations ...

  def create(leu: LegalUnit)(implicit session: DBSession = autoSession): LegalUnit = {
    withSQL {
      insert.into(LegalUnit).namedValues(
        column.ref_period -> leu.ref_period,
        column.ubrn -> leu.ubrn,
        column.businessname -> leu.businessname,
        column.postcode -> leu.postcode,
        column.industrycode -> leu.industrycode,
        column.legalstatus -> leu.legalstatus,
        column.tradingstatus -> leu.tradingstatus,
        column.turnover -> leu.turnover,
        column.employmentbands -> leu.employmentbands,
        column.entref -> leu.entref
      )
    }.update.apply()

    leu
  }

  def save(leu: LegalUnit)(implicit session: DBSession = autoSession): LegalUnit = {
    withSQL {
      update(LegalUnit).set(
        column.businessname -> leu.businessname,
        column.postcode -> leu.postcode,
        column.industrycode -> leu.industrycode,
        column.legalstatus -> leu.legalstatus,
        column.tradingstatus -> leu.tradingstatus,
        column.turnover -> leu.turnover,
        column.employmentbands -> leu.employmentbands,
        column.entref -> leu.entref
      ).where.eq(column.ref_period, leu.ref_period).and.eq(column.ubrn, leu.ubrn)
    }.update.apply()

    leu
  }

  def find(ref_period: Long, ubrn: Long)(implicit session: DBSession = autoSession): Option[LegalUnit] = withSQL {
    select.from(LegalUnit as u)
      .where.eq(u.ref_period, ref_period)
      .and.eq(u.ubrn, ubrn)
  }.map(LegalUnit(u)).single.apply()

  def findByEnt(ref_period: Long, entref: Long)(implicit session: DBSession = autoSession): List[LegalUnit] = withSQL {
    select.from(LegalUnit as u)
      .where.eq(u.ref_period, ref_period)
      .and.eq(u.entref, entref)
      .orderBy(u.ubrn)
  }.map(LegalUnit(u)).list.apply()


  def destroy(ref_period: Long, ubrn: Long)(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(LegalUnit as u)
      .where.eq(u.ref_period, ref_period)
      .and.eq(u.ubrn, ubrn)
  }.update.apply()

  def destroyAll()(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(LegalUnit as u)
  }.update.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(LegalUnit as u)
  }.map(rs => rs.long(1)).single.apply().get

  // Need to get child objects for an LEU

  def getLeuChildren(ref_period: Long, ubrn: Long)(implicit session: DBSession = autoSession): LeuChildren = {

    // Get Company if any - Should only be ONE CH entry for an LEU
    val ch: Option[String] = CompanyDao.getCompaniesForLegalUnit(ref_period, ubrn).map(_.companynumber).headOption

    // Get PAYEs if any
    val payes = PayeDao.getPayesForLegalUnit(ref_period, ubrn).map(_.payeref)

    // Get VATs if any
    val vats = VatDao.getVatsForLegalUnit(ref_period, ubrn).map(_.vatref)

    LeuChildren(ref_period, ubrn, ch, payes, vats)
  }

  def getChildren(ref_period: Long, ubrn: Long)(implicit session: DBSession = autoSession): Children = {
    // Get Company if any - Should only be ONE CH entry for an LEU
    val ch: Option[String] = CompanyDao.getCompaniesForLegalUnit(ref_period, ubrn).map(_.companynumber).headOption

    // Get PAYEs if any
    val payes: Option[Seq[String]] = PayeDao.getPayesForLegalUnit(ref_period, ubrn).map(_.payeref)
    match {case Nil => None
      case  xs: Seq[String] => Some(xs)}

    // Get VATs if any
    val vats: Option[Seq[String]] = VatDao.getVatsForLegalUnit(ref_period, ubrn).map(_.vatref)
      match {case Nil => None
      case  xs: Seq[String] => Some(xs)}
    // Children object can have LEUs, but we do not need them here.
    Children(ch = ch, paye = payes, vat = vats)
  }

  def getAsStatUnit(ref_period: Long, ubrn: Long)(implicit session: DBSession = autoSession): Option[StatUnit] = {

    val leu = find(ref_period, ubrn)(session)
    leu.map(StatUnit(_))
  }


}