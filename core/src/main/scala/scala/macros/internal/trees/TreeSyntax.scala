package scala.macros.internal
package trees

import scala.macros.internal.prettyprinters._
import scala.macros.Universe

trait TreeSyntax { self: Universe =>

  private[macros] implicit def treeSyntax[T <: Tree]: Syntax[T] = new Syntax[T] {
    override def render(p: Prettyprinter, x: T): Unit = x match {
      case Lit.Char(ch) => p.raw("'"); p.raw(ch.toString); p.raw("'")
      case Lit.Double(d) => p.raw(d.toString); p.raw("d")
      case Lit.Float(f) => p.raw(f.toString); p.raw("f")
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
