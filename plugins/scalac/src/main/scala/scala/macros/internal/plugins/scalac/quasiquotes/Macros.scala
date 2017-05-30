package scala.macros.internal
package plugins.scalac
package quasiquotes

import scala.language.implicitConversions
import scala.reflect.macros.whitebox.Context
import scala.meta.internal.parsers.Absolutize._
import scala.{meta => m}

class Macros(val c: Context) {
  import c.{universe => g}
  import g.{Quasiquote, TreeTag, IdentTag, LiteralTag}

  def apply(args: g.Tree*): g.Tree = expand
  def unapply(tree: g.Tree): g.Tree = expand

  sealed trait Mode {
    def isTerm: Boolean = this.isInstanceOf[Mode.Term]
    def isPattern: Boolean = this.isInstanceOf[Mode.Pattern]
    def multiline: Boolean
  }
  object Mode {
    case class Term(prefix: String, multiline: Boolean) extends Mode
    case class Pattern(prefix: String, multiline: Boolean, selectorDummy: g.Tree) extends Mode
  }

  private def expand: g.Tree = {
    val (input, mode) = obtainParameters()
    val skeleton = parseSkeleton(input, mode)
    reifySkeleton(skeleton, mode)
  }

  private def obtainParameters(): (m.Input, Mode) = {
    val expandee = c.macroApplication
    val source = expandee.pos.source
    val (parts, mode) = {
      def isMultiline(fstPart: g.Tree) = {
        source.content(fstPart.pos.start - 2) == '"'
      }
      try {
        expandee match {
          case q"$_($_.apply(..$parts)).$prefix.apply[..$_](..$args)" =>
            require(parts.length == args.length + 1)
            (parts, Mode.Term(prefix.toString, isMultiline(parts.head)))
          case q"$_($_.apply(..$parts)).$prefix.unapply[..$_](${selectorDummy: g.Ident})" =>
            require(selectorDummy.name == g.TermName("<unapply-selector>"))
            val args = c.internal.subpatterns(selectorDummy).get
            require(parts.length == args.length + 1)
            (parts, Mode.Pattern(prefix.toString, isMultiline(parts.head), selectorDummy))
        }
      } catch {
        case ex: Exception =>
          val message = s"unexpected quasiquote expandee: $expandee ${g.showRaw(expandee)}"
          c.abort(expandee.pos, message)
      }
    }
    val input = {
      val (fstPart, lstPart @ g.Literal(g.Constant(s_lastpart: String))) = (parts.head, parts.last)
      val start = fstPart.pos.start // looks like we can trust this position to point to the character right after the opening quotes
      val end = { // we have to infer this for ourselves, because there's no guarantee we run under -Yrangepos
        var remaining = s_lastpart.length
        var curr = lstPart.pos.start - 1
        while (remaining > 0) {
          curr += 1
          if (source.content(curr) == '$') {
            curr += 1
            require(source.content(curr) == '$')
          }
          remaining -= 1
        }
        curr + 1
      }
      val result = {
        val content = new String(source.content)
        if (source.file.file != null) m.Input.LabeledString(source.path, content)
        else m.Input.String(content)
      }
      m.Input.Slice(result, start, end)
    }
    (input, mode)
  }

  private def parseSkeleton(input: m.Input, mode: Mode): m.Tree = {
    try {
      println(input)
      println(mode)
      ???
    } catch {
      case m.TokenizeException(pos, message) => c.abort(pos, message)
      case m.ParseException(pos, message) => c.abort(pos, message)
    }
  }

  private def reifySkeleton(mtree: m.Tree, mode: Mode): g.Tree = {
    ???
  }

  private implicit def mpositionToGposition(pos: m.Position): g.Position = {
    // TODO: this is another instance of #383
    c.macroApplication.pos.focus.withPoint(pos.absolutize.start)
  }
}
