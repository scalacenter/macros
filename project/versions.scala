package org.scalamacros
package build

import scala.compat.Platform.EOL
import scala.io._
import org.scalamacros.os._

trait Versions { self: ScalamacrosBuild =>

  lazy val LanguageVersion = sys.env.getOrElse("SCALA_VERSION", Scala212)
  lazy val LanguageVersions = List(Scala212, Scala213, Dotty)
  lazy val Scala212 = "2.12.2"
  lazy val Scala213 = "2.13.0-M1"
  lazy val Dotty = "0.2.0-RC1"

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
        val root = shell.check_output(s"git rev-list --max-parents=0 HEAD")
        val distance = os.git.distance(root, "HEAD")
        val currentSha = os.git.currentSha().substring(0, 8)
        s"$distance-$currentSha"
      }
      if (os.git.isStable()) gitDescribeSuffix
      else gitDescribeSuffix + "." + os.time.stamp
    }
    preReleasePrefix + "-" + preReleaseSuffix
  }
}
