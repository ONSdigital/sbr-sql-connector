package uk.gov.ons.sbr.data.model

object UnitKeyRepo extends DataRepo[UnitKey]{

  def findById(refPeriod: Long, unitId: String): List[UnitKey] = UnitKey.findById(refPeriod, unitId)

  override def insert(data: UnitKey): UnitKey = UnitKey.create(data)

  // Record is all PK so no update possible
  override def update(data: UnitKey): UnitKey = UnitKey.create(data)

  override def delete(data: UnitKey): Unit = data.destroy

  override def deleteAll: Unit = UnitKey.destroyAll()

  override def count(): Long = UnitKey.countAll()

}
