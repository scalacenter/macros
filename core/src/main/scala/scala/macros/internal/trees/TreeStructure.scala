package scala.macros.internal
package trees

import scala.macros.internal.prettyprinters._
import scala.macros.Universe

trait TreeStructure { self: Universe =>

  private[macros] implicit def treeStructure[T <: Tree]: Structure[T] = new Structure[T] {
    // TODO: implement this
    override def render(p: Prettyprinter, x: T): Unit = x match {
      case Lit.Char(ch) => p.raw("Lit.Char("); p.raw(x.syntax); p.raw(")")
      case Lit.Double(d) => p.raw("Lit.Double("); p.raw(x.syntax); p.raw(")")
      case Lit.Float(f) => p.raw("Lit.Float("); p.raw(x.syntax); p.raw(")")
      case Lit.Int(l) => p.raw("Lit.Int("); p.raw(x.syntax); p.raw(")")
      case Lit.Long(l) => p.raw("Lit.Long("); p.raw(x.syntax); p.raw(")")
      case Lit.Null() => p.raw("Lit.Null()")
      case Lit.String(str) => p.raw("Lit.String("); p.raw(x.syntax); p.raw(")")
      case Lit.Symbol(sym) => p.raw("Lit.Symbol("); p.raw(x.syntax); p.raw(")")
      case _ =>
        pprint.log(x)
        ???
    }
  }
}
