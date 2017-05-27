package scala.meta
package io

private[scala] trait Api {
  // NOTE: We don't expose any extension methods
  // as part of the package's public API that will show up in scala.meta.
}

private[scala] trait Aliases {
  type AbsolutePath = scala.meta.io.AbsolutePath
  lazy val AbsolutePath = scala.meta.io.AbsolutePath
}
