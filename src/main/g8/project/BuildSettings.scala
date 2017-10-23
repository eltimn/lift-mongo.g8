import sbt._
import sbt.Keys._

import com.earldouglas.xsbtwebplugin.WebPlugin.{container, webSettings}
import com.earldouglas.xsbtwebplugin.PluginKeys._

object BuildSettings {
  val basicSettings = Defaults.defaultSettings ++ Seq(
    name := "$name;format="norm"$",
    version := "$project_version$",
    scalaVersion := "2.11.5",
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:postfixOps", "-language:implicitConversions")
  )

  val liftAppSettings = basicSettings ++
    webSettings ++
    addCommandAlias("ccr", "~ ;container:start ;container:reload /")

  lazy val noPublishing = seq(
    publish := (),
    publishLocal := ()
  )
}
