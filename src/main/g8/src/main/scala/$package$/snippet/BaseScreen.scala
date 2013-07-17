package $package$
package snippet

import net.liftweb._
import http.js._
import http.js.JsCmds._
import http.js.JE._

import net.liftmodules.extras.BootstrapScreen

/*
 * Base all LiftScreens off this. Currently configured to use bootstrap.
 */
abstract class BaseScreen extends BootstrapScreen {
  override def defaultToAjax_? = true
  def cssErrorClass = "error" // BS3 = has-error

  override protected def afterScreenLoad: JsCmd = JsRaw("""
    |\$(".form-alert").each(function() {
    |  \$(this).closest("div.control-group").addClass("%s");
    |});
    """.format(cssErrorClass).stripMargin)
}

