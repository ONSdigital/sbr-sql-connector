package uk.gov.ons.sbr.data.db

import uk.gov.ons.sbr.data.model.{Children, Enterprise, StatUnit}

object EnterpriseDao extends SbrDao[Enterprise]{

  def getAsStatUnit(ref_period: Long, entref: Long): Option[StatUnit] = Enterprise.getAsStatUnit(ref_period, entref)

  def getEnterprise(ref_period: Long, entref: Long): Option[Enterprise] = Enterprise.find(ref_period, entref)

  def updateEntStatUnit(statUnit: StatUnit): StatUnit = {
    val ent = Enterprise(statUnit)
    val savedEnt: Enterprise = update(ent)
    StatUnit(savedEnt)
  }

  def getChildren(ref_period: Long, ubrn: Long): Children = {
    // Get LEUs if any
    val leus: Option[Seq[String]] = LegalUnitDao.getLegalUnitsForEnterprise(ref_period, ubrn).map(_.entref.toString)
    match {case Nil => None
      case  xs: Seq[String] => Some(xs)}
    Children(legalunits = leus)
  }

  // general DAO methods

  override def insert(data: Enterprise): Enterprise = Enterprise.create(data)

  override def update(data: Enterprise): Enterprise = data.save

  override def delete(data: Enterprise): Unit = data.destroy

  override def deleteAll: Unit = Enterprise.destroyAll()

  override def count(): Long = Enterprise.countAll()

}
