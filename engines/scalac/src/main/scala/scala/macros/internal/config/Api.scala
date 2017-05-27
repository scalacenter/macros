package scala.macros.internal
package config

import scala.meta.internal.config.enginesScalac.BuildInfo
import scala.macros.Version

private[scala] trait Api {
  private def ensureVersion(key: String, value: String): Version = {
    def fail = sys.error(s"fatal error reading BuildInfo: $key $value is not a valid version")
    Version.parse(value).getOrElse(fail)
  }
  lazy val engineVersion: Version = ensureVersion("engineVersion", BuildInfo.version)
}

private[scala] trait Aliases {
  // NOTE: We don't expose any definitions inside this package
  // as part of the package's public API that will show up in scala.macros.
}
