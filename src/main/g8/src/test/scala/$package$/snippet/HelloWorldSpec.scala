package $package$
package snippet

import net.liftweb._
import common._
import http._
import util._
import Helpers._

class HelloWorldSpec extends BaseWordSpec {
  val session = new LiftSession("", randomString(20), Empty)
  val stableTime = now

  override def withFixture(test: NoArgTest) = {
    S.initIfUninitted(session) {
      HelloWorld.date.doWith(Full(stableTime)) {
        test()
      }
    }
  }

  "HelloWorld Snippet" should {
    "Put the time in the node" in {
      Thread.sleep(1000) // make sure the time changes

      val str = HelloWorld.render(<span>Welcome to your Lift app at <span id="time">Time goes here</span></span>).toString

      str.indexOf(stableTime.toString) should be >= 0
      str.indexOf("Welcome to your Lift app at") should be >= 0
    }
  }
}
