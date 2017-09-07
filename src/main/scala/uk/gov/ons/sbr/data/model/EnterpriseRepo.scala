package uk.gov.ons.sbr.data.model


object EnterpriseRepo extends DataRepo[Enterprise]{

  def getEnterprise(ref_period: Long, entref: Long): Option[Enterprise] = Enterprise.find(ref_period, entref)

  override def insert(data: Enterprise): Enterprise = Enterprise.create(data)

  override def update(data: Enterprise): Enterprise = data.save

  override def delete(data: Enterprise): Unit = data.destroy

  override def deleteAll: Unit = Enterprise.destroyAll()

  override def count(): Long = Enterprise.countAll()

}
