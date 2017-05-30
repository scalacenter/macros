package scala.macros

import scala.macros.internal.dialects._
import scala.macros.internal.prettyprinters._

// NOTE: Can't put Dialect into scala.macros.dialects,
// because then implicit scope for Dialect lookups will contain members of the package object,
// e.g. both Scala211 and Dotty, which is definitely not what we want.
final case class Dialect(
    // Are `&` intersection types supported by this dialect?
    allowAndTypes: Boolean,
    // Are extractor varargs specified using ats, i.e. is `case Extractor(xs @ _*)` legal or not?
    allowAtForExtractorVarargs: Boolean,
    // Are extractor varargs specified using colons, i.e. is `case Extractor(xs: _*)` legal or not?
    allowColonForExtractorVarargs: Boolean,
    // Are `inline` identifiers supported by this dialect?
    allowInlineIdents: Boolean,
    // Are inline vals and defs supported by this dialect?
    allowInlineMods: Boolean,
    // Are literal types allowed, i.e. is `val a : 42 = 42` legal or not?
    allowLiteralTypes: Boolean,
    // Are multiline programs allowed?
    // Some quasiquotes only support single-line snippets.
    allowMultilinePrograms: Boolean,
    // Are `|` (union types) supported by this dialect?
    allowOrTypes: Boolean,
    // Are unquotes ($x) and splices (..$xs, ...$xss) allowed?
    // If yes, they will be parsed as patterns.
    allowPatUnquotes: Boolean,
    // Are naked underscores allowed after $ in pattern interpolators, i.e. is `case q"$_ + $_" =>` legal or not?
    allowSpliceUnderscores: Boolean,
    // Are unquotes ($x) and splices (..$xs, ...$xss) allowed?
    // If yes, they will be parsed as terms.
    allowTermUnquotes: Boolean,
    // Are terms on the top level supported by this dialect?
    // Necessary to support popular script-like DSLs.
    allowToplevelTerms: Boolean,
    // Are trailing commas allowed? SIP-27.
    allowTrailingCommas: Boolean,
    // Are trait allowed to have parameters?
    // They are in Dotty, but not in Scala 2.12 or older.
    allowTraitParameters: Boolean,
    // Are view bounds supported by this dialect?
    // Removed in Dotty.
    allowViewBounds: Boolean,
    // Are `with` intersection types supported by this dialect?
    allowWithTypes: Boolean,
    // Are XML literals supported by this dialect?
    // We plan to deprecate XML literal syntax, and some dialects
    // might go ahead and drop support completely.
    allowXmlLiterals: Boolean,
    // What kind of separator is necessary to split top-level statements?
    // Normally none is required, but scripts may have their own rules.
    toplevelSeparator: String
) extends Pretty {
  // Are unquotes ($x) and splices (..$xs, ...$xss) allowed?
  def allowUnquotes: Boolean = allowTermUnquotes || allowPatUnquotes

  // Dialects have reference equality semantics,
  // because sometimes dialects representing distinct Scala versions
  // can be structurally equal to each other.
  override def canEqual(that: Any): Boolean = this eq that.asInstanceOf[AnyRef]
  override def equals(other: Any): Boolean = this eq other.asInstanceOf[AnyRef]
  override def hashCode: Int = System.identityHashCode(this)

  override protected def syntax(p: Prettyprinter): Unit = structure(p)
  override protected def structure(p: Prettyprinter): Unit = {
    val renderStandardName = Dialect.standards.get(this).map(p.raw)
    renderStandardName.getOrElse(super.structure(p))
  }
}

object Dialect extends InternalDialect {
  // NOTE: Spinning up a macro just for this is too hard.
  // Using JVM reflection won't be portable to Scala.js.
  private[macros] lazy val standards: Map[Dialect, String] = {
    import scala.macros.dialects._
    Map(
      Scala210 -> "Scala210",
      Sbt0136 -> "Sbt0136",
      Sbt0137 -> "Sbt0137",
      Scala211 -> "Scala211",
      Typelevel211 -> "Typelevel211",
      Paradise211 -> "Paradise211",
      ParadiseTypelevel211 -> "ParadiseTypelevel211",
      Scala212 -> "Scala212",
      Typelevel212 -> "Typelevel212",
      Paradise212 -> "Paradise212",
      ParadiseTypelevel212 -> "ParadiseTypelevel212",
      Scala213 -> "Scala213",
      Dotty -> "Dotty"
    )
  }
}
