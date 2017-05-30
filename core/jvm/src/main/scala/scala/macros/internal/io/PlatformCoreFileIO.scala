package scala.macros.internal
package io

import java.nio.charset._
import scala.macros.io._

object PlatformCoreFileIO {
  def slurp(path: AbsolutePath, charset: Charset): String =
    scala.io.Source.fromFile(path.toFile)(scala.io.Codec(charset)).mkString
}
