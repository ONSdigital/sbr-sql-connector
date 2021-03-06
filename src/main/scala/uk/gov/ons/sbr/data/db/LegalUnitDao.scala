/**
 * LegalUnitDao.scala
 * --------------
 * Author: websc
 * Date: 18/09/17 15:06
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.db

import uk.gov.ons.sbr.data.model.{Children, LegalUnit, LeuChildren, StatUnit}

object LegalUnitDao extends SbrDao[LegalUnit]{

  def getAsStatUnit(ref_period: Long, ubrn: Long): Option[StatUnit] = LegalUnit.getAsStatUnit(ref_period, ubrn)

  def getLegalUnit(ref_period: Long, ubrn: Long): Option[LegalUnit] = LegalUnit.find(ref_period, ubrn)

  def getLegalUnitsForEnterprise(ref_period: Long, entref: Long): Seq[LegalUnit] = LegalUnit.findByEnt(ref_period, entref)

  def getChildren(ref_period: Long, ubrn: Long): Children = LegalUnit.getChildren(ref_period, ubrn)

  def getLeuChildren(ref_period: Long, ubrn: Long): LeuChildren = LegalUnit.getLeuChildren(ref_period, ubrn)

  override def insert(data: LegalUnit): LegalUnit = LegalUnit.create(data)

  override def update(data: LegalUnit): LegalUnit = data.save

  override def delete(data: LegalUnit): Unit = data.destroy

  override def deleteAll: Unit = LegalUnit.destroyAll()

  override def count(): Long = LegalUnit.countAll()

}

