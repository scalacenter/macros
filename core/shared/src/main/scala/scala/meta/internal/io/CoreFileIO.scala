package scala.meta
package internal
package io

import java.nio.charset._
import scala.meta.io._

object CoreFileIO {
  def slurp(path: AbsolutePath, charset: Charset): String =
    PlatformCoreFileIO.slurp(path, charset)
}
