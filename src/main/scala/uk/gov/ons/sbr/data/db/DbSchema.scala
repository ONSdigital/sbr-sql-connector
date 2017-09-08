package uk.gov.ons.sbr.data.db

import scalikejdbc._
import uk.gov.ons.sbr.data.model.Enterprise.autoSession

import javax.inject.Singleton

import com.typesafe.config.{Config}

@Singleton
class DbSchema(dbConfig: Config) {

  private val unitLinksTable = new UnitLinksDbTable(dbConfig)
  private val vatTable = new VatDbTable(dbConfig)
  private val payeTable = new PayeDbTable(dbConfig)
  private val companyTable = new CompanyDbTable(dbConfig)
  private val legalUnitTable = new LegalUnitDbTable(dbConfig)
  private val enterpriseTable = new EnterpriseDbTable(dbConfig)


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


  def loadDataIntoSchema() = {

    enterpriseTable.loadFromSqlFile("ent_2500_data.sql")

    legalUnitTable.loadFromSqlFile("leu_2500_data.sql")

    companyTable.loadFromSqlFile("ch_2500_data.sql")

    payeTable.loadFromSqlFile("paye_2500_data.sql")

    vatTable.loadFromSqlFile("vat_2500_data.sql")

    unitLinksTable.loadFromSqlFile("unit_links_2500_data.sql")
  }

}
