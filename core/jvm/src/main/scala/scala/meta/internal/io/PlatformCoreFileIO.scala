package scala.meta.internal
package io

import java.nio.charset._
import scala.meta.io._

object PlatformCoreFileIO {
  def slurp(path: AbsolutePath, charset: Charset): String =
    scala.io.Source.fromFile(path.toFile)(scala.io.Codec(charset)).mkString
}
