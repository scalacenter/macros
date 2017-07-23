package scala.macros
package inputs

import java.nio.charset._
import java.nio.file._
import scala.io._
import scala.macros.internal.inputs._
import scala.macros.internal.prettyprinters._

trait Input extends Pretty with InternalInput {
  def chars: Array[Char]
  def text: String = new String(chars)
  protected def syntax(p: Prettyprinter) = p.raw("<" + productPrefix.toLowerCase + ">")
  override protected def structure(p: Prettyprinter) = super.structure(p.raw("Input."))
}

object Input {
  case object None extends Input {
    lazy val chars = new Array[Char](0)
  }

  final case class File(path: Path, charset: Charset) extends Input {
    lazy val chars = scala.io.Source.fromFile(path.toFile)(Codec(charset)).mkString.toArray
    override protected def syntax(p: Prettyprinter) = p.stx(path.toString)
    override protected def structure(p: Prettyprinter) = {
      p.raw("Input.File(Paths.get(").str(path.toString).raw(")")
      if (charset.name != "UTF-8") p.raw(", ").str(charset)
      p.raw(")")
    }
  }
  object File {
    def apply(path: Path): File = apply(path, Charset.forName("UTF-8"))
  }

  final case class VirtualFile(label: String, contents: String) extends Input {
    lazy val chars = contents.toArray
    override protected def syntax(p: Prettyprinter) = p.raw(label)
  }
}
