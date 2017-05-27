package scala.macros

import scala.macros.inputs._

private[scala] trait Expansions { self: Universe =>

  type Expansion >: Null <: AnyRef
  implicit class XtensionExpansionsExpansion(e: Expansion) {
    private[scala] def expandee: Term = abstracts.expandee(e)
    private[scala] def abort(pos: Position, msg: String): Nothing = abstracts.abort(e, pos, msg)
    private[scala] def error(pos: Position, msg: String): Unit = abstracts.error(e, pos, msg)
    private[scala] def warning(pos: Position, msg: String): Unit = abstracts.warning(e, pos, msg)
  }

  private[scala] def abstracts: ExpansionAbstracts
  private[scala] trait ExpansionAbstracts {
    def expandee(e: Expansion): Term
    def abort(e: Expansion, pos: Position, msg: String): Nothing
    def error(e: Expansion, pos: Position, msg: String): Unit
    def warning(e: Expansion, pos: Position, msg: String): Unit
  }
}
