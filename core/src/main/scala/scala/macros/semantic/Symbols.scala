package scala.macros
package semantic

import scala.macros.internal.prettyprinters._

private[macros] trait Symbols { self: Universe =>
  type Symbol >: Null <: AnyRef
  object Symbol {
    def apply(id: String)(implicit m: Mirror): Symbol = abstracts.sym(id)
  }

  implicit def symSyntaxInstance[T <: Symbol](implicit m: Mirror): Syntax[T] = {
    Syntax((p, x) => abstracts.symSyntax(p, x))
  }
  implicit def symStructureInstance[T <: Symbol](implicit m: Mirror): Structure[T] = {
    Structure((p, x) => abstracts.symStructure(p, x))
  }

  implicit class XtensionSymbolsSymbol(protected val sym: Symbol)(implicit m0: Mirror)
      extends SymbolBasedOps
      with MemberBasedOps[Symbol]
      with Prettyprinted {
    protected def m: Mirror = m0
    protected def syntax(p: Prettyprinter): Unit = abstracts.symSyntax(p, sym)
    protected def structure(p: Prettyprinter): Unit = abstracts.symStructure(p, sym)
    protected def members(f: SymFilter) = abstracts.symMembers(sym, f)
    protected def members(name: String, f: SymFilter) = abstracts.symMembers(sym, name, f)
  }
}
