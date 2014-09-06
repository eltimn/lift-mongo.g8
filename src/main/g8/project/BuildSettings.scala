import sbt._
import sbt.Keys._

import com.earldouglas.xsbtwebplugin.WebPlugin.{container, webSettings}
import com.earldouglas.xsbtwebplugin.PluginKeys._
import sbtbuildinfo.Plugin._
import less.Plugin._
import sbtbuildinfo.Plugin._
import sbtclosure.SbtClosurePlugin._

object BuildSettings {

  val buildTime = SettingKey[String]("build-time")

  val basicSettings = Defaults.defaultSettings ++ Seq(
    name := "$name;format="norm"$",
    version := "$project_version$",
    scalaVersion := "2.11.2",
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:postfixOps", "-language:implicitConversions"),
    resolvers ++= Dependencies.resolutionRepos
  )

  val liftAppSettings = basicSettings ++
    webSettings ++
    buildInfoSettings ++
    lessSettings ++
    closureSettings ++
    seq(
      buildTime := System.currentTimeMillis.toString,

      // build-info
      buildInfoKeys ++= Seq[BuildInfoKey](buildTime),
      buildInfoPackage := "$package$",
      sourceGenerators in Compile <+= buildInfo,

      // less
      (LessKeys.filter in (Compile, LessKeys.less)) := "*styles.less",
      (LessKeys.mini in (Compile, LessKeys.less)) := true,

      // closure
      (ClosureKeys.prettyPrint in (Compile, ClosureKeys.closure)) := false,

      // add managed resources, where less and closure publish to, to the webapp
      (webappResources in Compile) <+= (resourceManaged in Compile)
    )

  lazy val noPublishing = seq(
    publish := (),
    publishLocal := ()
  )
}

