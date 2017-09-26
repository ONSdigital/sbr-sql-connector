
/**
 * CompanyDao.scala
 * --------------
 * Author: websc
 * Date: 19/09/17 14:10
 * Copyright (c) 2017  Office for National Statistics
 **/

package uk.gov.ons.sbr.data.db


import uk.gov.ons.sbr.data.model.Company

object CompanyDao extends SbrDao[Company]{

  // entity-specific queries

  def getCompany(ref_period: Long, companynumber: String): Option[Company] = Company.find(ref_period, companynumber)

  def getCompaniesForLegalUnit(ref_period: Long, ubrn: Long): Seq[Company] = Company.findByLegalUnit(ref_period, ubrn)

  // generic methods

  override def insert(data: Company): Company = Company.create(data)

  override def update(data: Company): Company = data.save

  override def delete(data: Company): Unit = data.destroy

  override def deleteAll: Unit = Company.destroyAll()

  override def count(): Long = Company.countAll()

}

