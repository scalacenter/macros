package scala.meta
package inputs

import java.io.{File => JFile, _}
import java.lang.{String => JString}
import java.nio.charset._
import java.nio.file._
import scala.meta.internal.inputs._
import scala.meta.internal.io._
import scala.meta.io._
import scala.meta.prettyprinters._

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

  final case class String(s: JString) extends Input {
    lazy val chars = s.toArray
  }

  final case class Stream(stream: InputStream, charset: Charset) extends Input {
    lazy val chars = new JString(CoreStreamIO.readBytes(stream), charset).toArray
    override protected def structure(p: Prettyprinter) = {
      p.raw("Input.Stream(<stream>")
      if (charset.name != "UTF-8") p.raw(", ").str(charset)
      p.raw(")")
    }
  }
  object Stream {
    def apply(stream: InputStream): Stream = apply(stream, Charset.forName("UTF-8"))
  }

  final case class LabeledString(label: JString, contents: JString) extends Input {
    lazy val chars = contents.toArray
    override protected def syntax(p: Prettyprinter) = p.raw(label)
  }

  final case class File(path: AbsolutePath, charset: Charset) extends Input {
    lazy val chars = CoreFileIO.slurp(path, charset).toArray
    override protected def syntax(p: Prettyprinter) = p.stx(path)
    override protected def structure(p: Prettyprinter) = {
      p.raw("Input.File(").str(path.toPath)
      if (charset.name != "UTF-8") p.raw(", ").str(charset)
      p.raw(")")
    }
  }
  object File {
    def apply(path: AbsolutePath): File = apply(path, Charset.forName("UTF-8"))
    def apply(path: Path, charset: Charset): File = apply(AbsolutePath(path), charset)
    def apply(path: Path): File = apply(path, Charset.forName("UTF-8"))
    def apply(file: JFile, charset: Charset): File = apply(AbsolutePath(file), charset)
    def apply(file: JFile): File = apply(file, Charset.forName("UTF-8"))
  }

  // NOTE: `start` and `end` are String.substring-style,
  // i.e. `start` is inclusive and `end` is not.
  // Therefore Slice.end can point to the last character of input plus one.
  final case class Slice(input: Input, start: Int, end: Int) extends Input {
    lazy val chars = input.chars.slice(start, end)
  }
}
