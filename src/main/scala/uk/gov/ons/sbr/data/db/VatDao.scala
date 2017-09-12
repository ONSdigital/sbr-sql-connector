package uk.gov.ons.sbr.data.db

import uk.gov.ons.sbr.data.model.Vat

object VatDao extends SbrDao[Vat]{

  def getVatsForLegalUnit(ref_period: Long, ubrn: Long): List[Vat] = Vat.findByLegalUnit(ref_period, ubrn)

  def getVat(ref_period: Long, vatref: String): Option[Vat] = Vat.find(ref_period, vatref)

  override def insert(data: Vat): Vat = Vat.create(data)

  override def update(data: Vat): Vat = data.save

  override def delete(data: Vat): Unit = data.destroy

  override def deleteAll: Unit = Vat.destroyAll()

  override def count(): Long = Vat.countAll()

}
