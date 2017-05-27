package scala.meta.internal

import java.nio.charset._
import java.nio.file._
import scala.meta.inputs._
import scala.meta.prettyprinters._
import scala.meta.internal.prettyprinters._

package object inputs {
  implicit class XtensionPositionFormatMessage(pos: Position) {
    def formatMessage(severity: String, message: String): String = {
      // TODO: In order to be completely compatible with scalac, we need to support Position.point.
      // On the other hand, do we really need to? Let's try without it. See #383 for discussion.
      if (pos != Position.None) {
        val input = pos.input
        val header = s"${input.syntax}:${pos.startLine + 1}: $severity: $message"
        val line = {
          val start = input.lineToOffset(pos.startLine)
          val notEof = start < input.chars.length
          val end = if (notEof) input.lineToOffset(pos.startLine + 1) else start
          new String(input.chars, start, end - start).trim
        }
        var caret = " " * pos.startColumn + "^"
        header + EOL + line + EOL + caret
      } else {
        s"$severity: $message"
      }
    }
  }

  implicit val charsetStructure: Structure[Charset] = Structure { (p, x) =>
    p.raw(s"Charset.forName(").str(x.name).raw(")")
  }

  implicit val pathStructure: Structure[Path] = Structure { (p, x) =>
    p.raw(s"Paths.get(").str(x.toAbsolutePath.toString).raw(")")
  }
}
