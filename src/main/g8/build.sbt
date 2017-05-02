Revolver.settings

mainClass in Compile := Some("$package$.WebApp")

// add webapp dir to classpath
resourceGenerators in Compile <+= (resourceManaged, baseDirectory) map { (managedBase, base) =>
  val webappBase = base / "src" / "main" / "webapp"
  for {
    (from, to) <- webappBase ** "*" x rebase(webappBase, managedBase / "main" / "webapp")
  } yield {
    Sync.copy(from, to)
    to
  }
}

addCommandAlias("start", "reStart")
addCommandAlias("stop", "reStop")
