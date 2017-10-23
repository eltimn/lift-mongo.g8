import sbt._
import sbt.Keys._

object LiftProjectBuild extends Build {

  import BuildSettings._

  object Ver {
    val lift = "2.6"
    val lift_edition = "2-6"
    val jetty = "9.2.9.v20150224"
  }

  def compile(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  lazy val root = Project("$name;format="norm"$", file("."))
    .settings(liftAppSettings: _*)
    .settings(libraryDependencies ++=
      compile(
        "net.liftweb"       %% "lift-webkit"                   % Ver.lift,
        "net.liftweb"       %% "lift-mongodb-record"           % Ver.lift,
        "net.liftmodules"   %% ("extras_"+Ver.lift_edition)    % "0.4",
        "net.liftmodules"   %% ("mongoauth_"+Ver.lift_edition) % "0.6",
        "ch.qos.logback"    % "logback-classic"                % "1.1.2",
        "org.eclipse.jetty" % "jetty-server"                   % Ver.jetty,
        "org.eclipse.jetty" % "jetty-webapp"                   % Ver.jetty
      ) ++
      container("org.eclipse.jetty" % "jetty-webapp" % Ver.jetty) ++
      test("org.scalatest" %% "scalatest" % "2.2.4")
    )
}
