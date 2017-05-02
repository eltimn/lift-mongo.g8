package $package$

import config.MongoConfig

import org.scalatest._

import net.liftweb._
import common._
import http._
import util._
import Helpers._

trait BaseWordSpec extends WordSpec with Matchers

trait BaseMongoWordSpec extends BaseWordSpec with MongoSuite {
  def mongoIdentifier = MongoConfig.defaultId
}
trait BaseMongoSessionWordSpec extends BaseWordSpec with MongoSessionSuite {
  def mongoIdentifier = MongoConfig.defaultId
}

trait WithSessionSpec extends TestSuiteMixin { this: TestSuite =>

  protected def session = new LiftSession("", randomString(20), Empty)

  abstract override def withFixture(test: NoArgTest) = {
    S.initIfUninitted(session) { super.withFixture(test) }
  }
}

