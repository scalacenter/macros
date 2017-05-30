package scala.macros.internal
package plugins.scalac
package quasiquotes

import scala.reflect.macros.whitebox.Context

object Macros {
  def apply(c: Context)(args: c.Tree*): c.Tree = {
    ???
  }

  def unapply(c: Context)(tree: c.Tree): c.Tree = {
    ???
  }
}
