import sbt._
import sbt.Keys._

import com.github.siasia.WebPlugin.{container, webSettings}
import com.github.siasia.PluginKeys._
import sbtbuildinfo.Plugin._
import less.Plugin._
import sbtbuildinfo.Plugin._
import sbtclosure.SbtClosurePlugin._

object BuildSettings {
  object Ver {
    val lift = "2.5-RC5"
    val lift_edition = "2.5"
    val jetty = "8.1.8.v20121106"
  }

  val buildTime = SettingKey[String]("build-time")

  val basicSettings = Defaults.defaultSettings ++ Seq(
    name := "$name;format="norm"$",
    version := "$project_version$",
    organization := "$sbt_organization$",
    scalaVersion := "2.10.0",
    scalacOptions <<= scalaVersion map { sv: String =>
      if (sv.startsWith("2.10."))
        Seq("-deprecation", "-unchecked", "-feature", "-language:postfixOps", "-language:implicitConversions")
      else
        Seq("-deprecation", "-unchecked")
    }
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

