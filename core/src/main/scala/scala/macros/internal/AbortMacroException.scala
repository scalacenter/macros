package scala.macros
package internal

import scala.macros.core.Position

final case class AbortMacroException(pos: Position, msg: String) extends Exception(msg)
