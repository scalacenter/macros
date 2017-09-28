package scala.macros
package internal

final case class AbortMacroException(pos: Position, msg: String) extends Exception(msg)
