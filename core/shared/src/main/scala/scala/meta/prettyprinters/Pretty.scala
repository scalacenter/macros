package scala.meta
package prettyprinters

trait Pretty extends Product {
  protected def render(p: Prettyprinter): Unit
  final override def toString = this.syntax
}

object Pretty {
  implicit def prettySyntax[T <: Pretty]: Syntax[T] = Syntax{ (p, x) =>
    x.render(p)
  }

  // NOTE: Pretty structure is provided via Structure.structureProduct.
  // Pretty extends Product, and that allows it to use the standard infrastructure.
}