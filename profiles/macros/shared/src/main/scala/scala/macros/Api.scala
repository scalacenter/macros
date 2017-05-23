package scala.macros

private[scala] trait Api {
  def expandee(implicit expansion: Expansion): Tree = {
    expansion.expandee
  }

  def abort(pos: Position, msg: String)(implicit expansion: Expansion): Nothing = {
    expansion.abort(pos, msg)
  }

  def error(pos: Position, msg: String)(implicit expansion: Expansion): Unit = {
    expansion.error(pos, msg)
  }

  def warning(pos: Position, msg: String)(implicit expansion: Expansion): Unit = {
    expansion.warning(pos, msg)
  }
}

private[scala] trait Aliases {
  type Dialect = scala.meta.Dialect
  val Dialect = scala.meta.Dialect
}
