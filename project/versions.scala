package org.scalameta
package build

import scala.io._

trait Versions {
  self: ScalametaBuild =>

  lazy val LanguageVersion = sys.env.getOrElse("SCALA_VERSION", LatestScala211)
  lazy val LanguageVersions = List(LatestScala210, LatestScala211, LatestScala212, LatestDotty)
  lazy val LibraryVersion = sys.env.getOrElse("SCALAMETA_VERSION", computeLibraryVersionFromGit())
  protected def computeLibraryVersionFromGit(): String = {
    // TODO: uncomment this once we tag v2.0.0
    // val currStableVersion: String = {
    //   val stdout = shell.check_output(s"git tag -l v*")
    //   val latestTag = stdout.split(EOL).last
    //   val status = """^v(\d+)\.(\d+)\.(\d+)$""".r.unapplySeq(latestTag)
    //   if (status.isEmpty) sys.error(s"unexpected latest tag $latestTag in$EOL$stdout")
    //   latestTag.stripPrefix("v")
    // }
    // val nextStableVersion = {
    //   def fail() = sys.error(s"unexpected version series $currStableVersion")
    //   val rxKindaSemVer = """^(\d+)\.(\d+)\.(\d+)$""".r
    //   currStableVersion match {
    //     case rxKindaSemVer(s_currEpoch, s_currMajor, _) =>
    //       val currMajor = {
    //         try s_currMajor.toInt
    //         catch { case ex: Exception => fail() }
    //       }
    //       val nextMajor = currMajor + 1
    //       s"$s_currEpoch.$nextMajor.0"
    //     case _ =>
    //       fail()
    //   }
    // }
    // val preReleasePrefix = nextStableVersion
    val preReleasePrefix = "2.0.0"
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
}
