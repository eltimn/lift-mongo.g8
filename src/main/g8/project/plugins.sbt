resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
      Resolver.ivyStylePatterns)

resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"

addSbtPlugin("org.scala-sbt" % "sbt-closure" % "0.1.4")

addSbtPlugin("me.lessis" % "less-sbt" % "0.2.2")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.2.5")

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.9.0")
