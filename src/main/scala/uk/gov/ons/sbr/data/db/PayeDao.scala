/**
 * PayeDao.scala
 * --------------
 * Author: websc
 * Date: 12/09/17 08:53
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.db

import uk.gov.ons.sbr.data.model.Paye

object PayeDao extends SbrDao[Paye]{

  def getPayesForLegalUnit(ref_period: Long, ubrn: Long): List[Paye] = Paye.findByLegalUnit(ref_period, ubrn)

  def getPaye(ref_period: Long, payeref: String): Option[Paye] = Paye.find(ref_period, payeref)

  override def insert(data: Paye): Paye = Paye.create(data)

  override def update(data: Paye): Paye = data.save

  override def delete(data: Paye): Unit = data.destroy

  override def deleteAll: Unit = Paye.destroyAll()

  override def count(): Long = Paye.countAll()

}

