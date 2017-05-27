package scala.macros

private[scala] trait Api {
  // TODO: No idea why an explicit call to XtensionExpansionsExpansion is necessary.
  private[scala] def e1(implicit e: Expansion) = XtensionExpansionsExpansion(e)
  def expandee(implicit e: Expansion): Tree = e1.expandee
  def abort(pos: Position, msg: String)(implicit e: Expansion): Nothing = e1.abort(pos, msg)
  def error(pos: Position, msg: String)(implicit e: Expansion): Unit = e1.error(pos, msg)
  def warning(pos: Position, msg: String)(implicit e: Expansion): Unit = e1.warning(pos, msg)
}

private[scala] trait Aliases {
  type Dialect = scala.meta.Dialect
  val Dialect = scala.meta.Dialect
}
