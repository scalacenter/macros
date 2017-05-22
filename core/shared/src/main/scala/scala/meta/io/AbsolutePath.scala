package scala.meta
package io

import java.io._
import java.nio.file._
import scala.meta.internal.io._
import scala.meta.prettyprinters._

final class AbsolutePath private (value: String) extends Prettyprinted {
  protected def syntax(p: Prettyprinter): Unit = p.raw(value)
  protected def structure(p: Prettyprinter): Unit = {
    p.raw("AbsolutePath(Paths.get(").str(value).raw("))")
  }
  def toFile: File = this.toPath.toFile
  def toPath: Path = Paths.get(value)
}

object AbsolutePath {
  def apply(path: String): Option[AbsolutePath] = {
    if (CorePathIO.isAbsolutePath(path)) Some(new AbsolutePath(path))
    else None
  }

  def apply(file: File): AbsolutePath = {
    new AbsolutePath(file.getAbsolutePath)
  }

  def apply(path: Path): AbsolutePath = {
    new AbsolutePath(path.toAbsolutePath.toString)
  }
}
