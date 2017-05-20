import org.scalameta.build._
import org.scalajs.sbtplugin.ScalaJSCrossVersion

// ==========================================
// Projects
// ==========================================

{
  println(s"[info] Welcome to scalameta $LibraryVersion")
  name := "scalametaRoot"
}
sharedSettings
nonPublishableSettings
commands += CiCommand("ci-test", List("test"))
commands += CiCommand("ci-publish", List("publish"))

// ==========================================
// Settings
// ==========================================

lazy val sharedSettings = Def.settings(
  scalaVersion := LanguageVersion,
  crossScalaVersions := {
    // NOTE: Scala.js doesn't support Dotty yet
    val bannedLanguageVersions = if (isScalaJSProject.value) List(LatestDotty) else Nil
    LanguageVersions.diff(bannedLanguageVersions)
  },
  crossVersion := {
    crossVersion.value match {
      case old @ ScalaJSCrossVersion.binary => old
      case _ => CrossVersion.binary
    }
  },
  version := LibraryVersion,
  organization := "org.scalameta",
  scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings"),
  logBuffered := false,
  incOptions := incOptions.value.withLogRecompileOnMacro(false),
  updateOptions := updateOptions.value.withCachedResolution(true),
  triggeredMessage.in(ThisBuild) := Watched.clearWhenTriggered
)

lazy val publishableSettings = Def.settings(
  sharedSettings,
  bintrayOrganization := Some("scalameta"),
  publishArtifact.in(Compile) := true,
  publishArtifact.in(Test) := false,
  publishMavenStyle := true,
  pomIncludeRepository := { x => false },
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
