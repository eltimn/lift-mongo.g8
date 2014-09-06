package $package$
package snippet

import $package$.lib._

import java.util.Date
import scala.xml.{NodeSeq, Text}

import net.liftweb._
import common._
import util._
import Helpers._

import net.liftmodules.extras.Gravatar

object HelloWorld extends SimpleInjector {

  lazy val date = new Inject(() => Full(Helpers.now)) {}

  // replace the contents of the element with id "time" with the date
   def render = {
    "#time *" #> date.vend.map(_.toString) &
    "#avatar *" #> Gravatar.imgTag("test@nowhere.com")
  }
}
