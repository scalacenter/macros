package scala

package object macros
    extends scala.macros.config.Api
    with scala.macros.config.Aliases
    with scala.macros.inputs.Api
    with scala.macros.inputs.Aliases
    with scala.macros.prettyprinters.Api
    with scala.macros.prettyprinters.Aliases
    with scala.macros.trees.Api
    with scala.macros.trees.Aliases
    with scala.macros.semantic.Api
    with scala.macros.semantic.Aliases
    with scala.macros.Universe {

  private[macros] val universe = new ThreadLocal[scala.macros.Universe]
  private[macros] def abstracts = {
    if (universe.get == null) sys.error("this API can only be called in a macro expansion")
    universe.get.abstracts.asInstanceOf[Abstracts]
  }
}
