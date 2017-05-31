package org.scalamacros
package build

import scala.compat.Platform.EOL
import scala.io._
import org.scalamacros.os._

trait Versions { self: ScalamacrosBuild =>

  lazy val LanguageVersion = sys.env.getOrElse("SCALA_VERSION", Scala211)
  lazy val LanguageVersions = List(Scala210, Scala211, Scala212, Scala213, Dotty)
  lazy val Scala210 = readScalaVersionFromDroneYml("2.10.x")
  lazy val Scala211 = readScalaVersionFromDroneYml("2.11.x")
  lazy val Scala212 = readScalaVersionFromDroneYml("2.12.x")
  lazy val Scala213 = readScalaVersionFromDroneYml("2.13.x")
  lazy val Dotty = readScalaVersionFromDroneYml("0.1.x")
  private def readScalaVersionFromDroneYml(series: String): String = {
    val lines = Source.fromFile(".drone.yml")(Codec.UTF8).getLines.toList
    val rxVersion = series.replace(".", "\\.").replace("x", ".*")
    val rxMatrixEntry = ("""^.*?SCALA_VERSION:\s*(""" + rxVersion + """)\s*$""").r
    val versions = lines.collect { case rxMatrixEntry(version) => version }
    versions.distinct match {
      case List(version) => version
      case Nil => sys.error(s"no Scala version $series found in .drone.yml")
      case versions => sys.error("multiple Scala versions $series found in .drone.yml")
    }
  }

  lazy val BuildVersion = computeProductVersionFromGit(BuildProduct).replace("2.0.0", "x.y.z")
  lazy val CoreVersion = computeProductVersionFromGit(CoreProduct)
  lazy val EngineScalacVersion = computeProductVersionFromGit(EngineScalacProduct)
  lazy val PluginScalacVersion = computeProductVersionFromGit(PluginScalacProduct)
  protected def computeProductVersionFromGit(product: String): String = {
    val currStableVersion: String = {
      val prefix = product + "-"
      val stdout = shell.check_output(s"git tag -l $prefix*").split(EOL).filter(_.nonEmpty).toList
      val latestTag = stdout.lastOption.getOrElse(prefix + "1.0.0")
      val latestVersion = latestTag.stripPrefix(prefix)
      val status = """(\d+)\.(\d+)\.(\d+)$""".r.unapplySeq(latestVersion)
      if (status.isEmpty) sys.error(s"unexpected latest tag $latestTag in $stdout")
      latestVersion
    }
    val nextStableVersion = {
      // TODO: It would be great to accommodate the difference between major and minor upgrades.
      // I believe we can implement this in a particularly neat way:
      //   * By default, bump the minor version of the product
      //   * If there's any commit since the latest tag that says "[$product breaking change] ..."
      //     then bump the major version of the product
      //   * Use the CI to verify that breaking changes are reported and reported correctly
      def fail() = sys.error(s"unexpected version series $currStableVersion")
      val rxSemVer = """^(\d+)\.(\d+)\.(\d+)$""".r
      currStableVersion match {
        case rxSemVer(s_currMajor, _, _) =>
          val currMajor = {
            try s_currMajor.toInt
            catch { case ex: Exception => fail() }
          }
          val nextMajor = currMajor + 1
          s"$nextMajor.0.0"
        case _ =>
          fail()
      }
    }
    val preReleasePrefix = nextStableVersion
    val preReleaseSuffix = {
      val gitDescribeSuffix = {
        val distance = os.git.distance("be06f2b229d93538f63e5ddde644fbde69e75afb", "HEAD")
        val currentSha = os.git.currentSha().substring(0, 8)
        s"$distance-$currentSha"
      }
      if (os.git.isStable()) gitDescribeSuffix
      else gitDescribeSuffix + "." + os.time.stamp
    }
    preReleasePrefix + "-" + preReleaseSuffix
  }
}
