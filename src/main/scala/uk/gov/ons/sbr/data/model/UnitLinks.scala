package uk.gov.ons.sbr.data.model

import scalikejdbc._

case class UnitLinks(ref_period: Long,
                     unitType: String,
                     unitId: String,
                     pEnt: Option[Long] = None,
                     pLeu: Option[Long] = None,
                     children: Option[String] = None
                    ) {

  // deletes this instance from the database
  def destroy()(implicit session: DBSession = UnitLinks.autoSession): Unit =
    UnitLinks.destroy(this.ref_period, this.unitType, this.unitId)(session)

  // Allows you to save current instance directly
  def save()(implicit session: DBSession = UnitLinks.autoSession): UnitLinks =
    UnitLinks.save(this)(session)

}

object UnitLinks extends SQLSyntaxSupport[UnitLinks] {
  // This is where the DB voodoo happens

  override val tableName = "unit_links_2500"

  def apply(e: SyntaxProvider[UnitLinks])(rs: WrappedResultSet): UnitLinks = apply(e.resultName)(rs)

  def apply(e: ResultName[UnitLinks])(rs: WrappedResultSet): UnitLinks = new UnitLinks(
    rs.long("ref_period"),
    rs.string("unit_type"),
    rs.string("unit_id"),
    rs.longOpt("p_ent"),
    rs.longOpt("p_leu"),
    rs.stringOpt("children")
  )

  // Seems to make it easier to use SQL DSL below
  val lnk = UnitLinks.syntax("lnk")

  // Standard database operations ...
  def create(unitLink: UnitLinks)(implicit session: DBSession = autoSession): UnitLinks = {
    withSQL {
      insert.into(UnitLinks).namedValues(
        column.ref_period -> unitLink.ref_period,
        column.unitType -> unitLink.unitType,
        column.unitId -> unitLink.unitId,
        column.pEnt -> unitLink.pEnt,
        column.pLeu -> unitLink.pLeu,
        column.children -> unitLink.children
      )
    }.update.apply()

    unitLink
  }

  def save(unitLink: UnitLinks)(implicit session: DBSession = autoSession): UnitLinks = {
    withSQL {
      update(UnitLinks as lnk).set(
        lnk.pEnt -> unitLink.pEnt,
        lnk.pLeu -> unitLink.pLeu,
        lnk.children -> unitLink.children
      ).where.eq(column.ref_period, unitLink.ref_period)
        .and.eq(column.unitType, unitLink.unitType)
        .and.eq(column.unitId, unitLink.unitId)
    }.update.apply()

    unitLink
  }

  def findByKey(ref_period: Long, unitType: String, unitId: String)(implicit session: DBSession = autoSession): Option[UnitLinks] = withSQL {
    select.from(UnitLinks as lnk)
      .where.eq(lnk.ref_period, ref_period)
      .and.eq(lnk.unitType, unitType)
      .and.eq(lnk.unitId, unitId)
  }.map(UnitLinks(lnk)).single.apply()

  def findById(ref_period: Long, unitId: String)(implicit session: DBSession = autoSession): List[UnitLinks] = withSQL {
    select.from(UnitLinks as lnk).where.eq(lnk.ref_period, ref_period).and.eq(lnk.unitId, unitId)
  }.map(UnitLinks(lnk)).list.apply()

  def destroy(ref_period: Long, unitType: String, unitId: String)(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(UnitLinks as lnk)
      .where.eq(lnk.ref_period, ref_period)
      .and.eq(lnk.unitType, unitType)
      .and.eq(lnk.unitId, unitId)
  }.update.apply()

  def destroyAll()(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(UnitLinks as lnk)
  }.update.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(UnitLinks as lnk)
  }.map(rs => rs.long(1)).single.apply().get


}