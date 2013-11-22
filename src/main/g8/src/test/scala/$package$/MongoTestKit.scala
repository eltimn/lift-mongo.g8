package $package$

import org.scalatest._

import net.liftweb._
import common._
import http._
import mongodb._
import util.StackableMaker
import util.Helpers.randomString

import com.mongodb.{MongoClient, ServerAddress}

// The sole mongo object for testing
object TestMongo {
  val mongo = new MongoClient(new ServerAddress("127.0.0.1", 27017))
}

/**
  * Creates a `MongoIdentifier` and database named after the class.
  * Therefore, each Suite class shares the same database.
  * Database is dropped after all tests have been run in the suite.
  */
trait MongoBeforeAndAfterAll extends BeforeAndAfterAll {
  this: Suite =>

  lazy val dbName = "$name;format="norm"$_test_"+this.getClass.getName
    .replace(".", "_")
    .toLowerCase

  def debug = false // db won't be dropped if this is true

  lazy val identifier = new MongoIdentifier {
    val jndiName = dbName
  }

  override def beforeAll(configMap: Map[String, Any]) {
    // define the db
    MongoDB.defineDb(identifier, TestMongo.mongo, dbName)
  }

  override def afterAll(configMap: Map[String, Any]) {
    if (!debug) { dropDb() }
  }

  protected def dropDb(): Unit = MongoDB.use(identifier) { db => db.dropDatabase }
}

/**
  * Basic Mongo suite for running Mongo tests.
  */
trait MongoSuite extends AbstractSuite with MongoBeforeAndAfterAll {
  this: Suite =>

  def mongoIdentifier: StackableMaker[MongoIdentifier]

  abstract override def withFixture(test: NoArgTest) {
    mongoIdentifier.doWith(identifier) {
      super.withFixture(test)
    }
  }
}

/**
  * Mongo suite running within a LiftSession.
  */
trait MongoSessionSuite extends AbstractSuite with MongoBeforeAndAfterAll {
  this: Suite =>

  def mongoIdentifier: StackableMaker[MongoIdentifier]

  // Override with `val` to share session amongst tests.
  protected def session = new LiftSession("", randomString(20), Empty)

  abstract override def withFixture(test: NoArgTest) {
    S.initIfUninitted(session) {
      mongoIdentifier.doWith(identifier) {
        super.withFixture(test)
      }
    }
  }
}
