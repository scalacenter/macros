package scala.macros
package internal

import scala.macros.inputs.Position

final case class AbortMacroException(pos: Position, msg: String) extends Exception(msg)
