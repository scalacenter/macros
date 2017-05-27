package scala.meta
package inputs

private[scala] trait Api {
  // NOTE: We don't expose any extension methods
  // as part of the package's public API that will show up in scala.meta.
}

private[scala] trait Aliases {
  type Input = scala.meta.inputs.Input
  lazy val Input = scala.meta.inputs.Input

  type Position = scala.meta.inputs.Position
  lazy val Position = scala.meta.inputs.Position
}
