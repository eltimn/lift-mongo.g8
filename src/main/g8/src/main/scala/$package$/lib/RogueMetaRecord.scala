package $package$
package lib

import config.MongoConfig

import net.liftweb._
import mongodb.record._

import com.foursquare.rogue._

/**
  * A custom MongoMetaRecord that adds Rogue and an injectable MongoIdentifier.
  */
trait RogueMetaRecord[A <: MongoRecord[A]] extends MongoMetaRecord[A] with LiftRogue {
  self: A =>

  override def mongoIdentifier = MongoConfig.identifier.vend
}
