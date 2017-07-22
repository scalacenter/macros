import org.scalamacros.build._

// ==========================================
// Projects
// ==========================================

{
  println(s"[info] Welcome to Scala macros $BuildVersion")
  name := "scalamacrosRoot"
}
nonPublishableSettings

lazy val scalamacros = project
  .in(file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    publishableSettings,
    version := CoreVersion,
    moduleName := CoreProduct,
    description := "Platform-independent interfaces for new-style Scala macros"
  )

lazy val enginesScalac = project
  .in(file("engines/scalac"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    publishableSettings,
    version := EngineScalacVersion,
    crossScalaVersions := List(Scala211), // TODO: support other versions of Scalac
    moduleName := EngineScalacProduct,
    description := "Scalac implementation of interfaces for new-style Scala macros",
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
  )
  .dependsOn(scalamacros)

lazy val pluginsScalac = project
  .in(file("plugins/scalac"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    pluginSettings,
    version := PluginScalacVersion,
    crossScalaVersions := List(Scala211), // TODO: support other versions of Scalac
    moduleName := PluginScalacProduct,
    description := "Scalac integration for new-style Scala macros",
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    resolvers += Resolver.bintrayRepo("scalameta", "maven"),
    libraryDependencies += "org.scalameta" %% "scalameta" % "1.9.0-768-cf95688f"
  )
  .dependsOn(enginesScalac)

lazy val testsApi = project
  .in(file("tests/api"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    nonPublishableSettings,
    description := "Tests of interfaces for new-style Scala macros",
    libraryDependencies += "junit" % "junit" % "4.12",
    libraryDependencies ++= (
      if (isDotty.value) Nil
      else Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    )
  )
  .dependsOn(scalamacros)

lazy val testsMacros = project
  .in(file("tests/macros"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    nonPublishableSettings,
    description := "Tests of new-style Scala macros",
    crossScalaVersions := List(Scala211), // TODO: support other versions of Scala
    libraryDependencies += "junit" % "junit" % "4.12",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full),
    scalacOptions ++= {
      val jar = Keys.`package`.in(pluginsScalac).in(Compile).value
      val addPlugin = "-Xplugin:" + jar.getAbsolutePath
      // Thanks Jason for this cool idea (taken from https://github.com/retronym/boxer)
      // add plugin timestamp to compiler options to trigger recompile of
      // main after editing the plugin. (Otherwise a 'clean' is needed.)
      val dummy = "-Jdummy=" + jar.lastModified
      Seq(addPlugin, dummy)
    }
  )
  .dependsOn(scalamacros)

// ==========================================
// Settings
// ==========================================

lazy val sharedSettings = Def.settings(
  initialize := {
    val _ = initialize.value
    val jdk = sys.props("java.specification.version")
    assert(jdk == "1.8", "this build only supports JDK 1.8")
  },
  scalaVersion := LanguageVersion,
  crossScalaVersions := LanguageVersions,
  unmanagedSourceDirectories.in(Compile) += {
    val main = baseDirectory.in(Compile).value / "src" / "main"
    val epochSpecificName = if (isDotty.value) "scala-0" else "scala-2"
    main / epochSpecificName
  },
  unmanagedSourceDirectories.in(Test) += {
    val test = baseDirectory.in(Test).value / "src" / "test"
    val epochSpecificName = if (isDotty.value) "scala-0" else "scala-2"
    test / epochSpecificName
  },
  crossVersion := CrossVersion.binary,
  organization := "org.scalamacros",
  scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings"),
  logBuffered := false,
  incOptions := incOptions.value.withLogRecompileOnMacro(false),
  updateOptions := updateOptions.value.withCachedResolution(true),
  triggeredMessage.in(ThisBuild) := Watched.clearWhenTriggered,
  buildInfoKeys := Seq[BuildInfoKey](
    scalaVersion,
    version,
    name,
    moduleName
  ),
  buildInfoPackage := "scala.macros.internal.config." + name.value,
  buildInfoObject := "BuildInfo",
  libraryDependencies += "junit" % "junit" % "4.12" % "test",
  libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-n")
)

lazy val pluginSettings = Def.settings(
  publishableSettings,
  crossVersion := CrossVersion.full,
  unmanagedSourceDirectories.in(Compile) += {
    val base = sourceDirectory.in(Compile).value
    base / ("scala-" + scalaVersion.value)
  },
  test.in(assembly) := {},
  logLevel.in(assembly) := Level.Error,
  assemblyJarName.in(assembly) := {
    name.value + "_" + scalaVersion.value + "-" + version.value + "-assembly.jar"
  },
  assemblyOption.in(assembly) ~= { _.copy(includeScala = false) },
  Keys.`package`.in(Compile) := {
    val slimJar = Keys.`package`.in(Compile).value
    val fatJar = new File(crossTarget.value + "/" + assemblyJarName.in(assembly).value)
    val _ = assembly.value
    IO.copy(List(fatJar -> slimJar), overwrite = true)
    slimJar
  },
  packagedArtifact.in(Compile).in(packageBin) := {
    val temp = packagedArtifact.in(Compile).in(packageBin).value
    val (art, slimJar) = temp
    val fatJar = new File(crossTarget.value + "/" + assemblyJarName.in(assembly).value)
    val _ = assembly.value
    IO.copy(List(fatJar -> slimJar), overwrite = true)
    (art, slimJar)
  }
)

lazy val publishableSettings = Def.settings(
  sharedSettings,
  bintrayOrganization := Some("scalamacros"),
  publishArtifact.in(Compile) := true,
  publishArtifact.in(Test) := false,
  publishMavenStyle := true,
  pomIncludeRepository := { x =>
    false
  },
  licenses += "BSD" -> url("https://github.com/scalamacros/scalamacros/blob/master/LICENSE.md"),
  pomExtra := (
    <url>https://github.com/scalamacros/scalamacros</url>
    <inceptionYear>2017</inceptionYear>
    <scm>
      <url>git://github.com/scalamacros/scalamacros.git</url>
      <connection>scm:git:git://github.com/scalamacros/scalamacros.git</connection>
    </scm>
    <issueManagement>
      <system>GitHub</system>
      <url>https://github.com/scalamacros/scalamacros/issues</url>
    </issueManagement>
    <developers>
      <developer>
        <id>xeno-by</id>
        <name>Eugene Burmako</name>
        <url>http://xeno.by</url>
      </developer>
    </developers>
  )
)

lazy val nonPublishableSettings = Def.settings(
  sharedSettings,
  publishArtifact.in(Compile, packageDoc) := false,
  publishArtifact.in(packageDoc) := false,
  sources.in(Compile, doc) := Seq.empty,
  packagedArtifacts := Map.empty,
  publishArtifact := false,
  publish := {}
)
