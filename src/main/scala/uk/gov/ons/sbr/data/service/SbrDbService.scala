package uk.gov.ons.sbr.data.service

import com.typesafe.config._
import uk.gov.ons.sbr.data.db._
import javax.inject.Singleton

import uk.gov.ons.sbr.data.model.StatUnit

@Singleton
class SbrDbService(dbConfig: Config) {

  // Set up DB...
  val db = new SbrDatabase(dbConfig)
  val initSchema: Boolean = dbConfig.getBoolean("init")
  val loadSample: Boolean = dbConfig.getBoolean("load")

  val sampleDir = dbConfig.getString("sample")

  val dbSchema = DbSchema

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

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

  def getEnterpriseAsStatUnit(ref_period: Long, entref: Long): Option[StatUnit] = {
    // construct hierarchy of StatUnits for this Enterprise
    // Get the Ent record first...
    val entSU: Option[StatUnit] = entDao.getAsStatUnit(ref_period, entref)
    // .. then get children i.e. LEUs
    val leus= unitDao.getLegalUnitsForEnterprise(ref_period, entref)
    // fetch these as SUs with nested children (CH, PAYEs, VATs)
    val leuSUs: Seq[StatUnit] = leus.map{ x => getLegalUnitAsStatUnit(x.ref_period, x.ubrn)}.flatten
    // Add LEUs (if any) to children
    entSU.map { e => e.children ++= leuSUs; e }
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

  // These Stat unit types have no children so they are easy to construct
  def getCompanyAsStatUnit(ref_period: Long, companyno: String): Option[StatUnit] = {
    chDao.getCompany(ref_period, companyno).map(StatUnit(_))
  }

  def getPayeAsStatUnit(ref_period: Long, payeref: String): Option[StatUnit] = {
    payeDao.getPaye(ref_period, payeref).map(StatUnit(_))
  }

  def getVatAsStatUnit(ref_period: Long, vatref: String): Option[StatUnit] = {
    vatDao.getVat(ref_period, vatref).map(StatUnit(_))
  }

}
