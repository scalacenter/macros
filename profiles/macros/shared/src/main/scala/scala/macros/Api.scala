package scala.macros

private[scala] trait Api {
  // NOTE: We don't expose any extension methods
  // as part of the package's public API that will show up in scala.macros.
}

private[scala] trait Aliases {
  type Dialect = scala.meta.Dialect
  val Dialect = scala.meta.Dialect
}
