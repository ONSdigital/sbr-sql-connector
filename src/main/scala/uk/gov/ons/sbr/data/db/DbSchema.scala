package uk.gov.ons.sbr.data.db

import scalikejdbc._
import uk.gov.ons.sbr.data.model.Enterprise.autoSession

object DbSchema {

  def dropSchema(implicit session: DBSession = autoSession) = {

    UnitLinksDbTable.dropTable

    VatDbTable.dropTable

    PayeDbTable.dropTable

    CompanyDbTable.dropTable

    LegalUnitDbTable.dropTable

    EnterpriseDbTable.dropTable
  }

  def createSchema(implicit session: DBSession = autoSession) = {

    dropSchema

    EnterpriseDbTable.createTable

    LegalUnitDbTable.createTable

    CompanyDbTable.createTable

    PayeDbTable.createTable

    VatDbTable.createTable

    UnitLinksDbTable.createTable

  }


  def loadDataIntoSchema() = {

    EnterpriseDbTable.loadFromSqlFile("ent_2500_data.sql")

    LegalUnitDbTable.loadFromSqlFile("leu_2500_data.sql")

    CompanyDbTable.loadFromSqlFile("ch_2500_data.sql")

    PayeDbTable.loadFromSqlFile("paye_2500_data.sql")

    VatDbTable.loadFromSqlFile("vat_2500_data.sql")

    UnitLinksDbTable.loadFromSqlFile("unit_links_2500_data.sql")
  }

}
