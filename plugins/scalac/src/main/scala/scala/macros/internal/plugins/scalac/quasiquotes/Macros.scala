package scala.macros.internal
package plugins.scalac
package quasiquotes

import scala.language.implicitConversions
import scala.reflect.macros.whitebox.Context
import scala.macros.internal.trees.Errors
import scala.meta.internal.parsers.Absolutize._
import scala.meta.internal.trees.Quasi
import scala.{meta => m}

class Macros(val c: Context) {
  import c.{universe => g}
  import g.{Quasiquote, TreeTag, IdentTag, LiteralTag, BindTag}

  def apply(args: g.Tree*): g.Tree = expand
  def unapply(tree: g.Tree): g.Tree = expand

  private case class Hole(name: g.TermName, arg: g.Tree) { def pos = arg.pos }
  private sealed trait Mode {
    def isTerm: Boolean = this.isInstanceOf[Mode.Term]
    def isPattern: Boolean = this.isInstanceOf[Mode.Pattern]
    def name: String
    def multi: Boolean
    def holes: List[Hole]
  }
  private object Mode {
    case class Term(name: String, multi: Boolean, holes: List[Hole]) extends Mode
    case class Pattern(name: String, multi: Boolean, dummy: g.Tree, holes: List[Hole]) extends Mode
  }

  private val QuasiquotePrefix = c.freshName("qq")
  private def holeName(i: Int) = g.TermName(QuasiquotePrefix + "$hole$" + i)
  private def resultName(i: Int) = g.TermName(QuasiquotePrefix + "$result$" + i)

  private def expand: g.Tree = {
    val (input, mode) = obtainParameters()
    val skeleton = parseSkeleton(input, mode)
    reifySkeleton(skeleton, mode)
  }

  private def obtainParameters(): (m.Input, Mode) = {
    val expandee = c.macroApplication
    val source = expandee.pos.source
    val (parts, mode) = {
      def isMulti(fstPart: g.Tree) = source.content(fstPart.pos.start - 2) == '"'
      def mkHole(argi: (g.Tree, Int)) = Hole(holeName(argi._2), argi._1)
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

  private def reifySkeleton(tree: m.Tree, mode: Mode): g.Tree = {
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
    def unquote(quasi: m.Tree, optional: Boolean): g.Tree = {
      val pos = quasi.pos.absolutize
      val hole = mode.holes.find(h => pos.start <= h.pos.point && h.pos.point <= pos.end).get
      if (mode.isTerm) {
        if (optional) {
          val captureOption = hole.arg.tpe.baseType(g.definitions.OptionClass) != g.NoType
          if (captureOption) hole.arg else q"_root_.scala.Some(${hole.arg})"
        } else {
          hole.arg
        }
      } else {
        pq"${hole.name}"
      }
    }
    def trees(trees: List[m.Tree]): g.Tree = {
      def loop(trees: List[m.Tree], acc: g.Tree, prefix: List[m.Tree]): g.Tree = trees match {
        case (quasi: Quasi) +: rest if quasi.rank == 1 =>
          if (acc.isEmpty) {
            if (prefix.isEmpty) loop(rest, reify(quasi), Nil)
            else loop(rest, prefix.foldRight(acc)((curr, acc) => {
              val currElement = reify(curr)
              val alreadyLiftedList = acc.orElse(reify(quasi))
              if (mode.isTerm) q"$currElement +: $alreadyLiftedList"
              else pq"$currElement +: $alreadyLiftedList"
            }), Nil)
          } else {
            if (mode.isTerm) loop(rest, q"$acc ++ ${reify(quasi)}", Nil)
            else c.abort(quasi.pos, Errors.QuasiquoteAdjacentEllipsesInPattern(quasi.rank))
          }
        case other +: rest =>
          if (acc.isEmpty) loop(rest, acc, prefix :+ other)
          else {
            if (mode.isTerm) loop(rest, q"$acc :+ ${reify(other)}", Nil)
            else loop(rest, pq"$acc :+ ${reify(other)}", Nil)
          }
        case Nil =>
          if (acc.isEmpty) q"_root_.scala.List(..${prefix.map(reify)})"
          else acc
      }
      loop(trees, g.EmptyTree, Nil)
    }
    def treess(treess: List[List[m.Tree]]): g.Tree = {
      val tripleDotQuasis = treess.flatten.collect{ case quasi: Quasi if quasi.rank == 2 => quasi }
      if (tripleDotQuasis.length == 0) {
        apply("scala.List", treess)
      } else if (tripleDotQuasis.length == 1) {
        if (treess.flatten.length == 1) reify(tripleDotQuasis(0))
        else c.abort(tripleDotQuasis(0).pos, Errors.QuasiquoteTripleDotImplementationRestriction)
      } else {
        c.abort(tripleDotQuasis(1).pos, Errors.QuasiquoteAdjacentEllipsesInPattern(2))
      }
    }
    def reify(x: Any): g.Tree = x match {
      case x: Quasi => unquote(x, optional = false)
      case Some(x: Quasi) => unquote(x, optional = true)
      case x: m.Tree => apply("scala.macros." + x.productPrefix, x.productIterator.toList)
      case Nil => path("scala.Nil")
      case xss @ List(_: List[_], _*) => treess(xss.asInstanceOf[List[List[m.Tree]]])
      case xs @ List(_*) => trees(xs.asInstanceOf[List[m.Tree]])
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
    mode match {
      case Mode.Term(_, _, _) =>
        reify(tree)
      case Mode.Pattern(_, _, dummy, holes) =>
        // inspired by https://github.com/densh/joyquote/blob/master/src/main/scala/JoyQuote.scala
        val pattern = reify(tree)
        val (thenp, elsep) = {
          if (holes.isEmpty) (q"true", q"false")
          else {
            val resultNames = holes.zipWithIndex.map({ case (_, i) => resultName(i) })
            val resultPatterns = resultNames.map(name => pq"$name")
            val resultTerms = resultNames.map(name => q"$name")
            val thenp = q"""
              (..${holes.map(_.name)}) match {
                case (..$resultPatterns) => _root_.scala.Some((..$resultTerms))
                case _ => _root_.scala.None
              }
            """
            (thenp, q"_root_.scala.None")
          }
        }
        val matchp = pattern match {
          case g.Bind(_, g.Ident(g.termNames.WILDCARD)) => q"tree match { case $pattern => $thenp }"
          case _ => q"tree match { case $pattern => $thenp; case _ => $elsep }"
        }
        q"new { def unapply(tree: _root_.scala.Any) = $matchp }.unapply($dummy)"
    }
  }

  private implicit def mpositionToGposition(pos: m.Position): g.Position = {
    // TODO: this is another instance of #383
    c.macroApplication.pos.focus.withPoint(pos.absolutize.start)
  }
}
