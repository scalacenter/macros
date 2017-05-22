package scala.meta
package inputs

private[scala] trait Api {}

private[scala] trait Aliases {
  type Input = scala.meta.inputs.Input
  lazy val Input = scala.meta.inputs.Input

  type Position = scala.meta.inputs.Position
  lazy val Position = scala.meta.inputs.Position
}
