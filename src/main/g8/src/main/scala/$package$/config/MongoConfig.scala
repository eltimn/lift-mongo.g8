package $package$
package config

import net.liftweb._
import common._
import http._
import json._
import mongodb._
import util._

import com.mongodb._

object MongoConfig extends Factory with Loggable {

  import scala.collection.JavaConversions._

  // configure your MongoMetaRecords to use this. See lib/RogueMetaRecord.scala.
  val defaultId = new FactoryMaker[ConnectionIdentifier](DefaultConnectionIdentifier) {}

  def init() {
    /**
      * First checks for existence of mongo.default.url. If not found, then
      * checks for mongo.default.host, port, and name. Uses defaults if those
      * are not found.
      */
    val defaultDbAddress = Props.get("mongo.default.url")
      .map(url => new DBAddress(url))
      .openOr(new DBAddress(
        Props.get("mongo.default.host", "127.0.0.1"),
        Props.getInt("mongo.default.port", 27017),
        Props.get("mongo.default.name", "$name;format="norm"$")
      ))

    /*
     * If mongo.default.user, and pwd are defined, configure Mongo using authentication.
     */
    (Props.get("mongo.default.user"), Props.get("mongo.default.pwd")) match {
      case (Full(user), Full(pwd)) =>
        val creds = MongoCredential.createMongoCRCredential(user, defaultDbAddress.getDBName, pwd.toCharArray)
        MongoDB.defineDb(
          defaultId.vend,
          new MongoClient(defaultDbAddress, List(creds)),
          defaultDbAddress.getDBName
        )
        logger.info("MongoDB inited using authentication: %s".format(defaultDbAddress.toString))
      case _ =>
        MongoDB.defineDb(
          defaultId.vend,
          new MongoClient(defaultDbAddress),
          defaultDbAddress.getDBName
        )
        logger.info("MongoDB inited: %s".format(defaultDbAddress.toString))
    }
  }
}

