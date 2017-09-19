/**
 * SbrDbService.scala
 * --------------
 * Author: websc
 * Date: 19/09/17 13:51
 * Copyright (c) 2017  Office for National Statistics
 */

package uk.gov.ons.sbr.data.service

import com.typesafe.config._
import uk.gov.ons.sbr.data.db._
import javax.inject.Singleton

import uk.gov.ons.sbr.data.model.{StatUnit, StatUnitLinks, UnitLinks}

@Singleton
class SbrDbService(dbConfigOpt: Option[Config]) {

  // Set up DB...
  // Did we receive a config?  If not, use "default".
  private val dbConfig = dbConfigOpt match {
    case Some(cfg: Config) => cfg
    case None =>   ConfigFactory.load().getConfig("db").getConfig("default")
  }
  val db = new SbrDatabase(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // Initialise DB as required
  private val initSchema: Boolean = dbConfig.getBoolean("init")
  private val loadSample: Boolean = dbConfig.getBoolean("load")
  private val sampleDir = dbConfig.getString("sample")

  val dbSchema = DbSchema

  // create tables?
  if (initSchema) dbSchema.createSchema

  if (loadSample) dbSchema.loadDataIntoSchema(sampleDir)

  // DAOs to provide service
  val entDao = EnterpriseDao
  val unitDao = LegalUnitDao
  val chDao = CompanyDao
  val payeDao = PayeDao
  val vatDao = VatDao
  val linksDao = UnitLinksDao

  // Service methods

  // Ony use this hard-coded value for demo!!!
  def defaultRefPeriod: Long = this.dbConfig.getLong("refperiod")

  // StatUnitLinks only contain IDs - this is where you search for an ID but do not yet know the unit type
  def getStatUnitLinks(ref_period: Long, unitId: String): Seq[StatUnitLinks] = {
    linksDao.findById(ref_period, unitId).map(StatUnitLinks(_))
  }

  def getStatUnitLinks(unitId: String): Seq[StatUnitLinks] = {
    getStatUnitLinks(ref_period = defaultRefPeriod, unitId)
  }

  // The StatUnit methods are for fetching specific objects (Ent, LEU, CH, PAYE, VAT) as SU hieraarchies
  def getEnterpriseAsStatUnit(ref_period: Long, entref: Long): Option[StatUnit] = {
    // construct hierarchy of StatUnits for this Enterprise
    // Get the Ent record first...
    val entSU: Option[StatUnit] = entDao.getAsStatUnit(ref_period, entref)
    // .. then get children i.e. LEUs
    val leus = unitDao.getLegalUnitsForEnterprise(ref_period, entref)
    // fetch these as SUs with nested children (CH, PAYEs, VATs)
    val leuSUs: Seq[StatUnit] = leus.map { x => getLegalUnitAsStatUnit(x.ref_period, x.ubrn) }.flatten
    // Add LEUs (if any) to children
    entSU.map { e => e.children ++= leuSUs; e }
  }

  def getEnterpriseAsStatUnit(entref: Long): Option[StatUnit] = getEnterpriseAsStatUnit(defaultRefPeriod, entref)

  // Update Enterprise (Stat Unit)
  def updateEnterpriseStatUnit(statUnit: StatUnit): StatUnit = {
    entDao.updateEntStatUnit(statUnit)
  }

  def getLegalUnitAsStatUnit(ref_period: Long, ubrn: Long): Option[StatUnit] = {
    // construct hierarchy of StatUnits for this LEU
    val leuSU = unitDao.getAsStatUnit(ref_period, ubrn)

    // get Company info for this legal unit as StatUnit (should be just one or zero)
    val coSU: Seq[StatUnit] = chDao.getCompaniesForLegalUnit(ref_period, ubrn).map(StatUnit(_))
    // Add CH (if any) to children
    leuSU.map { su => su.children ++= coSU; su }

    // Get PAYEs (if any) and add to children
    val payeSU = payeDao.getPayesForLegalUnit(ref_period, ubrn).map(StatUnit(_))
    leuSU.map { su => su.children ++= payeSU; su }

    // Get VATs (if any) and add to children
    val vatSU = vatDao.getVatsForLegalUnit(ref_period, ubrn).map(StatUnit(_))
    leuSU.map { su => su.children ++= vatSU; su }

    leuSU
  }

  def getLegalUnitAsStatUnit(ubrn: Long): Option[StatUnit] = getLegalUnitAsStatUnit(defaultRefPeriod, ubrn)

  // These Stat unit types have no children so they are easy to construct
  def getCompanyAsStatUnit(ref_period: Long, companyno: String): Option[StatUnit] = {
    chDao.getCompany(ref_period, companyno).map(StatUnit(_))
  }

  def getCompanyAsStatUnit(companyno: String): Option[StatUnit] = getCompanyAsStatUnit(defaultRefPeriod, companyno)

  def getPayeAsStatUnit(ref_period: Long, payeref: String): Option[StatUnit] = {
    payeDao.getPaye(ref_period, payeref).map(StatUnit(_))
  }

  def getPayeAsStatUnit(payeref: String): Option[StatUnit] = getPayeAsStatUnit(defaultRefPeriod, payeref)

  def getVatAsStatUnit(ref_period: Long, vatref: String): Option[StatUnit] = {
    vatDao.getVat(ref_period, vatref).map(StatUnit(_))
  }

  def getVatAsStatUnit(vatref: String): Option[StatUnit] = getVatAsStatUnit(defaultRefPeriod, vatref)

}
