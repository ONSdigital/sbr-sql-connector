/**
 * UnitLinksDao.scala
 * --------------
 * Author: websc
 * Date: 14/09/17 08:45
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.db

import uk.gov.ons.sbr.data.model.UnitLinks

object UnitLinksDao extends SbrDao[UnitLinks] {

  def findByKey(ref_period: Long, unitType: String, unitId: String): Option[UnitLinks] = UnitLinks.findByKey(ref_period, unitType, unitId)

  def findById(refPeriod: Long, unitId: String): List[UnitLinks] = UnitLinks.findById(refPeriod, unitId)

  override def insert(data: UnitLinks): UnitLinks = UnitLinks.create(data)

  // Record is all PK so no update possible
  override def update(data: UnitLinks): UnitLinks = UnitLinks.save(data)

  override def delete(data: UnitLinks): Unit = data.destroy

  override def deleteAll: Unit = UnitLinks.destroyAll()

  override def count(): Long = UnitLinks.countAll()

}
