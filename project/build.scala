import bintray.BintrayPlugin.autoImport._
import ch.epfl.scala.sbt.release.ReleaseEarlyPlugin.autoImport._
import com.typesafe.sbt.SbtPgp.autoImport._
import dotty.tools.sbtplugin.DottyPlugin.autoImport._
import sbt.Def
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin
import sbtassembly.AssemblyPlugin.autoImport._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object Version {
  val scala212 = "2.12.3"
  val scala213 = "2.13.0-M1"
  val dotty = "0.3.0-RC2"
}

object ScalamacrosBuildPlugin extends AutoPlugin {
  import Version._
  override def requires: Plugins = JvmPlugin && BuildInfoPlugin
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    lazy val pluginSettings = Def.settings(
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

    lazy val noPublish = Def.settings(
      publishArtifact.in(Compile, packageDoc) := false,
      publishArtifact.in(packageDoc) := false,
      sources.in(Compile, doc) := Seq.empty,
      packagedArtifacts := Map.empty,
      publishArtifact := false,
      publish := {}
    )
  }

  override def globalSettings: Seq[Def.Setting[_]] = List(
    scalaVersion := scala212,
    crossScalaVersions := List(scala212),
    crossVersion := CrossVersion.binary,
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings"),
    logBuffered := false,
    incOptions := incOptions.value.withLogRecompileOnMacro(false),
    updateOptions := updateOptions.value.withCachedResolution(true),
    triggeredMessage.in(ThisBuild) := Watched.clearWhenTriggered,
    libraryDependencies ++= List(
      "junit" % "junit" % "4.12" % Test,
      "com.novocode" % "junit-interface" % "0.11" % Test
    ),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-n"),
    organization := "org.scalamacros",
    licenses := Seq(
      "BSD" -> url("https://github.com/scalamacros/scalamacros/blob/master/LICENSE.md")
    ),
    homepage := Some(url(s"https://github.com/scalacenter/macros")),
    scmInfo := Some(
      ScmInfo(
        url(s"https://github.com/scalacenter/macros"),
        "scm:git:git@github.com:scalacenter/macros.git"
      )
    ),
    bintrayOrganization := Some("scalamacros"),
    publishArtifact.in(Compile) := true,
    publishArtifact.in(Test) := false,
    publishMavenStyle := true,
    pomIncludeRepository := { x =>
      false
    },
    developers := List(
      Developer(
        "@xeno-by",
        "Eugene Burmako",
        "xeno.by@gmail.com",
        url("http://xeno.by")
      ),
      Developer(
        "@liufengyun",
        "Liu Fengyun",
        "liu@fengy.me",
        url("http://fengy.me")
      ),
      Developer(
        "@mutcianm",
        "Mikhail Mutcianko",
        "mutcianko.m@gmail.com",
        url("https://github.com/mutcianm")
      ),
      Developer(
        "@olafurpg",
        "Olafur Pall Geirsson",
        "olafurpg@gmail.com",
        url("http://geirsson.com")
      )
    ),
    PgpKeys.pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray()),
    releaseEarlyWith := BintrayPublisher
  )

  override def projectSettings: Seq[Def.Setting[_]] = List(
    buildInfoPackage := "scala.macros.internal.config." + name.value,
    buildInfoKeys := Seq[BuildInfoKey](
      scalaVersion,
      version,
      name,
      moduleName
    ),
    buildInfoObject := "BuildInfo",
    unmanagedSourceDirectories.in(Compile) += {
      val main = baseDirectory.in(Compile).value / "src" / "main"
      val epochSpecificName = if (isDotty.value) "scala-0" else "scala-2"
      main / epochSpecificName
    },
    unmanagedSourceDirectories.in(Test) += {
      val test = baseDirectory.in(Test).value / "src" / "test"
      val epochSpecificName = if (isDotty.value) "scala-0" else "scala-2"
      test / epochSpecificName
    }
  )

}
