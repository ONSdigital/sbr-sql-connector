package uk.gov.ons.sbr.data.model

object PayeDao extends SbrDao[Paye]{

  def getPayesForLegalUnit(ref_period: Long, ubrn: Long): List[Paye] = Paye.findByLegalUnit(ref_period, ubrn)

  def getPaye(ref_period: Long, payeref: String): Option[Paye] = Paye.find(ref_period, payeref)

  override def insert(data: Paye): Paye = Paye.create(data)

  override def update(data: Paye): Paye = data.save

  override def delete(data: Paye): Unit = data.destroy

  override def deleteAll: Unit = Paye.destroyAll()

  override def count(): Long = Paye.countAll()

}

