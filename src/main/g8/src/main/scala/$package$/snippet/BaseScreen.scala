package $package$
package snippet

import net.liftweb._
import http.js._
import http.js.JsCmds._
import http.js.JE._

import net.liftmodules.extras.Bootstrap3Screen

/*
 * Base all LiftScreens off this. Currently configured to use bootstrap 3.
 */
abstract class BaseScreen extends Bootstrap3Screen {
  override def defaultToAjax_? = true
}

