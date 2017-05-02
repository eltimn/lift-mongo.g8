package $package$
package snippet

import $package$.model.User

import net.liftweb.common._
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.util.Helpers._

object Register extends Loggable {

  def render = {
    var username = ""
    var email = User.loginCredentials.email
    var password = ""
    var password2 = ""
    var isRememberMe = User.loginCredentials.isRememberMe

    def submit(): JsCmd = {
      val user = User.createRecord
        .username(username)
        .email(email)
        .password(password)

      (user.validate ::: User.validatePasswords(password, password2)) match {
        case Nil =>
          user.password.hashIt
          tryo(user.save(true)) match {
            case Full(u) =>
              User.logUserIn(u, true)
              if (isRememberMe) User.createExtSession(u.id.get)
              S.redirectTo("/index", () => S.notice("Thanks for signing up!"))
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

    "#username_id" #> SHtml.text(username, username = _) &
    "#email_id" #> SHtml.text(email, email = _) &
    "#password_id" #> SHtml.password(password, password = _) &
    "#password2_id" #> SHtml.password(password2, password2 = _) &
    "#remember_me" #> SHtml.checkbox(isRememberMe, isRememberMe = _) &
    "type=submit" #> SHtml.ajaxOnSubmit(submit _)
  }
}
