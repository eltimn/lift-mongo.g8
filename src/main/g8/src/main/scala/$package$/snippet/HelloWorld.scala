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

class HelloWorld {
  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  // replace the contents of the element with id "time" with the date
   def render = {
    "#time *" #> date.map(_.toString) &
    "#avatar *" #> Gravatar.imgTag("test@nowhere.com")
  }
}
