package $package$

import java.net.{InetAddress, InetSocketAddress}

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

import net.liftweb.common.Loggable
import net.liftweb.util.{ LoggingAutoConfigurer, Props }

object WebApp extends App with Loggable {
  LoggingAutoConfigurer().apply()

  logger.info("run.mode: " + Props.mode.toString)
  logger.trace("system environment: " + sys.env)
  logger.trace("system props: " + sys.props)
  logger.trace("liftweb props: " + Props.props)

  val port = sys.props.getOrElse("port", "8080").toInt

  // bind to localhost only
  val address = new InetSocketAddress(InetAddress.getLoopbackAddress, port)

  val webPath = getClass.getClassLoader.getResource("webapp").toExternalForm

  // root context
  val context = new WebAppContext(webPath, "/")
  // don't allow directory browsing
  context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false")

  // start the server
  val server = new Server(address)
  server.setHandler(context)
  server.start()
  server.join()
}
