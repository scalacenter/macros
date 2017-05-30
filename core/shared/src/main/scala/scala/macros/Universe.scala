package scala.macros

import scala.macros.semantic._
import scala.macros.trees._

private[scala] trait Universe extends Expansions with Quasiquotes with Semantic with Trees {
  private[scala] type Abstracts <: TreeAbstracts with MirrorAbstracts with ExpansionAbstracts
  private[scala] def abstracts: Abstracts

  def expandee(implicit e: Expansion): Term = abstracts.expandee
  def abort(pos: Position, msg: String)(implicit e: Expansion): Nothing = abstracts.abort(pos, msg)
  def error(pos: Position, msg: String)(implicit e: Expansion): Unit = abstracts.error(pos, msg)
  def warning(pos: Position, msg: String)(implicit e: Expansion): Unit = abstracts.warning(pos, msg)
}
