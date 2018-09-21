/**
 * DbSchema.scala
 * --------------
 * Author: websc
 * Date: 19/09/17 13:46
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.db

import scalikejdbc._
import uk.gov.ons.sbr.data.model.Enterprise.autoSession

object DbSchema {

  private val unitLinksTable =  UnitLinksDbTable
  private val vatTable =  VatDbTable
  private val payeTable =  PayeDbTable
  private val companyTable =  CompanyDbTable
  private val legalUnitTable =  LegalUnitDbTable
  private val enterpriseTable =  EnterpriseDbTable

  def dropSchema(implicit session: DBSession = autoSession): Boolean = {

    unitLinksTable.dropTable

    vatTable.dropTable

    payeTable.dropTable

    companyTable.dropTable

    legalUnitTable.dropTable

    enterpriseTable.dropTable
  }

  def createSchema(implicit session: DBSession = autoSession): Unit = {

    dropSchema

    enterpriseTable.createTable

    legalUnitTable.createTable

    companyTable.createTable

    payeTable.createTable

    vatTable.createTable

    unitLinksTable.createTable

  }


  def loadDataIntoSchema(sampleDir: String): Unit = {

    enterpriseTable.loadFromSqlFile(s"$sampleDir/ent_2500_data.sql")

    legalUnitTable.loadFromSqlFile(s"$sampleDir/leu_2500_data.sql")

    companyTable.loadFromSqlFile(s"$sampleDir/ch_2500_data.sql")

    payeTable.loadFromSqlFile(s"$sampleDir/paye_2500_data.sql")

    vatTable.loadFromSqlFile(s"$sampleDir/vat_2500_data.sql")

    unitLinksTable.loadFromSqlFile(s"$sampleDir/unit_links_2500_data.sql")
  }

}
