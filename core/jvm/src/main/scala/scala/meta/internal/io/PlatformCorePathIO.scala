package scala.meta.internal
package io

import java.nio.file._

object PlatformCorePathIO {
  def isAbsolutePath(s: String): Boolean =
    Paths.get(s).isAbsolute
}
