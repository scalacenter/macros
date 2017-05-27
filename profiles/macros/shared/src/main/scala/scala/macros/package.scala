package scala

package object macros
    extends scala.macros.config.Api
    with scala.macros.config.Aliases
    with scala.macros.dialects.Api
    with scala.macros.dialects.Aliases
    with scala.macros.inputs.Api
    with scala.macros.inputs.Aliases
    with scala.macros.io.Api
    with scala.macros.io.Aliases
    with scala.macros.prettyprinters.Api
    with scala.macros.prettyprinters.Aliases
    with scala.macros.Api
    with scala.macros.Aliases
    with scala.macros.Universe {

  private[scala] val universe = new ThreadLocal[scala.macros.Universe]
  private[scala] def abstracts = universe.get.abstracts.asInstanceOf[Abstracts]
}
