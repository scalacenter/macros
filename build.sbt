import org.scalameta.build._
import org.scalajs.sbtplugin.ScalaJSCrossVersion

// ==========================================
// Projects
// ==========================================

name := "scalametaRoot"
nonPublishableSettings
commands += CiCommand("ci-test", List("test"))
commands += CiCommand("ci-publish", List("publish"))

lazy val core = crossProject
  .in(file("core"))
  .enablePlugins(BuildInfoPlugin)
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
  .settings(
    publishableSettings,
    version := CoreVersion,
    description := "Platform-independent interfaces for syntactic and semantic APIs of Scalameta"
  )
  .jsSettings(
    npmDependencies in Compile += "shelljs" -> "0.7.7"
  )
lazy val coreJVM = core.jvm
lazy val coreJS = core.js

lazy val profilesMacros = crossProject
  .in(file("profiles/macros"))
  .settings(
    publishableSettings,
    version := CoreVersion,
    description := "Platform-independent interfaces for new-style Scala macros"
  )
  .dependsOn(core)
lazy val profilesMacrosJVM = profilesMacros.jvm
lazy val profilesMacrosJS = profilesMacros.js

lazy val tests = crossProject
  .in(file("tests"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    nonPublishableSettings,
    description := "Integration tests of Scalameta",
    libraryDependencies += "junit" % "junit" % "4.12",
    libraryDependencies ++= (
      if (isDotty.value) Nil
      else Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    )
  )
  .dependsOn(core, profilesMacros)
lazy val testsJVM = tests.jvm
lazy val testsJS = tests.js

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
  crossScalaVersions := {
    // NOTE: Scala.js doesn't support Dotty yet
    val bannedLanguageVersions = if (isScalaJSProject.value) List(LatestDotty) else Nil
    LanguageVersions.diff(bannedLanguageVersions)
  },
  unmanagedSourceDirectories.in(Compile) += {
    val main = CrossType.Full.sharedSrcDir(baseDirectory.in(Compile).value, "main").get
    val epochSpecificName = if (isDotty.value) "scala-0" else "scala-2"
    main / ".." / epochSpecificName
  },
  unmanagedSourceDirectories.in(Test) += {
    val test = CrossType.Full.sharedSrcDir(baseDirectory.in(Test).value, "test").get
    val epochSpecificName = if (isDotty.value) "scala-0" else "scala-2"
    test / ".." / epochSpecificName
  },
  crossVersion := {
    crossVersion.value match {
      case old @ ScalaJSCrossVersion.binary => old
      case _ => CrossVersion.binary
    }
  },
  organization := "org.scalameta",
  scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings"),
  logBuffered := false,
  incOptions := incOptions.value.withLogRecompileOnMacro(false),
  updateOptions := updateOptions.value.withCachedResolution(true),
  triggeredMessage.in(ThisBuild) := Watched.clearWhenTriggered,
  buildInfoKeys := Seq[BuildInfoKey](
    scalaVersion,
    version
  ),
  buildInfoPackage := "scala.meta.internal.config." + name.value,
  buildInfoObject := "BuildInfo",
  libraryDependencies += "junit" % "junit" % "4.12" % "test",
  libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-n")
)

lazy val publishableSettings = Def.settings(
  sharedSettings,
  bintrayOrganization := Some("scalameta"),
  publishArtifact.in(Compile) := true,
  publishArtifact.in(Test) := false,
  publishMavenStyle := true,
  pomIncludeRepository := { x =>
    false
  },
  licenses += "BSD" -> url("https://github.com/scalameta/scalameta/blob/master/LICENSE.md"),
  pomExtra := (
    <url>https://github.com/scalameta/scalameta</url>
    <inceptionYear>2014</inceptionYear>
    <scm>
      <url>git://github.com/scalameta/scalameta.git</url>
      <connection>scm:git:git://github.com/scalameta/scalameta.git</connection>
    </scm>
    <issueManagement>
      <system>GitHub</system>
      <url>https://github.com/scalameta/scalameta/issues</url>
    </issueManagement>
    <developers>
      <developer>
        <id>xeno-by</id>
        <name>Eugene Burmako</name>
        <url>http://xeno.by</url>
      </developer>
      <developer>
        <id>densh</id>
        <name>Denys Shabalin</name>
        <url>http://den.sh</url>
      </developer>
      <developer>
        <id>olafurpg</id>
        <name>Ólafur Páll Geirsson</name>
        <url>https://geirsson.com/</url>
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
