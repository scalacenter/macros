package scala.macros.internal
package engines.scalac

import scala.reflect.macros.whitebox.Context
import scala.{macros => m}

case class Expansion(val c: Context) extends m.Expansion {
  private[scala] def expandee: m.Tree = ???
  private[scala] def abort(pos: m.Position, msg: String): Nothing = ???
  private[scala] def error(pos: m.Position, msg: String): Unit = ???
  private[scala] def warning(pos: m.Position, msg: String): Unit = ???
}