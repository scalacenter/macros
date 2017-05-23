package scala

package object macros
    extends scala.meta.config.Api
    with scala.meta.config.Aliases
    with scala.meta.dialects.Api
    with scala.meta.dialects.Aliases
    with scala.meta.inputs.Api
    with scala.meta.inputs.Aliases
    with scala.meta.io.Api
    with scala.meta.io.Aliases
    with scala.meta.prettyprinters.Api
    with scala.meta.prettyprinters.Aliases
    with scala.macros.Api
    with scala.macros.Aliases
    with scala.macros.Universe {

  private[scala] val universe = new ThreadLocal[scala.meta.Universe]
  private[scala] def abstracts = universe.get.abstracts.asInstanceOf[Abstracts]
}
