package scala.macros

import scala.meta.inputs._

private[scala] trait Expansions { self: Universe =>

  trait Expansion {
    private[scala] def expandee: Tree
    private[scala] def abort(pos: Position, msg: String): Nothing
    private[scala] def error(pos: Position, msg: String): Unit
    private[scala] def warning(pos: Position, msg: String): Unit
  }
}
