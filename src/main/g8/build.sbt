name := "$name;format="norm"$"
version := "$project_version$"
scalaVersion := "2.12.2"
scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:postfixOps", "-language:implicitConversions")

def lv(s: String): String = s"\${s}_\${Build.Ver.lift_edition}"

libraryDependencies += "net.liftweb"       %% "lift-webkit"           % Build.Ver.lift
libraryDependencies += "net.liftweb"       %% "lift-mongodb-record"   % Build.Ver.lift
libraryDependencies += "net.liftmodules"   %% lv("extras")            % "1.0.1"
libraryDependencies += "net.liftmodules"   %% lv("mongoauth")         % "1.2"
libraryDependencies += "ch.qos.logback"    % "logback-classic"        % "1.1.2"
// libraryDependencies += "org.eclipse.jetty" % "jetty-server"           % Build.Ver.jetty
// libraryDependencies += "org.eclipse.jetty" % "jetty-webapp"           % Build.Ver.jetty

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

enablePlugins(JettyPlugin)

containerLibs in Jetty := Seq("org.eclipse.jetty" % "jetty-runner" % Build.Ver.jetty intransitive())

addCommandAlias("ccr", "~ ;container:start ;container:reload /")
