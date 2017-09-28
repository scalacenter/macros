package scala.macros.internal
package trees

import scala.macros.internal.prettyprinters._
import scala.macros.Universe

trait TreeSyntax { self: Universe =>

  private[macros] implicit def treeSyntax[T <: Tree]: Syntax[T] = new Syntax[T] {
    override def render(p: Prettyprinter, x: T): Unit = x match {
      case Lit.Char(ch) => p.raw("'"); p.raw(ch.toString); p.raw("'")
      case Lit.Double(n) =>
        if (java.lang.Double.isNaN(n)) p.raw("Double.NaN")
        else {
          n match {
            case Double.PositiveInfinity => p.raw("Double.PositiveInfinity")
            case Double.NegativeInfinity => p.raw("Double.NegativeInfinity")
            case _ =>
              p.raw(n.toString)
              p.raw("d")
          }
        }
      case Lit.Float(n) =>
        if (java.lang.Float.isNaN(n)) p.raw("Float.NaN")
        else {
          n match {
            case Float.PositiveInfinity => p.raw("Float.PositiveInfinity")
            case Float.NegativeInfinity => p.raw("Float.NegativeInfinity")
            case _ =>
              p.raw(n.toString)
              p.raw("f")
          }
        }
      case Lit.Int(l) => p.raw(l.toString)
      case Lit.Long(l) => p.raw(l.toString); p.raw("L")
      case Lit.Null() => p.raw("null")
      case Lit.String(str) => p.raw("\""); p.raw(str); p.raw("\"")
      case Lit.Symbol(sym) => p.raw("'"); p.raw(sym.name)
      case els =>
        println(s"ELS: $els (${els.getClass})")
        ???
    }
  }
}
