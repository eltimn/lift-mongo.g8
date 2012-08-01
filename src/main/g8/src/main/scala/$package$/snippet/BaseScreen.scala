package $package$
package snippet

import scala.xml._

import net.liftweb._
import common._
import http._
import util.Helpers._

/*
 * Base all LiftScreens off this. Currently configured to use bootstrap.
 */
trait BaseScreen extends LiftScreen {
  override val cancelButton = super.cancelButton % ("class" -> "btn") % ("tabindex" -> "1")
  override val finishButton = super.finishButton % ("class" -> "btn btn-primary") % ("tabindex" -> "1")

  override def additionalAttributes: MetaData = {
    val cssCls = new UnprefixedAttribute("class", "form-horizontal", Null)
    cssCls.append(super.additionalAttributes)
  }

  def displayOnly(fieldName: => String, html: => NodeSeq) =
    new Field {
      type ValueType = String
      override def name = fieldName
      override implicit def manifest = buildIt[String]
      override def default = ""
      override def toForm: Box[NodeSeq] = Full(html)
    }
}
