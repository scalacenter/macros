package scala.macros.tests.scaladays

import scala.macros._

object TestMacros {
  def syntax[T](a: T): String = macro {
    print("")
    Lit.String(a.syntax)
  }
  def structure[T](a: T): String = macro {
    print("")
    Lit.String(a.structure)
  }
}
