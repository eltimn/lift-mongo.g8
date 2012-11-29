package $package$
package model

import org.bson.types.ObjectId
import org.joda.time.DateTime

import net.liftweb._
import common._
import http.{StringField => _, BooleanField => _, _}
import mongodb.record.field._
import record.field._
import util.FieldContainer

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

  /*
   * FieldContainers for various LiftScreeens.
   */
  def accountScreenFields = new FieldContainer {
    def allFields = List(username, email, locale, timezone)
  }

  def profileScreenFields = new FieldContainer {
    def allFields = List(name, location, bio)
  }

  def registerScreenFields = new FieldContainer {
    def allFields = List(username, email)
  }

  def whenCreated: DateTime = new DateTime(id.is.getTime)
}

object User extends User with ProtoAuthUserMeta[User] with Loggable {
  import mongodb.BsonDSL._

  override def collectionName = "user.users"

  ensureIndex((email.name -> 1), true)
  ensureIndex((username.name -> 1), true)

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

  /*
   * MongoAuth vars
   */
  private lazy val siteName = MongoAuth.siteName.vend
  private lazy val sysUsername = MongoAuth.systemUsername.vend
  private lazy val indexUrl = MongoAuth.indexUrl.vend
  private lazy val registerUrl = MongoAuth.registerUrl.vend
  private lazy val loginTokenAfterUrl = MongoAuth.loginTokenAfterUrl.vend

  /*
   * LoginToken
   */
  override def handleLoginToken: Box[LiftResponse] = {
    val resp = S.param("token").flatMap(LoginToken.findByStringId) match {
      case Full(at) if (at.expires.isExpired) => {
        at.delete_!
        RedirectWithState(indexUrl, RedirectState(() => { S.error("Login token has expired") }))
      }
      case Full(at) => find(at.userId.is).map(user => {
        if (user.validate.length == 0) {
          user.verified(true)
          user.save
          logUserIn(user)
          at.delete_!
          RedirectResponse(loginTokenAfterUrl)
        }
        else {
          at.delete_!
          regUser(user)
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

    val token = LoginToken.createForUserId(user.id.is)

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

  /*
   * ExtSession
   */
  def createExtSession(uid: ObjectId) = ExtSession.createExtSession(uid)

  /*
  * Test for active ExtSession.
  */
  def testForExtSession: Box[Req] => Unit = {
    ignoredReq => {
      if (currentUserId.isEmpty) {
        ExtSession.handleExtSession match {
          case Full(es) => find(es.userId.is).foreach { user => logUserIn(user, false) }
          case Failure(msg, _, _) =>
            logger.warn("Error logging user in with ExtSession: %s".format(msg))
          case Empty =>
        }
      }
    }
  }

  // used during login process
  object loginCredentials extends SessionVar[LoginCredentials](LoginCredentials(""))
  object regUser extends SessionVar[User](createRecord.email(loginCredentials.is.email))
}

case class LoginCredentials(email: String, isRememberMe: Boolean = false)

object SystemUser {
  private val username = "$name;format="norm"$"
  private val email = "$admin_email$"

  lazy val user: User = User.find("username", username) openOr {
    User.createRecord
      .name("$name$")
      .username(username)
      .email(email)
      .locale("en_US")
      .timezone("America/Chicago")
      .verified(true)
      .password("$admin_password$", true)
      .save
  }
}
