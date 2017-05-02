import sbt._
import sbt.Keys._

// import com.earldouglas.xsbtwebplugin.WebPlugin.{container, webSettings}
// import com.earldouglas.xsbtwebplugin.PluginKeys._

import com.typesafe.sbt.web.PathMapping
import com.typesafe.sbt.web.pipeline.Pipeline
import com.typesafe.sbt.web.Import._
import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.uglify.Import._
import com.typesafe.sbt.jshint.Import._
import net.ground5hark.sbt.concat.Import._

object BuildSettings {
  private var numReloads: Int = 0

  val resolutionRepos = Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )

  val prepareAssets = taskKey[Unit]("prepare-assets")
  val copyVendorAssets = taskKey[Pipeline.Stage]("Copy vendor assets to dist directory")
  val distDir = settingKey[File]("Asset dist directory")
  val uglifyDir = settingKey[File]("Uglify out directory")

  // https://vaadin.com/blog/-/blogs/browsersync-and-jrebel-for-keeping-you-in-flow
  val browserSyncFile = settingKey[File]("BrowserSync file")
  val browserSync = taskKey[Unit]("Update BrowserSync file so grunt notices")

  val basicSettings = Defaults.defaultSettings ++ Seq(
    name := "$name;format="norm"$",
    version := "$project_version$",
    scalaVersion := "2.11.7",
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:postfixOps", "-language:implicitConversions"),
    resolvers ++= resolutionRepos
  )

  // relative to target/web/web-modules/webjars
  val webjarsJs = Seq(
    "lib/jquery/jquery.min.js",
    "lib/bootstrap/js/bootstrap.min.js"
  )

  // relative to src/main/public
  val publicJs = Seq(
    "query.bsAlerts.min.js",
    "jquery.bsFormAlerts.min.js",
    "liftAjax.js"
  )

  val vendorCss = Seq(
    "public/gravatar.min.css"
  )

  val liftAppSettings = basicSettings ++
    // webSettings ++
    addCommandAlias("ccr", "~ ;container:start ;container:reload /") ++
    addCommandAlias("ccrs", "~ ;container:start ;container:reload / ;browserSync") ++
    seq(
      LessKeys.sourceMap in Assets := false,
      LessKeys.compress in Assets := true,

      //UglifyKeys.mangle := false,

      Concat.parentDir := "dist",
      Concat.groups := Seq(
        "styles.min.css" -> group(Seq("less/main.min.css") ++ vendorCss),
        "scripts.min.js" -> group(webjarsJs ++ publicJs)
      ),

      distDir := (WebKeys.webTarget in Assets).value / "dist",
      uglifyDir := (WebKeys.webTarget in Assets).value / "uglify" / "build",

      copyVendorAssets := { mappings: Seq[PathMapping] =>
        val web = (WebKeys.webTarget in Assets).value
        val webjars = (WebKeys.webJarsDirectory in Assets).value
        val dist = distDir.value

        // bootstrap font icons
        IO.copyDirectory(
          webjars / "lib" / "bootstrap" / "fonts",
          dist / "fonts"
        )

        // map files (for dev, will be stripped out via warPostProcess)
        IO.copyFile(
          webjars / "lib" / "jquery" / "jquery.min.map",
          web / "dist" / "jquery.min.map"
        )

        mappings
      },

      pipelineStages in Assets := Seq(uglify, concat, copyVendorAssets),

      prepareAssets := {
        val a = (JshintKeys.jshint in Compile).value
        val b = (LessKeys.less in Compile).value
        val c = (WebKeys.pipeline in Assets).value
        ()
      },

      // (packageWebapp in Compile) <<= (packageWebapp in Compile) dependsOn ((compile in Compile), prepareAssets),
      // (start in container.Configuration) <<= (start in container.Configuration) dependsOn ((compile in Compile), prepareAssets),

      // add distDir and uglifyDir, where sbt-web plugins publish to, to the webapp
      // (webappResources in Compile) <+= distDir,
      // (webappResources in Compile) <+= uglifyDir,

      /*warPostProcess in Compile := {
        (warPath) =>
          // scan all source js files, adding the min.js ones to the list of
          // files to digest and removing anything else
          val srcs: Seq[File] =
            listFilesRecursively(warPath / "js", GlobFilter("*.js"))
              .toSeq
              .flatMap { file =>
                if (file.getName.endsWith("min.js"))
                  Seq(file)
                else {
                  IO.delete(file)
                  Seq[File]()
                }
              }

          // rename assets files with SHA-1 checksum
          val files: Seq[File] =
            (warPath / "scripts.min.js") ::
            (warPath / "styles.min.css") ::
            Nil

          val mapFile = warPath / "WEB-INF" / "classes" / "assets.json"
          val mapFileMappings = (files ++ srcs)
            .flatMap(f => digestFile(f, warPath))
            .map { case (orig, hashed) => s""" "\${orig}": "\${hashed}" """ }
            .mkString(",")

          IO.write(mapFile, s"{ \$mapFileMappings }")

          // remove all .map files
          listFilesRecursively(warPath, GlobFilter("*.map"))
            .foreach(f => IO.delete(f))
      },*/

      browserSyncFile := (target in Compile).value / "browser-sync.txt",
      browserSync := {
        numReloads = numReloads + 1
        IO.write(browserSyncFile.value, numReloads.toString)
      }

    )

  lazy val noPublishing = seq(
    publish := (),
    publishLocal := ()
  )

  private def digestFile(file: File, baseDir: File): Option[(String, String)] = {
    val digest = file.hashString.take(8)
    val (base, ext) = file.baseAndExt
    val newFile = new File(file.getParent, s"\$base-\$digest.\$ext")

    IO.move(file, newFile)

    (IO.relativize(baseDir, file), IO.relativize(baseDir, newFile)) match {
      case (Some(a), Some(b)) => Some(("/"+a, "/"+b))
      case _ => None
    }
  }

  private def listFilesRecursively(dir: File, filter: FileFilter): Array[File] = {
    val files: Array[File] = IO.listFiles(dir)
    files.flatMap { file =>
      if (file.isDirectory)
        listFilesRecursively(file, filter)
      else if (filter.accept(file))
        Array(file)
      else
        Array[File]()
    }
  }
}
