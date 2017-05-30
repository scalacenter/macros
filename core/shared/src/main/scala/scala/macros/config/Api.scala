package scala.macros
package config

import scala.macros.internal.config.scalamacros.BuildInfo

private[scala] trait Api {
  private def ensureVersion(key: String, value: String): Version = {
    def fail = sys.error(s"fatal error reading BuildInfo: $key $value is not a valid version")
    Version.parse(value).getOrElse(fail)
  }
  lazy val scalaVersion: Version = ensureVersion("scalaVersion", BuildInfo.scalaVersion)
  lazy val coreVersion: Version = ensureVersion("version", BuildInfo.version)
}

private[scala] trait Aliases {
  type Version = scala.macros.config.Version
  val Version = scala.macros.config.Version
}
