package uk.gov.ons.sbr.data.model


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
                leu_id : Long)
{
  // Allows you to save current instance directly
  def save()(implicit session: DBSession = Vat.autoSession): Vat = Vat.save(this)(session)

  // deletes this instance from the database
  def destroy()(implicit session: DBSession = Vat.autoSession): Unit = Vat.destroy(this.ref_period, this.vatref)(session)

}

object Vat extends SQLSyntaxSupport[Vat] {
  // This is where the DB voodoo happens

  override val tableName = "vat_2500"

  def apply(e: SyntaxProvider[Vat])(rs: WrappedResultSet): Vat = apply(e.resultName)(rs)

  def apply(e: ResultName[Vat])(rs: WrappedResultSet): Vat = new Vat(
    rs.long("ref_period"),
    rs.string("vatref"),
    rs.stringOpt("deathcode"),
    rs.stringOpt("birthdate"),
    rs.stringOpt("deathdate"),
    rs.stringOpt("sic92"),
    rs.stringOpt("turnover"),
    rs.stringOpt("turnover_date"),
    rs.stringOpt("record_type"),
    rs.stringOpt("status"),
    rs.stringOpt("actiondate"),
    rs.stringOpt("crn"),
    rs.stringOpt("marker"),
    rs.stringOpt("addressref"),
    rs.stringOpt("inqcode"),
    rs.stringOpt("name1"),
    rs.stringOpt("name2"),
    rs.stringOpt("name3"),
    rs.stringOpt("tradstyle1"),
    rs.stringOpt("tradstyle2"),
    rs.stringOpt("tradstyle3"),
    rs.stringOpt("address1"),
    rs.stringOpt("address2"),
    rs.stringOpt("address3"),
    rs.stringOpt("address4"),
    rs.stringOpt("address5"),
    rs.stringOpt("postcode"),
    rs.stringOpt("mkr"),
    rs.long("leu_id")
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
        column.leu_id -> vat.leu_id
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
        column.leu_id -> vat.leu_id
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
      .and.eq(v.leu_id, ubrn)
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

