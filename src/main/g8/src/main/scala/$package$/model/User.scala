package $package$
package model

import $package$.config.MongoConfig

import org.bson.types.ObjectId
import org.joda.time.DateTime

import net.liftweb.common._
import net.liftweb.http.{StringField => _, BooleanField => _, _}
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import net.liftweb.util.{FieldContainer, FieldError, FieldIdentifier}

import net.liftmodules.mongoauth._
import net.liftmodules.mongoauth.field._
import net.liftmodules.mongoauth.model._

class User private () extends ProtoAuthUser[User] with ObjectIdPk[User] {
  def meta = User

  def userIdAsString: String = id.toString

  object locale extends LocaleField(this) {
    override def displayName = "Locale"
    override def defaultValue = "en_US"
  }
  object timezone extends TimeZoneField(this) {
    override def displayName = "Time Zone"
    override def defaultValue = "America/Chicago"
  }

  object name extends StringField(this, 64) {
    override def displayName = "Name"

    override def validations =
      valMaxLen(64, "Name must be 64 characters or less") _ ::
      super.validations
  }
  object location extends StringField(this, 64) {
    override def displayName = "Location"

    override def validations =
      valMaxLen(64, "Location must be 64 characters or less") _ ::
      super.validations
  }
  object bio extends TextareaField(this, 160) {
    override def displayName = "Bio"

    override def validations =
      valMaxLen(160, "Bio must be 160 characters or less") _ ::
      super.validations
  }

  def whenCreated: DateTime = new DateTime(id.get.getDate)
}

object User extends User with ProtoAuthUserMeta[User] with Loggable {

  override def collectionName = "user.users"
  override def connectionIdentifier = MongoConfig.defaultId.vend

  /** Note: this will be called multiple times if
    * run on multiple servers. Mongo won't create an index
    * if one already exists, but it's best to create indexes
    * manually.
    */
  // createIndex((email.name -> 1), true)
  // createIndex((username.name -> 1), true)

  def findByEmail(in: String): Box[User] = find(email.name, in)
  def findByUsername(in: String): Box[User] = find(username.name, in)

  def findByStringId(id: String): Box[User] =
    if (ObjectId.isValid(id)) find(new ObjectId(id))
    else Empty

  override def onLogIn: List[User => Unit] = List(user => User.loginCredentials.remove())
  override def onLogOut: List[Box[User] => Unit] = List(
    x => logger.debug("User.onLogOut called."),
    boxedUser => boxedUser.foreach { u =>
      ExtSession.deleteExtCookie()
    }
  )

  /**
    * MongoAuth vars
    */
  private lazy val siteName = MongoAuth.siteName.vend
  private lazy val sysUsername = MongoAuth.systemUsername.vend
  private lazy val indexUrl = MongoAuth.indexUrl.vend
  private lazy val registerUrl = MongoAuth.registerUrl.vend
  private lazy val loginTokenAfterUrl = MongoAuth.loginTokenAfterUrl.vend

  /**
    * LoginToken
    */
  override def handleLoginToken: Box[LiftResponse] = {
    val resp = S.param("token").flatMap(LoginToken.findByStringId) match {
      case Full(at) if (at.expires.isExpired) => {
        at.delete_!
        RedirectWithState(indexUrl, RedirectState(() => { S.error("Login token has expired") }))
      }
      case Full(at) => find(at.userId.get).map(user => {
        if (user.validate.length == 0) {
          user.verified(true)
          user.update
          logUserIn(user)
          at.delete_!
          RedirectResponse(loginTokenAfterUrl)
        } else {
          at.delete_!
          loginCredentials(LoginCredentials(user.email.get))
          RedirectWithState(registerUrl, RedirectState(() => { S.notice("Please complete the registration form") }))
        }
      }).openOr(RedirectWithState(indexUrl, RedirectState(() => { S.error("User not found") })))
      case _ => RedirectWithState(indexUrl, RedirectState(() => { S.warning("Login token not provided") }))
    }

    Full(resp)
  }

  // send an email to the user with a link for logging in
  def sendLoginToken(user: User): Unit = {
    import net.liftweb.util.Mailer._

    LoginToken.createForUserIdBox(user.id.get).foreach { token =>

      val msgTxt =
        """
          |Someone requested a link to change your password on the %s website.
          |
          |If you did not request this, you can safely ignore it. It will expire 48 hours from the time this message was sent.
          |
          |Follow the link below or copy and paste it into your internet browser.
          |
          |%s
          |
          |Thanks,
          |%s
        """.format(siteName, token.url, sysUsername).stripMargin

      sendMail(
        From(MongoAuth.systemFancyEmail),
        Subject("%s Password Help".format(siteName)),
        To(user.fancyEmail),
        PlainMailBodyType(msgTxt)
      )
    }
  }

  /**
    * ExtSession
    */
  def createExtSession(uid: ObjectId): Box[Unit] = ExtSession.createExtSessionBox(uid)

  /**
    * Test for active ExtSession.
    */
  def testForExtSession: Box[Req] => Unit = {
    ignoredReq => {
      if (currentUserId.isEmpty) {
        ExtSession.handleExtSession match {
          case Full(es) =>
            find(es.userId.get).foreach { user => logUserIn(user, false) }
          case Failure(msg, _, _) =>
            logger.warn("Error logging user in with ExtSession: %s".format(msg))
          case Empty =>
        }
      }
    }
  }

  private val pwdMinLength = 6
  private val pwdMaxLength = 256

  private val passwordFieldId = new FieldIdentifier {
    override def uniqueFieldId: Box[String] = Full("password_id")
  }

  private val password2FieldId = new FieldIdentifier {
    override def uniqueFieldId: Box[String] = Full("password2_id")
  }

  def validatePasswords(password: String, password2: String): List[FieldError] = {
    if (password.length < pwdMinLength) {
      List(FieldError(passwordFieldId, s"Password must be at least \${pwdMinLength} characters"))
    } else if (password.length > pwdMaxLength) {
      List(FieldError(passwordFieldId, s"Password must be \${pwdMaxLength} characters or less"))
    } else if (password != password2) {
      List(FieldError(password2FieldId, "Passwords must match"))
    } else {
      Nil
    }
  }

  // used during login process
  object loginCredentials extends SessionVar[LoginCredentials](LoginCredentials(""))
}

case class LoginCredentials(email: String, isRememberMe: Boolean = false)
