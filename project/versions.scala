package org.scalameta
package build

import scala.compat.Platform.EOL
import scala.io._
import org.scalameta.os._

trait Versions {
  self: ScalametaBuild =>

  lazy val LanguageVersion = sys.env.getOrElse("SCALA_VERSION", LatestScala211)
  lazy val LanguageVersions = List(LatestScala210, LatestScala211, LatestScala212, LatestDotty)
  lazy val LatestScala210 = readScalaVersionFromDroneYml("2.10.x")
  lazy val LatestScala211 = readScalaVersionFromDroneYml("2.11.x")
  lazy val LatestScala212 = readScalaVersionFromDroneYml("2.12.x")
  lazy val LatestDotty = readScalaVersionFromDroneYml("0.1.x")
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

  lazy val CoreVersion = computeProductVersionFromGit("core")
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
        val distance = os.git.distance("v1.0.0", "HEAD")
        val currentSha = os.git.currentSha().substring(0, 8)
        s"$distance-$currentSha"
      }
      if (os.git.isStable()) gitDescribeSuffix
      else gitDescribeSuffix + "." + os.time.stamp
    }
    preReleasePrefix + "-" + preReleaseSuffix
  }
}
