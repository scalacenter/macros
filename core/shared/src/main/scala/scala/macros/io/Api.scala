package scala.macros
package io

private[macros] trait Api {
  // NOTE: We don't expose any extension methods
  // as part of the package's public API that will show up in scala.macros.
}

private[macros] trait Aliases {
  type AbsolutePath = scala.macros.io.AbsolutePath
  val AbsolutePath = scala.macros.io.AbsolutePath
}
