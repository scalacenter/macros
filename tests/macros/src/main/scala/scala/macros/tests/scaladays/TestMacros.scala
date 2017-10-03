package scala.macros.tests.scaladays

import scala.macros._
import scala.macros.lib.Lib._

object TestMacros {
  def syntax[T](a: T): String = macro Lit.String(a.syntax)
  def structure[T](a: T): String = macro Lit.String(a.structure)
}
