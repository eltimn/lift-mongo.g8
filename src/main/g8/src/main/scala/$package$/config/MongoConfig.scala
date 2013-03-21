package $package$
package config

import net.liftweb._
import common._
import json._
import mongodb._
import util.Props

import com.mongodb.{DBAddress, Mongo}

object MongoConfig extends Loggable {

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
        MongoDB.defineDbAuth(
          DefaultMongoIdentifier,
          new Mongo(defaultDbAddress),
          defaultDbAddress.getDBName,
          user,
          pwd
        )
        logger.info("MongoDB inited using authentication: %s".format(defaultDbAddress.toString))
      case _ =>
        MongoDB.defineDb(
          DefaultMongoIdentifier,
          new Mongo(defaultDbAddress),
          defaultDbAddress.getDBName
        )
        logger.info("MongoDB inited: %s".format(defaultDbAddress.toString))
    }
  }
}

