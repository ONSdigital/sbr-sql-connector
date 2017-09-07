package uk.gov.ons.sbr.data.model


import scalikejdbc._

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
  // This is where the DB voodoo happens

  override val tableName = "leu_2500"

  def apply(e: SyntaxProvider[LegalUnit])(rs: WrappedResultSet): LegalUnit = apply(e.resultName)(rs)

  def apply(e: ResultName[LegalUnit])(rs: WrappedResultSet): LegalUnit = new LegalUnit(
    rs.long("ref_period"),
    rs.long("ubrn"),
    rs.stringOpt("businessname"),
    rs.stringOpt("postcode"),
    rs.stringOpt("industrycode"),
    rs.stringOpt("legalstatus"),
    rs.stringOpt("tradingstatus"),
    rs.stringOpt("turnover"),
    rs.stringOpt("employmentbands"),
    rs.long("entref")
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

}