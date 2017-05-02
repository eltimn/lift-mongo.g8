package $package$
package snippet

import $package$.model.User

import scala.xml.NodeSeq

import net.liftweb.common._
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE.Str
import net.liftweb.http.js.JsCmds.{Noop, SetValById}
import net.liftweb.util.{FieldError, FieldIdentifier}
import net.liftweb.util.Helpers._

object Settings extends Loggable {

  private val currentPasswordFieldId = new FieldIdentifier {
    override def uniqueFieldId: Box[String] = Full("current_password_id")
  }

  private def validateAndSave(user: User, section: String): JsCmd = {
    user.validate match {
      case Nil =>
        tryo(user.save(true)) match {
          case Full(u) =>
            S.notice(s"\${section} settings updated!")
          case Failure(msg, _, _) =>
            S.error(s"Error saving record: \$msg")
          case Empty =>
            S.error("Unknown error")
        }
      case errs =>
        S.error(errs)
    }
    Noop
  }

  def account = User.currentUser.map { user =>
    def submit(): JsCmd = validateAndSave(user, "Account")

    "#username_id" #> user.username.toForm &
    "#email_id" #> user.email.toForm &
    "#locale_id" #> user.locale.toForm &
    "#timezone_id" #> user.timezone.toForm &
    "type=submit" #> SHtml.ajaxOnSubmit(submit _)
  } openOr NodeSeq.Empty

  def password = User.currentUser.map { user =>
    var currentPassword = ""
    var password = ""
    var password2 = ""

    def submit(): JsCmd = {
      if (!user.password.isMatch(currentPassword)) {
        currentPassword = ""
        S.error("current_password_id", "Invalid existing password")
        SetValById("current_password_id", Str(""))
      } else {
        User.validatePasswords(password, password2) match {
          case Nil =>
            user.password(password)
            user.password.hashIt
            tryo(user.save(true)) match {
              case Full(u) =>
                S.redirectTo("/settings/password", () => S.notice("Password updated!"))
              case Failure(msg, _, _) =>
                S.error(s"Error saving record: \$msg")
              case Empty =>
                S.error("Unknown error")
            }
          case errs =>
            S.error(errs)
        }

        Noop
      }
    }

    "#current_password_id" #> SHtml.password(currentPassword, currentPassword = _) &
    "#password_id" #> SHtml.password(password, password = _) &
    "#password2_id" #> SHtml.password(password2, password2 = _) &
    "type=submit" #> SHtml.ajaxOnSubmit(submit _)
  } openOr NodeSeq.Empty

  def profile = User.currentUser.map { user =>
    def submit(): JsCmd = validateAndSave(user, "Profile")

    "#name_id" #> user.name.toForm &
    "#location_id" #> user.location.toForm &
    "#bio_id" #> user.bio.toForm &
    "type=submit" #> SHtml.ajaxOnSubmit(submit _)
  } openOr NodeSeq.Empty
}
