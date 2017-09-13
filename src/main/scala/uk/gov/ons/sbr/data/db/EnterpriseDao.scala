package uk.gov.ons.sbr.data.db

import uk.gov.ons.sbr.data.model.{Enterprise, StatUnit}

object EnterpriseDao extends SbrDao[Enterprise]{

  def getAsStatUnit(ref_period: Long, entref: Long): Option[StatUnit] = Enterprise.getAsStatUnit(ref_period, entref)

  def getEnterprise(ref_period: Long, entref: Long): Option[Enterprise] = Enterprise.find(ref_period, entref)

  override def insert(data: Enterprise): Enterprise = Enterprise.create(data)

  override def update(data: Enterprise): Enterprise = data.save

  override def delete(data: Enterprise): Unit = data.destroy

  override def deleteAll: Unit = Enterprise.destroyAll()

  override def count(): Long = Enterprise.countAll()

}
