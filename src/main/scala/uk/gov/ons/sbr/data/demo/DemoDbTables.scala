package uk.gov.ons.sbr.data.demo

import com.typesafe.config.ConfigFactory
import scalikejdbc.{DBSession, _}
import uk.gov.ons.sbr.data.SbrDatabase
import uk.gov.ons.sbr.data.model.Enterprise.autoSession

trait DemoDbTable {

  // Get demo DB file locations
  val config = ConfigFactory.load()
  val demoDbDirs = config.getConfig("db").getConfig("demo").getConfig("data")
  val ddlDir = demoDbDirs.getString("ddl")
  val insertsDir = demoDbDirs.getString("inserts")

  def dropTable(implicit session: DBSession = autoSession)

  def createTable(implicit session: DBSession = autoSession)

  def loadFromSqlFile(insertsFile: String)(implicit session: DBSession = autoSession) = {
  //TODO *** no error handling - fix this!!!
    import scala.io.Source

    val filename = s"$insertsDir/$insertsFile"
    for (line <- Source.fromFile(filename).getLines) {
      val sql = line.trim
      if (sql != "")
        SQL(sql).execute.apply()
    }
  }
}

object EnterpriseDbTable extends DemoDbTable{

  def dropTable(implicit session: DBSession = autoSession) =
    sql"""DROP TABLE IF EXISTS ent_2500 CASCADE""".execute.apply()

   def createTable(implicit session: DBSession = autoSession) = sql"""
    CREATE TABLE ent_2500
    (
        ref_period bigint NOT NULL,
        entref bigint NOT NULL,
        ent_tradingstyle TEXT,
        ent_address1 TEXT,
        ent_address2 TEXT,
        ent_address3 TEXT,
        ent_address4 TEXT,
        ent_address5 TEXT,
        ent_postcode TEXT,
        legalstatus TEXT,
        paye_jobs TEXT,
        employees TEXT,
        standard_vat_turnover TEXT,
        num_unique_payerefs bigint,
        num_unique_vatrefs bigint,
        contained_rep_vat_turnover TEXT,
        CONSTRAINT ent_2500_pkey PRIMARY KEY (ref_period, entref)
    )
    """.execute.apply()
}




object LegalUnitDbTable extends DemoDbTable{

  def dropTable(implicit session: DBSession = autoSession) =
    sql"""DROP TABLE IF EXISTS leu_2500 CASCADE""".execute.apply()

  def createTable(implicit session: DBSession = autoSession) = sql"""
  CREATE TABLE leu_2500
   (
        ref_period integer NOT NULL,
        ubrn bigint NOT NULL,
        businessname TEXT ,
        postcode TEXT ,
        industrycode TEXT ,
        legalstatus TEXT ,
        tradingstatus TEXT ,
        turnover TEXT ,
        employmentbands TEXT ,
        entref bigint,
        CONSTRAINT leu_2500_pkey PRIMARY KEY (ref_period, ubrn),
        CONSTRAINT leu_ent_fk FOREIGN KEY (ref_period, entref)
            REFERENCES ent_2500 (ref_period, entref)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    )
    """.execute.apply()
}




object CompanyDbTable extends DemoDbTable{

  def dropTable(implicit session: DBSession = autoSession) =
    sql"""DROP TABLE IF EXISTS ch_2500 CASCADE""".execute.apply()

  def createTable(implicit session: DBSession = autoSession) = sql"""
    CREATE TABLE ch_2500
    (
    ref_period integer NOT NULL,
    companynumber VARCHAR(12)  NOT NULL,
    companyname TEXT ,
    regaddress_careof TEXT ,
    regaddress_pobox TEXT ,
    regaddress_addressline1 TEXT ,
    regaddress_addressline2 TEXT ,
    regaddress_posttown TEXT ,
    regaddress_county TEXT ,
    regaddress_country TEXT ,
    regaddress_postcode TEXT ,
    companycategory TEXT ,
    companystatus TEXT ,
    countryoforigin TEXT ,
    dissolutiondate TEXT ,
    incorporationdate TEXT ,
    accounts_accountrefday TEXT ,
    accounts_accountrefmonth TEXT ,
    accounts_nextduedate TEXT ,
    accounts_lastmadeupdate TEXT ,
    accounts_accountcategory TEXT ,
    returns_nextduedate TEXT ,
    returns_lastmadeupdate TEXT ,
    mortgages_nummortcharges TEXT ,
    mortgages_nummortoutstanding TEXT ,
    mortgages_nummortpartsatisfied TEXT ,
    mortgages_nummortsatisfied TEXT ,
    siccode_sictext_1 TEXT ,
    siccode_sictext_2 TEXT ,
    siccode_sictext_3 TEXT ,
    siccode_sictext_4 TEXT ,
    limitedpartnerships_numgenpartners TEXT ,
    limitedpartnerships_numlimpartners TEXT ,
    uri TEXT ,
    previousname_1_condate TEXT ,
    previousname_1_companyname TEXT ,
    previousname_2_condate TEXT ,
    previousname_2_companyname TEXT ,
    previousname_3_condate TEXT ,
    previousname_3_companyname TEXT ,
    previousname_4_condate TEXT ,
    previousname_4_companyname TEXT ,
    previousname_5_condate TEXT ,
    previousname_5_companyname TEXT ,
    previousname_6_condate TEXT ,
    previousname_6_companyname TEXT ,
    previousname_7_condate TEXT ,
    previousname_7_companyname TEXT ,
    previousname_8_condate TEXT ,
    previousname_8_companyname TEXT ,
    previousname_9_condate TEXT ,
    previousname_9_companyname TEXT ,
    previousname_10_condate TEXT ,
    previousname_10_companyname TEXT ,
    confstmtnextduedate TEXT ,
    confstmtlastmadeupdate TEXT ,
    ubrn bigint,
    CONSTRAINT ch_2500_pkey PRIMARY KEY (ref_period, companynumber),
    CONSTRAINT ch_leu_fk FOREIGN KEY (ref_period, ubrn)
    REFERENCES leu_2500 (ref_period, ubrn)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    )
    """.execute.apply()
}

object VatDbTable extends DemoDbTable{

  def dropTable(implicit session: DBSession = autoSession) =
    sql"""DROP TABLE IF EXISTS vat_2500 CASCADE""".execute.apply()

  def createTable(implicit session: DBSession = autoSession) = sql"""
  CREATE TABLE vat_2500
   (
      ref_period integer NOT NULL,
        vatref VARCHAR(40)  NOT NULL,
        deathcode TEXT ,
        birthdate TEXT ,
        deathdate TEXT ,
        sic92 TEXT ,
        turnover TEXT ,
        turnover_date TEXT ,
        record_type TEXT ,
        status TEXT ,
        actiondate TEXT ,
        crn TEXT ,
        marker TEXT ,
        addressref TEXT ,
        inqcode TEXT ,
        name1 TEXT ,
        name2 TEXT ,
        name3 TEXT ,
        tradstyle1 TEXT ,
        tradstyle2 TEXT ,
        tradstyle3 TEXT ,
        address1 TEXT ,
        address2 TEXT ,
        address3 TEXT ,
        address4 TEXT ,
        address5 TEXT ,
        postcode TEXT ,
        mkr TEXT ,
        ubrn bigint,
        CONSTRAINT vat_2500_pkey PRIMARY KEY (ref_period, vatref),
        CONSTRAINT vat_leu_fk FOREIGN KEY (ref_period, ubrn)
            REFERENCES leu_2500 (ref_period, ubrn)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    )
    """.execute.apply()
}


object PayeDbTable extends DemoDbTable{

  def dropTable(implicit session: DBSession = autoSession) =
    sql"""DROP TABLE IF EXISTS paye_2500 CASCADE""".execute.apply()

  def createTable(implicit session: DBSession = autoSession) = sql"""
  CREATE TABLE paye_2500
   (
    ref_period integer NOT NULL,
    payeref VARCHAR(40)  NOT NULL,
    deathcode TEXT ,
    birthdate TEXT ,
    deathdate TEXT ,
    mfullemp TEXT ,
    msubemp TEXT ,
    ffullemp TEXT ,
    fsubemp TEXT ,
    unclemp TEXT ,
    unclsubemp TEXT ,
    dec_jobs TEXT ,
    mar_jobs TEXT ,
    june_jobs TEXT ,
    sept_jobs TEXT ,
    jobs_lastupd TEXT ,
    status TEXT ,
    prevpaye TEXT ,
    employer_cat TEXT ,
    stc TEXT ,
    crn TEXT ,
    actiondate TEXT ,
    addressref TEXT ,
    marker TEXT ,
    inqcode TEXT ,
    name1 TEXT ,
    name2 TEXT ,
    name3 TEXT ,
    tradstyle1 TEXT ,
    tradstyle2 TEXT ,
    tradstyle3 TEXT ,
    address1 TEXT ,
    address2 TEXT ,
    address3 TEXT ,
    address4 TEXT ,
    address5 TEXT ,
    postcode TEXT ,
    mkr TEXT ,
    ubrn bigint,
    CONSTRAINT paye_2500_pkey PRIMARY KEY (ref_period, payeref),
    CONSTRAINT paye_leu_fk FOREIGN KEY (ref_period, ubrn)
        REFERENCES leu_2500 (ref_period, ubrn)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
    )
    """.execute.apply()
}


object UnitKeyDbTable extends DemoDbTable{

  def dropTable(implicit session: DBSession = autoSession) =
    sql"""DROP TABLE IF EXISTS unit_2500 CASCADE""".execute.apply()

  def createTable(implicit session: DBSession = autoSession) = sql"""
  CREATE TABLE unit_2500
   (
    ref_period bigint NOT NULL,
    unit_type VARCHAR(10)  NOT NULL,
    unit_id VARCHAR(400)  NOT NULL,
    CONSTRAINT units_2500_pkey PRIMARY KEY (ref_period, unit_type, unit_id)
    )
    """.execute.apply()

  def populate()(implicit session: DBSession = autoSession) = {
    // Populate unit keys from actual data in other tables
    sql"""INSERT INTO unit_2500 (ref_period, unit_type, unit_id)
      SELECT ref_period, 'ENT', entref FROM ent_2500
      """.execute.apply()
    sql"""INSERT INTO unit_2500 (ref_period, unit_type, unit_id)
      SELECT ref_period, 'LEU', ubrn FROM leu_2500
      """.execute.apply()

    sql"""INSERT INTO unit_2500 (ref_period, unit_type, unit_id)
      SELECT ref_period, 'CH', companynumber FROM ch_2500
      """.execute.apply()

    sql"""INSERT INTO unit_2500 (ref_period, unit_type, unit_id)
      SELECT ref_period, 'PAYE', payeref FROM paye_2500
      """.execute.apply()

    sql"""INSERT INTO unit_2500 (ref_period, unit_type, unit_id)
      SELECT ref_period, 'VAT', vatref FROM vat_2500
      """.execute.apply()
  }
}


object UnitLinksDbTable extends DemoDbTable{

  def dropTable(implicit session: DBSession = autoSession) =
    sql"""DROP TABLE IF EXISTS unit_links_2500 CASCADE""".execute.apply()

  def createTable(implicit session: DBSession = autoSession) = sql"""
  CREATE TABLE unit_links_2500
   (
       ref_period bigint NOT NULL,
       unit_type character varying(10) NOT NULL,
       unit_id character varying(100) NOT NULL,
       p_ent BIGINT,
       p_leu BIGINT,
       children TEXT,
       CONSTRAINT unit_links_2500_pkey PRIMARY KEY (ref_period, unit_type, unit_id)
   )
    """.execute.apply()

}
