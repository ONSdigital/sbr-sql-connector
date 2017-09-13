package uk.gov.ons.sbr.data.db

import uk.gov.ons.sbr.data.model.{LegalUnit, LeuChildren, StatUnit}

object LegalUnitDao extends SbrDao[LegalUnit]{

  def getAsStatUnit(ref_period: Long, ubrn: Long): Option[StatUnit] = LegalUnit.getAsStatUnit(ref_period, ubrn)

  def getLegalUnit(ref_period: Long, ubrn: Long): Option[LegalUnit] = LegalUnit.find(ref_period, ubrn)

  def getLegalUnitsForEnterprise(ref_period: Long, entref: Long): Seq[LegalUnit] = LegalUnit.findByEnt(ref_period, entref)

  def getChildren(ref_period: Long, ubrn: Long): LeuChildren = LegalUnit.getChildren(ref_period, ubrn)

  override def insert(data: LegalUnit): LegalUnit = LegalUnit.create(data)

  override def update(data: LegalUnit): LegalUnit = data.save

  override def delete(data: LegalUnit): Unit = data.destroy

  override def deleteAll: Unit = LegalUnit.destroyAll()

  override def count(): Long = LegalUnit.countAll()

}

