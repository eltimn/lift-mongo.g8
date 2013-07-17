package $package$

import config.MongoConfig

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import net.liftweb._
import common._
import http._
import util._
import Helpers._

trait BaseWordSpec extends WordSpec with ShouldMatchers

trait BaseMongoWordSpec extends BaseWordSpec with MongoSuite {
  def mongoIdentifier = MongoConfig.identifier
}
trait BaseMongoSessionWordSpec extends BaseWordSpec with MongoSessionSuite {
  def mongoIdentifier = MongoConfig.identifier
}

trait WithSessionSpec extends AbstractSuite { this: Suite =>

  protected def session = new LiftSession("", randomString(20), Empty)

  abstract override def withFixture(test: NoArgTest) {
    S.initIfUninitted(session) { super.withFixture(test) }
  }
}

