package uk.gov.ons.sbr.data.db

import scalikejdbc._
import uk.gov.ons.sbr.data.model.Enterprise.autoSession

import javax.inject.Singleton

import com.typesafe.config.{Config}

object DbSchema {

  private val unitLinksTable =  UnitLinksDbTable
  private val vatTable =  VatDbTable
  private val payeTable =  PayeDbTable
  private val companyTable =  CompanyDbTable
  private val legalUnitTable =  LegalUnitDbTable
  private val enterpriseTable =  EnterpriseDbTable

  def dropSchema(implicit session: DBSession = autoSession) = {

    unitLinksTable.dropTable

    vatTable.dropTable

    payeTable.dropTable

    companyTable.dropTable

    legalUnitTable.dropTable

    enterpriseTable.dropTable
  }

  def createSchema(implicit session: DBSession = autoSession) = {

    dropSchema

    enterpriseTable.createTable

    legalUnitTable.createTable

    companyTable.createTable

    payeTable.createTable

    vatTable.createTable

    unitLinksTable.createTable

  }


  def loadDataIntoSchema(sampleDir: String) = {

    println(s"*** Loading data from $sampleDir....")

    enterpriseTable.loadFromSqlFile(s"$sampleDir/ent_2500_data.sql")

    legalUnitTable.loadFromSqlFile(s"$sampleDir/leu_2500_data.sql")

    companyTable.loadFromSqlFile(s"$sampleDir/ch_2500_data.sql")

    payeTable.loadFromSqlFile(s"$sampleDir/paye_2500_data.sql")

    vatTable.loadFromSqlFile(s"$sampleDir/vat_2500_data.sql")

    unitLinksTable.loadFromSqlFile(s"$sampleDir/unit_links_2500_data.sql")
  }

}
