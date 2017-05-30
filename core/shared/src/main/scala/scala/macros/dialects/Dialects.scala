package scala.macros
package dialects

private[scala] trait Dialects {
  implicit val Scala210: Dialect = Dialect(
    allowAndTypes = false,
    allowAtForExtractorVarargs = true,
    allowColonForExtractorVarargs = false,
    allowInlineIdents = true,
    allowInlineMods = false,
    allowLiteralTypes = false,
    allowMultilinePrograms = true,
    allowOrTypes = false,
    allowPatUnquotes = false,
    allowSpliceUnderscores = false, // SI-7715, only fixed in 2.11.0-M5
    allowTermUnquotes = false,
    allowToplevelTerms = false,
    allowTrailingCommas = false,
    allowTraitParameters = false,
    allowViewBounds = true,
    allowWithTypes = true,
    allowXmlLiterals = true, // Not even deprecated yet, so we need to support xml literals
    toplevelSeparator = ""
  )

  implicit val Sbt0136: Dialect = Scala210.copy(
    allowToplevelTerms = true,
    toplevelSeparator = scala.macros.internal.prettyprinters.EOL
  )

  implicit val Sbt0137: Dialect = Scala210.copy(
    allowToplevelTerms = true,
    toplevelSeparator = ""
  )

  implicit val Scala211: Dialect = Scala210.copy(
    allowSpliceUnderscores = true // SI-7715, only fixed in 2.11.0-M5
  )

  implicit val Typelevel211: Dialect = Scala211.copy(
    allowLiteralTypes = true
  )

  implicit val Paradise211: Dialect = Scala211.copy(
    allowInlineIdents = true,
    allowInlineMods = true
  )

  implicit val ParadiseTypelevel211: Dialect = Typelevel211.copy(
    allowInlineIdents = true,
    allowInlineMods = true
  )

  implicit val Scala212: Dialect = Scala211.copy(
    // NOTE: support for literal types is tentatively scheduled for 2.12.3
    // https://github.com/scala/scala/pull/5310#issuecomment-290617202
    allowLiteralTypes = false,
    allowTrailingCommas = true
  )

  implicit val Typelevel212: Dialect = Scala212.copy(
    allowLiteralTypes = true
  )

  implicit val Paradise212: Dialect = Scala212.copy(
    allowInlineIdents = true,
    allowInlineMods = true
  )

  implicit val ParadiseTypelevel212: Dialect = Typelevel212.copy(
    allowInlineIdents = true,
    allowInlineMods = true
  )

  implicit val Scala213: Dialect = Scala212.copy()

  implicit val Dotty: Dialect = Scala211.copy(
    allowAndTypes = true, // New feature in Dotty
    allowAtForExtractorVarargs = false, // New feature in Dotty
    allowColonForExtractorVarargs = true, // New feature in Dotty
    allowInlineIdents = false, // New feature in Dotty
    allowInlineMods = true, // New feature in Dotty
    allowLiteralTypes = true, // New feature in Dotty
    allowOrTypes = true, // New feature in Dotty
    allowTrailingCommas = false, // Not yet implemented in Dotty
    allowTraitParameters = true, // New feature in Dotty
    allowViewBounds = false, // View bounds have been removed in Dotty
    allowWithTypes = false, // New feature in Dotty
    allowXmlLiterals = false // Dotty parser doesn't have the corresponding code, so it can't really support xml literals
  )
}