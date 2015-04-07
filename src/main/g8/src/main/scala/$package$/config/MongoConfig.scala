package $package$
package config

import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.json._
import net.liftweb.mongodb._
import net.liftweb.util._

import com.mongodb._

object MongoConfig extends Factory with Loggable {

  // configure your MongoMetaRecords to use this. See lib/RogueMetaRecord.scala.
  val defaultId = new FactoryMaker[ConnectionIdentifier](DefaultConnectionIdentifier) {}

  lazy val uri = new MongoClientURI(
    s"""mongodb://\${Props.get("mongo.default.uri", "$mongo_host$:$mongo_port$/$name;format="norm"$")}"""
  )

  def init(): Unit = {
    val client = new MongoClient(uri)

    MongoDB.defineDb(
      defaultId.vend,
      client,
      uri.getDatabase
    )

    logger.info(s"MongoDB inited: \${uri.getHosts}")
    logger.debug(s"MongoDB options: \${uri.getOptions}")
  }
}
