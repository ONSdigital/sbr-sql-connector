package uk.gov.ons.sbr.data.utils

import uk.gov.ons.sbr.data.SbrDatabase
import scalikejdbc._
import javax.inject.Singleton

import uk.gov.ons.sbr.data.demo._
import uk.gov.ons.sbr.data.model.Enterprise
import uk.gov.ons.sbr.data.model.Enterprise.autoSession

object DbSchemaService {

  def dropSchema(implicit session: DBSession = autoSession) = {

    UnitLinksDbTable.dropTable

    UnitKeyDbTable.dropTable

    VatDbTable.dropTable

    PayeDbTable.dropTable

    CompanyDbTable.dropTable

    LegalUnitDbTable.dropTable

    EnterpriseDbTable.dropTable
  }

  def createSchema(implicit session: DBSession = autoSession) = {

    println("Running create schema...")

    dropSchema

    EnterpriseDbTable.createTable

    LegalUnitDbTable.createTable

    CompanyDbTable.createTable

    PayeDbTable.createTable

    VatDbTable.createTable

    UnitKeyDbTable.createTable

    UnitLinksDbTable.createTable

  }


  def loadDataIntoSchema() = {

    EnterpriseDbTable.loadFromSqlFile("ent_2500_data.sql")

    LegalUnitDbTable.loadFromSqlFile("leu_2500_data.sql")

    CompanyDbTable.loadFromSqlFile("ch_2500_data.sql")

    PayeDbTable.loadFromSqlFile("paye_2500_data.sql")

    VatDbTable.loadFromSqlFile("vat_2500_data.sql")

    UnitLinksDbTable.loadFromSqlFile("unit_links_2500_data.sql")

    // Popualte Unit Keys from the above tables
    UnitKeyDbTable.populate()
  }

}
