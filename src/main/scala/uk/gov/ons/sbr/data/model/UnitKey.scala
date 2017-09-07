package uk.gov.ons.sbr.data.model

import scalikejdbc._

case class UnitKey( ref_period: Long,
                    unitType: String,
                    unitId: String
                     ) {

  // deletes this instance from the database
  def destroy()(implicit session: DBSession = UnitKey.autoSession): Unit =
    UnitKey.destroy(this.ref_period, this.unitType, this.unitId)(session)
}

object UnitKey extends SQLSyntaxSupport[UnitKey] {
  // This is where the DB voodoo happens

  override val tableName = "unit_2500"

  def apply(e: SyntaxProvider[UnitKey])(rs: WrappedResultSet): UnitKey = apply(e.resultName)(rs)

  def apply(e: ResultName[UnitKey])(rs: WrappedResultSet): UnitKey = new UnitKey(
    rs.long("ref_period"),
    rs.string("unit_type"),
    rs.string("unit_id")
  )

  // Seems to make it easier to use SQL DSL below
  val uk = UnitKey.syntax("uk")

  // Standard database operations ...
  def create(unk: UnitKey)(implicit session: DBSession = autoSession): UnitKey = {
    withSQL {
      insert.into(UnitKey).namedValues(
        column.ref_period -> unk.ref_period,
        column.unitType -> unk.unitType,
        column.unitId -> unk.unitId)
    }.update.apply()

    unk
  }


  def findById(ref_period: Long, unitId: String)(implicit session: DBSession = autoSession): List[UnitKey] = withSQL {
    select.from(UnitKey as uk).where.eq(uk.ref_period, ref_period).and.eq(uk.unitId, unitId)
  }.map(UnitKey(uk)).list.apply()


  def destroy(ref_period: Long, unitType: String, unitId: String)(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(UnitKey as uk)
      .where.eq(uk.ref_period, ref_period)
      .and.eq(uk.unitType, unitType)
      .and.eq(uk.unitId, unitId)
  }.update.apply()

  def destroyAll()(implicit session: DBSession = autoSession): Unit = withSQL {
    deleteFrom(UnitKey as uk)
  }.update.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(UnitKey as uk)
  }.map(rs => rs.long(1)).single.apply().get

}