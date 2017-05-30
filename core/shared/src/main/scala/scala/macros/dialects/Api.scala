package scala.macros
package dialects

private[scala] trait Api {
  // NOTE: We don't expose any extension methods
  // as part of the package's public API that will show up in scala.macros.
}

private[scala] trait Aliases {
  // NOTE: We don't expose any definitions inside this package
  // as part of the package's public API that will show up in scala.macros.
  // See comments in Dialect.scala for a reason why this didn't work out.
}
