package bootstrap.liftweb

import scala.xml.{Null, UnprefixedAttribute}
import javax.mail.internet.MimeMessage

import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.util._
import net.liftweb.util.Helpers._

import $package$.config._
import $package$.model.User

import net.liftmodules.extras.{Gravatar, LiftExtras}
import net.liftmodules.mongoauth.MongoAuth

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {
  def boot {
    logger.info("Run Mode: "+Props.mode.toString)

    // init mongodb
    MongoConfig.init()

    // init auth-mongo
    MongoAuth.authUserMeta.default.set(User)
    MongoAuth.loginTokenAfterUrl.default.set(Site.password.url)
    MongoAuth.siteName.default.set("$name$")
    MongoAuth.systemEmail.default.set("$system_email$")
    MongoAuth.systemUsername.default.set("$name$")

    // For S.loggedIn_? and TestCond.loggedIn/Out builtin snippet
    LiftRules.loggedInTest = Full(() => User.isLoggedIn)

    // checks for ExtSession cookie
    LiftRules.earlyInStateful.append(User.testForExtSession)

    // Gravatar
    Gravatar.defaultImage.default.set("wavatar")

    // config an email sender
    SmtpMailer.init

    // where to search snippet
    LiftRules.addToPackages("$package$")

    // set the default htmlProperties
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    // Build SiteMap
    LiftRules.setSiteMap(Site.siteMap)

    // Error handler
    ErrorHandler.init

    // 404 handler
    LiftRules.uriNotFound.prepend(NamedPF("404handler") {
      case (req, failure) =>
        NotFoundAsTemplate(ParsePath(List("404"), "html", false, false))
    })

    // Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => JsRaw("jQuery('#ajax-spinner').removeClass('hidden')").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => JsRaw("jQuery('#ajax-spinner').addClass('hidden')").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Init Extras
    LiftExtras.init()

    // Mailer
    Mailer.devModeSend.default.set((m: MimeMessage) => logger.info("Dev mode message:\n" + prettyPrintMime(m)))
    Mailer.testModeSend.default.set((m: MimeMessage) => logger.info("Test mode message:\n" + prettyPrintMime(m)))

    // Security Rules
    val csp = ContentSecurityPolicy(
      fontSources = List(
        ContentSourceRestriction.Self,
        ContentSourceRestriction.Host("https://maxcdn.bootstrapcdn.com")
      ),
      styleSources = List(
        ContentSourceRestriction.Self,
        ContentSourceRestriction.UnsafeInline,
        ContentSourceRestriction.Host("https://maxcdn.bootstrapcdn.com")
      ),
      scriptSources = List(
        ContentSourceRestriction.Self,
        ContentSourceRestriction.UnsafeEval, // Lift needs this
        ContentSourceRestriction.UnsafeInline,
        ContentSourceRestriction.Host("https://maxcdn.bootstrapcdn.com"),
        ContentSourceRestriction.Host("https://code.jquery.com")
      )
    )

    val httpRules =
      if (Props.devMode) {
        None
      } else {
        Some(HttpsRules.secure)
      }

    LiftRules.securityRules = () => SecurityRules(
      httpRules,
      Some(csp),
      enforceInOtherModes = true
    )
  }

  private def prettyPrintMime(m: MimeMessage): String = {
    val buf = new StringBuilder
    val hdrs = m.getAllHeaderLines
    while (hdrs.hasMoreElements)
      buf ++= hdrs.nextElement.toString + "\n"

    val out =
      """
        |%s
        |====================================
        |%s
      """.format(buf.toString, m.getContent.toString).stripMargin

    out
  }
}
