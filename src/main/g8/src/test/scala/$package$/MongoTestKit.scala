package $package$

import org.scalatest.{BeforeAndAfterAll, WordSpec}

import net.liftweb._
import mongodb._

import com.mongodb.{Mongo, ServerAddress}

/**
  * Creates a Mongo instance named after the class.
  * Therefore, each Spec class shares the same database.
  * Database is dropped after.
  */
trait MongoTestKit extends BeforeAndAfterAll {
  this: WordSpec =>

  def dbName = "test_"+this.getClass.getName
    .replace(".", "_")
    .toLowerCase

  def debug = false

  override def beforeAll(configMap: Map[String, Any]) {
    // define the db
    MongoDB.defineDb(DefaultMongoIdentifier, TestMongo.mongo, dbName)
  }

  override def afterAll(configMap: Map[String, Any]) {
    if (!debug) {
      // drop the database
      MongoDB.use(DefaultMongoIdentifier) { db => db.dropDatabase }
    }
  }
}

object TestMongo {
  val mongo = new Mongo(new ServerAddress("127.0.0.1", 27017))
}
