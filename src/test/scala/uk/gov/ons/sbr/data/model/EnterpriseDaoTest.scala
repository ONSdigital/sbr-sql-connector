package uk.gov.ons.sbr.data.model

import com.typesafe.config.ConfigFactory
import org.scalatest._
import uk.gov.ons.sbr.data.db.{DbSchemaService, SbrDatabase}

class EnterpriseDaoTest extends FlatSpec with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {

  // Set up DB...
  val config = ConfigFactory.load()
  val dbConfig = config.getConfig("db").getConfig("test")
  val db = new SbrDatabase(dbConfig)

  // Get implicit session for voodoo with DB operations below
  implicit val session = db.session

  // Use same Repo object for all tests
  val entRepo = EnterpriseDao

  override def beforeAll(): Unit = {
    super.beforeAll()
    // create DB schema (once only)
    DbSchemaService.createSchema
  }

  override def afterAll(): Unit = {
    super.afterAll()
    // drop DB schema
    DbSchemaService.dropSchema
  }

  // delete all Enterprises between tests
  override def beforeEach(): Unit = {
    super.beforeEach()
    entRepo.deleteAll
  }


  /** * TESTS START HERE ***/


  behavior of "EnterpriseRepo"


  it should "insert new Enterprise and query it correctly with Ref Period" in {

    val entref = 100L
    val refperiod = 201708 // not default period

    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val inserted: Enterprise = entRepo.insert(ent)

    val fetched = entRepo.getEnterprise(refperiod, entref)

    fetched shouldBe (Some(inserted))

    // delete record
    inserted.destroy()

  }


  it should "insert new Enterprise and delete it correctly" in {

    val entref = 101L
    val refperiod = 201706

    // assume insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val inserted: Enterprise = entRepo.insert(ent)
    // delete new record
    inserted.destroy()
    // see if it still exists (query works - tested elsewhere)
    val fetched = entRepo.getEnterprise(refperiod, entref)

    fetched shouldBe (None)

  }

  it should "query specific Enterprise correctly from several records" in {

    // insert dummy records
    val numEnts = 10
    val rp = 201706
    val ids = (1 to numEnts)
    ids foreach { id =>
      val ent = Enterprise(ref_period = rp, entref = id, ent_tradingstyle = Option(s"Entity $id"))
      entRepo.insert(ent)
    }

    // query a random record back for an ID between 1 and numEnts (watch out for 0 coming back from random gen)
    val r = scala.util.Random
    val qid: Long = r.nextInt(numEnts) match {
      case 0 => 1
      case n => n
    }

    // fetch the data
    val fetched: Option[Enterprise] = entRepo.getEnterprise(rp, qid)

    val expectedName = Some(s"Entity $qid")
    // Remember name is an option as well
    val actualName = fetched.map(_.ent_tradingstyle).flatten

    actualName shouldBe expectedName

    entRepo.deleteAll

  }

  it should "insert new Enterprise and update it correctly" in {

    val entref = 101L
    val refperiod = 201706

    // assume insert works because we test it elsewhere
    val ent = Enterprise(ref_period = refperiod, entref = entref, ent_tradingstyle = Option(s"Entity $entref"))
    val inserted: Enterprise = entRepo.insert(ent)
    // update record
    val newName = ent.ent_tradingstyle.map(_ + " MODIFIED")
    inserted.copy(ent_tradingstyle = newName).save

    // see if we can fetch correct new name (query works - tested elsewhere)
    val fetched = entRepo.getEnterprise(refperiod, entref)
    val fetchedName = fetched.map(_.ent_tradingstyle).flatten

    fetchedName shouldBe newName

    inserted.destroy()

  }

  it should "count Enterprise records correctly" in {

    // insert dummy records
    val numEnts = 10
    val rp = 201706
    val ids = (1 to numEnts)
    ids foreach { id =>
      val ent = Enterprise(ref_period = rp, entref = id, ent_tradingstyle = Option(s"Entity $id"))
      entRepo.insert(ent)
    }

    // Now see if count returns correct number
    val counted = entRepo.count()

    counted shouldBe numEnts

    entRepo.deleteAll
  }

  it should "delete all Enterprise records correctly" in {

    // insert dummy records
    val numEnts = 10
    val rp = 201706
    val ids = (1 to numEnts)
    ids foreach { id =>
      val ent = Enterprise(ref_period = rp, entref = id, ent_tradingstyle = Option(s"Entity $id"))
      entRepo.insert(ent)
    }

    // Now delete them all again
    entRepo.deleteAll

    // Now see if count returns zero
    val counted = entRepo.count()

    counted shouldBe 0
  }
}
