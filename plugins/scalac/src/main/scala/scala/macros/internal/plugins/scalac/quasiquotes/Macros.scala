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
  val QuasiquotePrefix = c.freshName("quasiquote")

  def apply(args: g.Tree*): g.Tree = expand
  def unapply(tree: g.Tree): g.Tree = expand

  case class Hole(name: g.TermName, arg: g.Tree)
  sealed trait Mode {
    def isTerm: Boolean = this.isInstanceOf[Mode.Term]
    def isPattern: Boolean = this.isInstanceOf[Mode.Pattern]
    def name: String
    def multi: Boolean
    def holes: List[Hole]
  }
  object Mode {
    case class Term(name: String, multi: Boolean, holes: List[Hole]) extends Mode
    case class Pattern(name: String, multi: Boolean, dummy: g.Tree, holes: List[Hole]) extends Mode
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
      def isMulti(fstPart: g.Tree) = {
        source.content(fstPart.pos.start - 2) == '"'
      }
      def mkHole(argi: (g.Tree, Int)) = {
        val (arg, i) = argi
        val name = g.TermName(QuasiquotePrefix + "$hole$" + i)
        Hole(name, arg)
      }
      try {
        expandee match {
          case q"$_($_.apply(..$parts)).$name.apply[..$_](..$args)" =>
            require(parts.length == args.length + 1)
            val holes = args.zipWithIndex.map(mkHole)
            (parts, Mode.Term(name.toString, isMulti(parts.head), holes))
          case q"$_($_.apply(..$parts)).$name.unapply[..$_](${dummy: g.Ident})" =>
            require(dummy.name == g.TermName("<unapply-selector>"))
            val args = c.internal.subpatterns(dummy).get
            require(parts.length == args.length + 1)
            val holes = args.zipWithIndex.map(mkHole)
            (parts, Mode.Pattern(name.toString, isMulti(parts.head), dummy, holes))
        }
      } catch {
        case _: Exception =>
          sys.error(s"unexpected quasiquote expandee: $expandee ${g.showRaw(expandee)}")
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
      val dialect = m.Dialect(
        allowAndTypes = true,
        allowAtForExtractorVarargs = true,
        allowColonForExtractorVarargs = true,
        allowInlineIdents = true,
        allowInlineMods = true,
        allowLiteralTypes = true,
        allowMultilinePrograms = mode.multi,
        allowOrTypes = true,
        allowPatUnquotes = mode.isPattern,
        allowSpliceUnderscores = true,
        allowTermUnquotes = mode.isTerm,
        allowToplevelTerms = false,
        allowTrailingCommas = true,
        allowTraitParameters = true,
        allowViewBounds = true,
        allowWithTypes = true,
        allowXmlLiterals = true,
        toplevelSeparator = ""
      )
      val parsee = dialect(input)
      val parsed = mode.name match {
        case "q" => parsee.parse[m.Ctor].orElse(parsee.parse[m.Stat])
        case "param" => parsee.parse[m.Term.Param]
        case "t" => parsee.parse[m.Type]
        case "tparam" => parsee.parse[m.Type.Param]
        case "p" => parsee.parse[m.Case].orElse(parsee.parse[m.Pat])
        case "init" => parsee.parse[m.Init]
        case "self" => parsee.parse[m.Self]
        case "template" => parsee.parse[m.Template]
        case "mod" => parsee.parse[m.Mod]
        case "enumerator" => parsee.parse[m.Enumerator]
        case "importer" => parsee.parse[m.Importer]
        case "importee" => parsee.parse[m.Importee]
        case "source" => parsee.parse[m.Source]
        case other => sys.error(s"unexpected quasiquote interpolator: $other")
      }
      parsed.get
    } catch {
      case m.TokenizeException(pos, message) => c.abort(pos, message)
      case m.ParseException(pos, message) => c.abort(pos, message)
    }
  }

  private def reifySkeleton(mtree: m.Tree, mode: Mode): g.Tree = {
    def path(fqn: String): g.Tree = {
      val frags = fqn.split("\\.").toList
      val root = g.Ident(g.TermName("_root_")): g.Tree
      frags.foldLeft(root)((acc, curr) => g.Select(acc, g.TermName(curr)))
    }
    def apply(fqn: String, args: List[Any]): g.Tree = {
      val reifiedFqn = path(fqn)
      val reifiedArgs = args.map(reify)
      g.Apply(reifiedFqn, reifiedArgs)
    }
    def reify(x: Any): g.Tree = x match {
      case x: m.Tree => apply("scala.macros." + x.productPrefix, x.productIterator.toList)
      case Nil => path("scala.Nil")
      case xs: List[_] => apply("scala.List", xs)
      case None => path("scala.None")
      case x: Some[_] => apply("scala.Some", List(x.get))
      case x: Boolean => g.Literal(g.Constant(x))
      case x: Byte => g.Literal(g.Constant(x))
      case x: Short => g.Literal(g.Constant(x))
      case x: Char => g.Literal(g.Constant(x))
      case x: Int => g.Literal(g.Constant(x))
      case x: Float => g.Literal(g.Constant(x))
      case x: Long => g.Literal(g.Constant(x))
      case x: Double => g.Literal(g.Constant(x))
      case x: String => g.Literal(g.Constant(x))
      case x: Symbol => g.Literal(g.Constant(x))
      case other => sys.error(s"unexpected reifee: ${other.getClass} $other")
    }
    reify(mtree)
  }

  private implicit def mpositionToGposition(pos: m.Position): g.Position = {
    // TODO: this is another instance of #383
    c.macroApplication.pos.focus.withPoint(pos.absolutize.start)
  }
}
