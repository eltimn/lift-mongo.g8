name := "$name;format="norm"$"
version := "$project_version$"
scalaVersion := "2.12.2"
scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:postfixOps", "-language:implicitConversions")

val liftVersion = "3.1.1"
val liftEdition = "3.1"

def lv(s: String): String = s"\${s}_\${liftEdition}"

libraryDependencies += "net.liftweb"       %% "lift-webkit"           % liftVersion
libraryDependencies += "net.liftweb"       %% "lift-mongodb-record"   % liftVersion
libraryDependencies += "net.liftmodules"   %% lv("extras")            % "1.1.0"
libraryDependencies += "net.liftmodules"   %% lv("mongoauth")         % "1.3.0"
libraryDependencies += "ch.qos.logback"    % "logback-classic"        % "1.2.3"

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

enablePlugins(JettyPlugin)
