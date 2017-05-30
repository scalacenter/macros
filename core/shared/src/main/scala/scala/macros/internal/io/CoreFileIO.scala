package scala.macros.internal
package io

import java.nio.charset._
import scala.macros.io._

object CoreFileIO {
  def slurp(path: AbsolutePath, charset: Charset): String =
    PlatformCoreFileIO.slurp(path, charset)
}
