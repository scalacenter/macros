package scala.macros.internal
package engines.dotc

import scala.reflect.macros.contexts.Context
import scala.macros.internal.engines.dotc.inputs._
import scala.macros.inputs._

trait Expansions extends scala.macros.Expansions with Positions { self: Universe =>
  case class Expansion(c: Context)

  trait ExpansionAbstracts extends super.ExpansionAbstracts {
    def expandee(implicit e: Expansion): Term = e.c.macroApplication.asInstanceOf[Term]
    def abort(pos: Position, msg: String)(implicit e: Expansion): Nothing = e.c.abort(pos, msg)
    def error(pos: Position, msg: String)(implicit e: Expansion): Unit = e.c.error(pos, msg)
    def warning(pos: Position, msg: String)(implicit e: Expansion): Unit = e.c.warning(pos, msg)
  }
}
