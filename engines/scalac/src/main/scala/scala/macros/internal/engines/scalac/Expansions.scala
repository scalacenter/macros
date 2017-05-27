package scala.macros.internal
package engines.scalac

import scala.reflect.macros.contexts.Context
import scala.macros.internal.engines.scalac.inputs._
import scala.macros.inputs._

trait Expansions extends scala.macros.Expansions with Positions { self: Universe =>
  case class Expansion(c: Context)

  def abstracts: ExpansionAbstracts
  trait ExpansionAbstracts extends super.ExpansionAbstracts {
    def expandee(e: Expansion): Term = e.c.macroApplication.asInstanceOf[Term]
    def abort(e: Expansion, pos: Position, msg: String): Nothing = e.c.abort(pos, msg)
    def error(e: Expansion, pos: Position, msg: String): Unit = e.c.error(pos, msg)
    def warning(e: Expansion, pos: Position, msg: String): Unit = e.c.warning(pos, msg)
  }
}
