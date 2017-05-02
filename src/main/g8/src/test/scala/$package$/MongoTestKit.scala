package $package$

import org.scalatest._

import net.liftweb._
import common._
import http._
import mongodb._
import util._
import util.Helpers.randomString

import com.mongodb._

// The sole mongo object for testing
object TestMongo {
  val mongo = {
    val uri = Props.get("mongo.default.uri", "$mongo_host$:$mongo_port$")
    new MongoClient(new MongoClientURI(s"mongodb://\$uri"))
  }
}

/**
  * Creates a `ConnectionIdentifier` and database named after the class.
  * Therefore, each Suite class shares the same database.
  * Database is dropped after all tests have been run in the suite.
  */
trait MongoBeforeAndAfterAll extends BeforeAndAfterAll {
  this: Suite =>

  lazy val dbName = s"$name;format="norm"$_test_\${this.getClass.getName}"
    .replace(".", "_")
    .toLowerCase

  def debug = false // db won't be dropped if this is true

  lazy val Identifier = new ConnectionIdentifier {
    val jndiName = dbName
  }

  override def beforeAll() {
    // define the db
    MongoDB.defineDb(Identifier, TestMongo.mongo, dbName)
  }

  override def afterAll() {
    if (!debug) { dropDb() }
  }

  protected def dropDb(): Unit = MongoDB.use(Identifier) { db => db.dropDatabase }
}

/**
  * Basic Mongo suite for running Mongo tests.
  */
trait MongoSuite extends TestSuiteMixin with MongoBeforeAndAfterAll {
  this: TestSuite =>

  def mongoIdentifier: StackableMaker[ConnectionIdentifier]

  abstract override def withFixture(test: NoArgTest): Outcome = {
    mongoIdentifier.doWith(Identifier) {
      super.withFixture(test)
    }
  }
}

/**
  * Mongo suite running within a LiftSession.
  */
trait MongoSessionSuite extends TestSuiteMixin with MongoBeforeAndAfterAll {
  this: TestSuite =>

  def mongoIdentifier: StackableMaker[ConnectionIdentifier]

  // Override with `val` to share session amongst tests.
  protected def session = new LiftSession("", randomString(20), Empty)

  abstract override def withFixture(test: NoArgTest): Outcome = {
    S.initIfUninitted(session) {
      mongoIdentifier.doWith(Identifier) {
        super.withFixture(test)
      }
    }
  }
}
