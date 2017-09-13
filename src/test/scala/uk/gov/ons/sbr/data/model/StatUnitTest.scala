package uk.gov.ons.sbr.data.model

import org.scalatest._

class StatUnitTest extends FlatSpec with Matchers {

  /** * TESTS START HERE ***/

  behavior of "StatUnit"

  // Enterprise

  it should "convert Enterprise to StatUnit with variables as Map" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    val ent = Enterprise(
      ref_period = refperiod,
      entref = entref,
      ent_tradingstyle = Option(s"Entity $entref"),
      ent_address1 = Some("Address 1"),
      ent_address2 = Some("Address 2"),
      ent_address3 = Some("Address 3"),
      ent_address4 = Some("Address 4"),
      ent_address5 = Some("Address 5"),
      ent_postcode = Some("AB1 2CD"),
      legalstatus = Some("L"),
      paye_jobs = Some("PAYE 123"),
      employees = Some("Emps"),
      standard_vat_turnover = Some("VAT T/O"),
      num_unique_payerefs = Some("PAYEREFS"),
      num_unique_vatrefs = Some("VATREFS"),
      contained_rep_vat_turnover = Some("CRVT")
    )

    val su: StatUnit = StatUnit(ent)

    val expectedVarMap = Map(
      "ent_tradingstyle" -> s"Entity $entref",
      "ent_address1" -> "Address 1",
      "ent_address2" -> "Address 2",
      "ent_address3" -> "Address 3",
      "ent_address4" -> "Address 4",
      "ent_address5" -> "Address 5",
      "ent_postcode" -> "AB1 2CD",
      "legalstatus" -> "L",
      "paye_jobs" -> "PAYE 123",
      "employees" -> "Emps",
      "standard_vat_turnover" -> "VAT T/O",
      "num_unique_payerefs" -> "PAYEREFS",
      "num_unique_vatrefs" -> "VATREFS",
      "contained_rep_vat_turnover" -> "CRVT"
    )

    val actualMap = su.variables

    actualMap should contain theSameElementsAs (expectedVarMap)
  }


  it should "convert Enterprise correctly to StatUnit with correct main attributes" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    val ent = Enterprise(
      ref_period = refperiod,
      entref = entref,
      ent_tradingstyle = Option(s"Entity $entref"),
      ent_address1 = Some("Address 1"),
      ent_address2 = Some("Address 2"),
      ent_address3 = Some("Address 3"),
      ent_address4 = Some("Address 4"),
      ent_address5 = Some("Address 5"),
      ent_postcode = Some("AB1 2CD"),
      legalstatus = Some("L"),
      paye_jobs = Some("PAYE 123"),
      employees = Some("Emps"),
      standard_vat_turnover = Some("VAT T/O"),
      num_unique_payerefs = Some("PAYEREFS"),
      num_unique_vatrefs = Some("VATREFS"),
      contained_rep_vat_turnover = Some("CRVT")
    )

    val su: StatUnit = StatUnit(ent)

    assert(su.refPeriod == refperiod && su.key == entref.toString && su.unitType.toString == "ENT")
  }

  it should "convert Enterprise to StatUnit and back to Enterprise correctly" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    val ent = Enterprise(
      ref_period = refperiod,
      entref = entref,
      ent_tradingstyle = Option(s"Entity $entref"),
      ent_address1 = Some("Address 1"),
      ent_address2 = Some("Address 2"),
      ent_address3 = Some("Address 3"),
      ent_address4 = Some("Address 4"),
      ent_address5 = Some("Address 5"),
      ent_postcode = Some("AB1 2CD"),
      legalstatus = Some("L"),
      paye_jobs = Some("PAYE 123"),
      employees = Some("Emps"),
      standard_vat_turnover = Some("VAT T/O"),
      num_unique_payerefs = Some("PAYEREFS"),
      num_unique_vatrefs = Some("VATREFS"),
      contained_rep_vat_turnover = Some("CRVT")
    )

    val su: StatUnit = StatUnit(ent)

    val ent2 = Enterprise(su)

    ent2 shouldBe ent
  }

  it should "not convert a non-Enterprise StatUnit back to Enterprise" in {

    val refperiod = 201708 // not default period

    val su: StatUnit = StatUnit(refperiod,"DUMMY",UnitType.LEU)
    // should not be able to convert SU to Ent
    an [IllegalArgumentException] should be thrownBy {
      Enterprise(su)
    }
  }

  // Legal Units

  it should "convert LegalUnit correctly to StatUnit with variables as Map" in {

    val entref = 100L
    val ubrn = 1234L
    val refperiod = 201708 // not default period

    val leu = LegalUnit(refperiod, ubrn, Some(s"LEU $ubrn"), Some("AB1 2CD"), Some("I"),
      Some("L"), Some("T"), Some("T/O"), Some("EB"), entref)


    val su: StatUnit = StatUnit(leu)

    val expectedVarMap = Map("employmentbands" -> "EB", "legalstatus" -> "L", "businessname" -> "LEU 1234",
      "postcode" -> "AB1 2CD", "industrycode" -> "I", "tradingstatus" -> "T", "turnover" -> "T/O")

    val actualMap = su.variables

    actualMap should contain theSameElementsAs (expectedVarMap)
  }

  it should "convert LegalUnit correctly to StatUnit with correct main attributes" in {

    val entref = 100L
    val ubrn = 1234L
    val refperiod = 201708 // not default period

    val leu = LegalUnit(refperiod, ubrn, Some(s"LEU $ubrn"), Some("AB1 2CD"), Some("I"),
      Some("L"), Some("T"), Some("T/O"), Some("EB"), entref)

    val su: StatUnit = StatUnit(leu)

    assert(su.refPeriod == refperiod && su.key == ubrn.toString && su.unitType.toString == "LEU")
  }

  // Company

  it should "convert Company correctly to StatUnit with variables as Map" in {

    val ubrn = 1234L
    val refperiod = 201708 // not default period
    val cono = "COMPANY123"


    val company = Company(
      ref_period = refperiod,
      companynumber = cono,
      ubrn = ubrn,
      companyname = Some("COMPANYNAME"),
      regaddress_careof = Some("REGADDRESS_CAREOF"),
      regaddress_pobox = Some("REGADDRESS_POBOX"),
      regaddress_addressline1 = Some("REGADDRESS_ADDRESSLINE1"),
      regaddress_addressline2 = Some("REGADDRESS_ADDRESSLINE2"),
      regaddress_posttown = Some("REGADDRESS_POSTTOWN"),
      regaddress_county = Some("REGADDRESS_COUNTY"),
      regaddress_country = Some("REGADDRESS_COUNTRY"),
      regaddress_postcode = Some("REGADDRESS_POSTCODE"),
      companycategory = Some("COMPANYCATEGORY"),
      companystatus = Some("COMPANYSTATUS"),
      countryoforigin = Some("COUNTRYOFORIGIN"),
      dissolutiondate = Some("DISSOLUTIONDATE"),
      incorporationdate = Some("INCORPORATIONDATE"),
      accounts_accountrefday = Some("ACCOUNTS_ACCOUNTREFDAY"),
      accounts_accountrefmonth = Some("ACCOUNTS_ACCOUNTREFMONTH"),
      accounts_nextduedate = Some("ACCOUNTS_NEXTDUEDATE"),
      accounts_lastmadeupdate = Some("ACCOUNTS_LASTMADEUPDATE"),
      accounts_accountcategory = Some("ACCOUNTS_ACCOUNTCATEGORY"),
      returns_nextduedate = Some("RETURNS_NEXTDUEDATE"),
      returns_lastmadeupdate = Some("RETURNS_LASTMADEUPDATE"),
      mortgages_nummortcharges = Some("MORTGAGES_NUMMORTCHARGES"),
      mortgages_nummortoutstanding = Some("MORTGAGES_NUMMORTOUTSTANDING"),
      mortgages_nummortpartsatisfied = Some("MORTGAGES_NUMMORTPARTSATISFIED"),
      mortgages_nummortsatisfied = Some("MORTGAGES_NUMMORTSATISFIED"),
      siccode_sictext_1 = Some("SICCODE_SICTEXT_1"),
      siccode_sictext_2 = Some("SICCODE_SICTEXT_2"),
      siccode_sictext_3 = Some("SICCODE_SICTEXT_3"),
      siccode_sictext_4 = Some("SICCODE_SICTEXT_4"),
      limitedpartnerships_numgenpartners = Some("LIMITEDPARTNERSHIPS_NUMGENPARTNERS"),
      limitedpartnerships_numlimpartners = Some("LIMITEDPARTNERSHIPS_NUMLIMPARTNERS"),
      uri = Some("URI"),
      previousname_1_condate = Some("PREVIOUSNAME_1_CONDATE"),
      previousname_1_companyname = Some("PREVIOUSNAME_1_COMPANYNAME"),
      previousname_2_condate = Some("PREVIOUSNAME_2_CONDATE"),
      previousname_2_companyname = Some("PREVIOUSNAME_2_COMPANYNAME"),
      previousname_3_condate = Some("PREVIOUSNAME_3_CONDATE"),
      previousname_3_companyname = Some("PREVIOUSNAME_3_COMPANYNAME"),
      previousname_4_condate = Some("PREVIOUSNAME_4_CONDATE"),
      previousname_4_companyname = Some("PREVIOUSNAME_4_COMPANYNAME"),
      previousname_5_condate = Some("PREVIOUSNAME_5_CONDATE"),
      previousname_5_companyname = Some("PREVIOUSNAME_5_COMPANYNAME"),
      previousname_6_condate = Some("PREVIOUSNAME_6_CONDATE"),
      previousname_6_companyname = Some("PREVIOUSNAME_6_COMPANYNAME"),
      previousname_7_condate = Some("PREVIOUSNAME_7_CONDATE"),
      previousname_7_companyname = Some("PREVIOUSNAME_7_COMPANYNAME"),
      previousname_8_condate = Some("PREVIOUSNAME_8_CONDATE"),
      previousname_8_companyname = Some("PREVIOUSNAME_8_COMPANYNAME"),
      previousname_9_condate = Some("PREVIOUSNAME_9_CONDATE"),
      previousname_9_companyname = Some("PREVIOUSNAME_9_COMPANYNAME"),
      previousname_10_condate = Some("PREVIOUSNAME_10_CONDATE"),
      previousname_10_companyname = Some("PREVIOUSNAME_10_COMPANYNAME"),
      confstmtnextduedate = Some("CONFSTMTNEXTDUEDATE"),
      confstmtlastmadeupdate = Some("CONFSTMTLASTMADEUPDATE")
    )

    val su: StatUnit = StatUnit(company)

    val expectedVarMap = Map(
      "companyname" -> "COMPANYNAME", "regaddress_careof" -> "REGADDRESS_CAREOF",
      "regaddress_pobox" -> "REGADDRESS_POBOX", "regaddress_addressline1" -> "REGADDRESS_ADDRESSLINE1",
      "regaddress_addressline2" -> "REGADDRESS_ADDRESSLINE2", "regaddress_posttown" -> "REGADDRESS_POSTTOWN",
      "regaddress_county" -> "REGADDRESS_COUNTY", "regaddress_country" -> "REGADDRESS_COUNTRY",
      "regaddress_postcode" -> "REGADDRESS_POSTCODE", "companycategory" -> "COMPANYCATEGORY",
      "companystatus" -> "COMPANYSTATUS", "countryoforigin" -> "COUNTRYOFORIGIN",
      "dissolutiondate" -> "DISSOLUTIONDATE", "incorporationdate" -> "INCORPORATIONDATE",
      "accounts_accountrefday" -> "ACCOUNTS_ACCOUNTREFDAY", "accounts_accountrefmonth" -> "ACCOUNTS_ACCOUNTREFMONTH",
      "accounts_nextduedate" -> "ACCOUNTS_NEXTDUEDATE", "accounts_lastmadeupdate" -> "ACCOUNTS_LASTMADEUPDATE",
      "accounts_accountcategory" -> "ACCOUNTS_ACCOUNTCATEGORY", "returns_nextduedate" -> "RETURNS_NEXTDUEDATE",
      "returns_lastmadeupdate" -> "RETURNS_LASTMADEUPDATE", "mortgages_nummortcharges" -> "MORTGAGES_NUMMORTCHARGES",
      "mortgages_nummortoutstanding" -> "MORTGAGES_NUMMORTOUTSTANDING",
      "mortgages_nummortpartsatisfied" -> "MORTGAGES_NUMMORTPARTSATISFIED",
      "mortgages_nummortsatisfied" -> "MORTGAGES_NUMMORTSATISFIED", "siccode_sictext_1" -> "SICCODE_SICTEXT_1",
      "siccode_sictext_2" -> "SICCODE_SICTEXT_2", "siccode_sictext_3" -> "SICCODE_SICTEXT_3",
      "siccode_sictext_4" -> "SICCODE_SICTEXT_4",
      "limitedpartnerships_numgenpartners" -> "LIMITEDPARTNERSHIPS_NUMGENPARTNERS",
      "limitedpartnerships_numlimpartners" -> "LIMITEDPARTNERSHIPS_NUMLIMPARTNERS",
      "uri" -> "URI", "previousname_1_condate" -> "PREVIOUSNAME_1_CONDATE",
      "previousname_1_companyname" -> "PREVIOUSNAME_1_COMPANYNAME",
      "previousname_2_condate" -> "PREVIOUSNAME_2_CONDATE",
      "previousname_2_companyname" -> "PREVIOUSNAME_2_COMPANYNAME",
      "previousname_3_condate" -> "PREVIOUSNAME_3_CONDATE",
      "previousname_3_companyname" -> "PREVIOUSNAME_3_COMPANYNAME",
      "previousname_4_condate" -> "PREVIOUSNAME_4_CONDATE",
      "previousname_4_companyname" -> "PREVIOUSNAME_4_COMPANYNAME",
      "previousname_5_condate" -> "PREVIOUSNAME_5_CONDATE",
      "previousname_5_companyname" -> "PREVIOUSNAME_5_COMPANYNAME",
      "previousname_6_condate" -> "PREVIOUSNAME_6_CONDATE",
      "previousname_6_companyname" -> "PREVIOUSNAME_6_COMPANYNAME",
      "previousname_7_condate" -> "PREVIOUSNAME_7_CONDATE",
      "previousname_7_companyname" -> "PREVIOUSNAME_7_COMPANYNAME",
      "previousname_8_condate" -> "PREVIOUSNAME_8_CONDATE",
      "previousname_8_companyname" -> "PREVIOUSNAME_8_COMPANYNAME",
      "previousname_9_condate" -> "PREVIOUSNAME_9_CONDATE",
      "previousname_9_companyname" -> "PREVIOUSNAME_9_COMPANYNAME",
      "previousname_10_condate" -> "PREVIOUSNAME_10_CONDATE",
      "previousname_10_companyname" -> "PREVIOUSNAME_10_COMPANYNAME",
      "confstmtnextduedate" -> "CONFSTMTNEXTDUEDATE",
      "confstmtlastmadeupdate" -> "CONFSTMTLASTMADEUPDATE"
    )

    val actualMap = su.variables

    actualMap should contain theSameElementsAs (expectedVarMap)
  }

  it should "convert Company correctly to StatUnit with correct main attributes" in {

    val entref = 100L
    val ubrn = 1234L
    val refperiod = 201708 // not default period

    val cono = "COMPANY123"


    val company = Company(
      ref_period = refperiod,
      companynumber = cono,
      ubrn = ubrn,
      companyname = Some("COMPANYNAME"),
      regaddress_careof = Some("REGADDRESS_CAREOF"),
      regaddress_pobox = Some("REGADDRESS_POBOX"),
      regaddress_addressline1 = Some("REGADDRESS_ADDRESSLINE1"),
      regaddress_addressline2 = Some("REGADDRESS_ADDRESSLINE2"),
      regaddress_posttown = Some("REGADDRESS_POSTTOWN"),
      regaddress_county = Some("REGADDRESS_COUNTY"),
      regaddress_country = Some("REGADDRESS_COUNTRY"),
      regaddress_postcode = Some("REGADDRESS_POSTCODE"),
      companycategory = Some("COMPANYCATEGORY"),
      companystatus = Some("COMPANYSTATUS"),
      countryoforigin = Some("COUNTRYOFORIGIN"),
      dissolutiondate = Some("DISSOLUTIONDATE"),
      incorporationdate = Some("INCORPORATIONDATE"),
      accounts_accountrefday = Some("ACCOUNTS_ACCOUNTREFDAY"),
      accounts_accountrefmonth = Some("ACCOUNTS_ACCOUNTREFMONTH"),
      accounts_nextduedate = Some("ACCOUNTS_NEXTDUEDATE"),
      accounts_lastmadeupdate = Some("ACCOUNTS_LASTMADEUPDATE"),
      accounts_accountcategory = Some("ACCOUNTS_ACCOUNTCATEGORY"),
      returns_nextduedate = Some("RETURNS_NEXTDUEDATE"),
      returns_lastmadeupdate = Some("RETURNS_LASTMADEUPDATE"),
      mortgages_nummortcharges = Some("MORTGAGES_NUMMORTCHARGES"),
      mortgages_nummortoutstanding = Some("MORTGAGES_NUMMORTOUTSTANDING"),
      mortgages_nummortpartsatisfied = Some("MORTGAGES_NUMMORTPARTSATISFIED"),
      mortgages_nummortsatisfied = Some("MORTGAGES_NUMMORTSATISFIED"),
      siccode_sictext_1 = Some("SICCODE_SICTEXT_1"),
      siccode_sictext_2 = Some("SICCODE_SICTEXT_2"),
      siccode_sictext_3 = Some("SICCODE_SICTEXT_3"),
      siccode_sictext_4 = Some("SICCODE_SICTEXT_4"),
      limitedpartnerships_numgenpartners = Some("LIMITEDPARTNERSHIPS_NUMGENPARTNERS"),
      limitedpartnerships_numlimpartners = Some("LIMITEDPARTNERSHIPS_NUMLIMPARTNERS"),
      uri = Some("URI"),
      previousname_1_condate = Some("PREVIOUSNAME_1_CONDATE"),
      previousname_1_companyname = Some("PREVIOUSNAME_1_COMPANYNAME"),
      previousname_2_condate = Some("PREVIOUSNAME_2_CONDATE"),
      previousname_2_companyname = Some("PREVIOUSNAME_2_COMPANYNAME"),
      previousname_3_condate = Some("PREVIOUSNAME_3_CONDATE"),
      previousname_3_companyname = Some("PREVIOUSNAME_3_COMPANYNAME"),
      previousname_4_condate = Some("PREVIOUSNAME_4_CONDATE"),
      previousname_4_companyname = Some("PREVIOUSNAME_4_COMPANYNAME"),
      previousname_5_condate = Some("PREVIOUSNAME_5_CONDATE"),
      previousname_5_companyname = Some("PREVIOUSNAME_5_COMPANYNAME"),
      previousname_6_condate = Some("PREVIOUSNAME_6_CONDATE"),
      previousname_6_companyname = Some("PREVIOUSNAME_6_COMPANYNAME"),
      previousname_7_condate = Some("PREVIOUSNAME_7_CONDATE"),
      previousname_7_companyname = Some("PREVIOUSNAME_7_COMPANYNAME"),
      previousname_8_condate = Some("PREVIOUSNAME_8_CONDATE"),
      previousname_8_companyname = Some("PREVIOUSNAME_8_COMPANYNAME"),
      previousname_9_condate = Some("PREVIOUSNAME_9_CONDATE"),
      previousname_9_companyname = Some("PREVIOUSNAME_9_COMPANYNAME"),
      previousname_10_condate = Some("PREVIOUSNAME_10_CONDATE"),
      previousname_10_companyname = Some("PREVIOUSNAME_10_COMPANYNAME"),
      confstmtnextduedate = Some("CONFSTMTNEXTDUEDATE"),
      confstmtlastmadeupdate = Some("CONFSTMTLASTMADEUPDATE")
    )

    val su: StatUnit = StatUnit(company)

    assert(su.refPeriod == refperiod && su.key == cono && su.unitType.toString == "CH")
  }

  // PAYE

  it should "convert PAYE correctly to StatUnit with variables as Map" in {

    val ubrn = 1234L
    val refperiod = 201708 // not default period
    val payeref = "PAYE123"

    val data = Paye(
      ref_period = refperiod, payeref = payeref, deathcode = Some("DEATHCODE"), birthdate = Some("BIRTHDATE"),
      deathdate = Some("DEATHDATE"), mfullemp = Some("MFULLEMP"), msubemp = Some("MSUBEMP"),
      ffullemp = Some("FFULLEMP"), fsubemp = Some("FSUBEMP"), unclemp = Some("UNCLEMP"),
      unclsubemp = Some("UNCLSUBEMP"), dec_jobs = Some("DEC_JOBS"), mar_jobs = Some("MAR_JOBS"),
      june_jobs = Some("JUNE_JOBS"), sept_jobs = Some("SEPT_JOBS"), jobs_lastupd = Some("JOBS_LASTUPD"),
      status = Some("STATUS"), prevpaye = Some("PREVPAYE"), employer_cat = Some("EMPLOYER_CAT"),
      stc = Some("STC"), crn = Some("CRN"), actiondate = Some("ACTIONDATE"),
      addressref = Some("ADDRESSREF"), marker = Some("MARKER"), inqcode = Some("INQCODE"),
      name1 = Some("NAME1"), name2 = Some("NAME2"), name3 = Some("NAME3"),
      tradstyle1 = Some("TRADSTYLE1"), tradstyle2 = Some("TRADSTYLE2"), tradstyle3 = Some("TRADSTYLE3"),
      address1 = Some("ADDRESS1"), address2 = Some("ADDRESS2"), address3 = Some("ADDRESS3"),
      address4 = Some("ADDRESS4"), address5 = Some("ADDRESS5"), postcode = Some("POSTCODE"), mkr = Some("MKR"),
      ubrn = ubrn
    )

    val su: StatUnit = StatUnit(data)

    val expectedVarMap = Map(
      "deathcode" -> "DEATHCODE", "birthdate" -> "BIRTHDATE", "deathdate" -> "DEATHDATE", "mfullemp" -> "MFULLEMP",
      "msubemp" -> "MSUBEMP", "ffullemp" -> "FFULLEMP", "fsubemp" -> "FSUBEMP", "unclemp" -> "UNCLEMP",
      "unclsubemp" -> "UNCLSUBEMP", "dec_jobs" -> "DEC_JOBS", "mar_jobs" -> "MAR_JOBS", "june_jobs" -> "JUNE_JOBS",
      "sept_jobs" -> "SEPT_JOBS", "jobs_lastupd" -> "JOBS_LASTUPD", "status" -> "STATUS", "prevpaye" -> "PREVPAYE",
      "employer_cat" -> "EMPLOYER_CAT", "stc" -> "STC", "crn" -> "CRN", "actiondate" -> "ACTIONDATE",
      "addressref" -> "ADDRESSREF", "marker" -> "MARKER", "inqcode" -> "INQCODE", "name1" -> "NAME1",
      "name2" -> "NAME2", "name3" -> "NAME3", "tradstyle1" -> "TRADSTYLE1", "tradstyle2" -> "TRADSTYLE2",
      "tradstyle3" -> "TRADSTYLE3", "address1" -> "ADDRESS1", "address2" -> "ADDRESS2", "address3" -> "ADDRESS3",
      "address4" -> "ADDRESS4", "address5" -> "ADDRESS5", "postcode" -> "POSTCODE", "mkr" -> "MKR"
    )

    val actualMap = su.variables

    actualMap should contain theSameElementsAs (expectedVarMap)
  }

  it should "convert PAYE correctly to StatUnit with correct main attributes" in {

    val ubrn = 1234L
    val refperiod = 201708 // not default period
    val payeref = "PAYE123"

    val data = Paye(
      ref_period = refperiod, payeref = payeref, deathcode = Some("DEATHCODE"), birthdate = Some("BIRTHDATE"),
      deathdate = Some("DEATHDATE"), mfullemp = Some("MFULLEMP"), msubemp = Some("MSUBEMP"),
      ffullemp = Some("FFULLEMP"), fsubemp = Some("FSUBEMP"), unclemp = Some("UNCLEMP"),
      unclsubemp = Some("UNCLSUBEMP"), dec_jobs = Some("DEC_JOBS"), mar_jobs = Some("MAR_JOBS"),
      june_jobs = Some("JUNE_JOBS"), sept_jobs = Some("SEPT_JOBS"), jobs_lastupd = Some("JOBS_LASTUPD"),
      status = Some("STATUS"), prevpaye = Some("PREVPAYE"), employer_cat = Some("EMPLOYER_CAT"),
      stc = Some("STC"), crn = Some("CRN"), actiondate = Some("ACTIONDATE"),
      addressref = Some("ADDRESSREF"), marker = Some("MARKER"), inqcode = Some("INQCODE"),
      name1 = Some("NAME1"), name2 = Some("NAME2"), name3 = Some("NAME3"),
      tradstyle1 = Some("TRADSTYLE1"), tradstyle2 = Some("TRADSTYLE2"), tradstyle3 = Some("TRADSTYLE3"),
      address1 = Some("ADDRESS1"), address2 = Some("ADDRESS2"), address3 = Some("ADDRESS3"),
      address4 = Some("ADDRESS4"), address5 = Some("ADDRESS5"), postcode = Some("POSTCODE"), mkr = Some("MKR"),
      ubrn = ubrn
    )

    val su: StatUnit = StatUnit(data)

    assert(su.refPeriod == refperiod && su.key == payeref && su.unitType.toString == "PAYE")

  }

  // VAT

  it should "convert VAT correctly to StatUnit with variables as Map" in {

    val ubrn = 1234L
    val refperiod = 201708 // not default period
    val vatref = "VAT123"

    val data = Vat(
      ref_period = refperiod, vatref = vatref, deathcode = Some("DEATHCODE"), birthdate = Some("BIRTHDATE"),
      deathdate = Some("DEATHDATE"), sic92 = Some("SIC92"), turnover = Some("TURNOVER"),
      turnover_date = Some("TURNOVER_DATE"), record_type = Some("RECORD_TYPE"),
      status = Some("STATUS"), actiondate = Some("ACTIONDATE"), crn = Some("CRN"), marker = Some("MARKER"),
      addressref = Some("ADDRESSREF"), inqcode = Some("INQCODE"), name1 = Some("NAME1"), name2 = Some("NAME2"),
      name3 = Some("NAME3"), tradstyle1 = Some("TRADSTYLE1"), tradstyle2 = Some("TRADSTYLE2"),
      tradstyle3 = Some("TRADSTYLE3"), address1 = Some("ADDRESS1"), address2 = Some("ADDRESS2"),
      address3 = Some("ADDRESS3"), address4 = Some("ADDRESS4"), address5 = Some("ADDRESS5"),
      postcode = Some("POSTCODE"), mkr = Some("MKR"), ubrn = ubrn
    )

    val su: StatUnit = StatUnit(data)

    val expectedVarMap = Map(
      "deathcode" -> "DEATHCODE", "birthdate" -> "BIRTHDATE", "deathdate" -> "DEATHDATE", "sic92" -> "SIC92",
      "turnover" -> "TURNOVER", "turnover_date" -> "TURNOVER_DATE", "record_type" -> "RECORD_TYPE",
      "status" -> "STATUS", "actiondate" -> "ACTIONDATE", "crn" -> "CRN", "marker" -> "MARKER",
      "addressref" -> "ADDRESSREF", "inqcode" -> "INQCODE", "name1" -> "NAME1", "name2" -> "NAME2",
      "name3" -> "NAME3", "tradstyle1" -> "TRADSTYLE1", "tradstyle2" -> "TRADSTYLE2",
      "tradstyle3" -> "TRADSTYLE3", "address1" -> "ADDRESS1", "address2" -> "ADDRESS2",
      "address3" -> "ADDRESS3", "address4" -> "ADDRESS4", "address5" -> "ADDRESS5",
      "postcode" -> "POSTCODE", "mkr" -> "MKR"
    )

    val actualMap = su.variables

    actualMap should contain theSameElementsAs (expectedVarMap)
  }

  it should "convert VAT correctly to StatUnit with correct main attributes" in {

    val ubrn = 1234L
    val refperiod = 201708 // not default period
    val vatref = "VAT123"

    val data = Vat(
      ref_period = refperiod, vatref = vatref, deathcode = Some("DEATHCODE"), birthdate = Some("BIRTHDATE"),
      deathdate = Some("DEATHDATE"), sic92 = Some("SIC92"), turnover = Some("TURNOVER"),
      turnover_date = Some("TURNOVER_DATE"), record_type = Some("RECORD_TYPE"),
      status = Some("STATUS"), actiondate = Some("ACTIONDATE"), crn = Some("CRN"), marker = Some("MARKER"),
      addressref = Some("ADDRESSREF"), inqcode = Some("INQCODE"), name1 = Some("NAME1"), name2 = Some("NAME2"),
      name3 = Some("NAME3"), tradstyle1 = Some("TRADSTYLE1"), tradstyle2 = Some("TRADSTYLE2"),
      tradstyle3 = Some("TRADSTYLE3"), address1 = Some("ADDRESS1"), address2 = Some("ADDRESS2"),
      address3 = Some("ADDRESS3"), address4 = Some("ADDRESS4"), address5 = Some("ADDRESS5"),
      postcode = Some("POSTCODE"), mkr = Some("MKR"), ubrn = ubrn
    )

    val su: StatUnit = StatUnit(data)

    assert(su.refPeriod == refperiod && su.key == vatref && su.unitType.toString == "VAT")
  }

}
