package scala.meta
package semantic

import scala.meta.internal.prettyprinters._

private[scala] trait Symbols { self: Semantic =>
  type Symbol >: Null <: AnyRef

  implicit class XtensionSymbolsSymbol(protected val symbol: Symbol)
      extends SymbolBasedOps
      with MemberBasedOps[Symbol]
      with Prettyprinted {
    protected def syntax(p: Prettyprinter): Unit = abstracts.symbolSyntax(p, symbol)
    protected def structure(p: Prettyprinter): Unit = abstracts.symbolStructure(p, symbol)
    protected def members(f: SymbolFilter) = abstracts.symbolMembers(symbol, f)
    protected def members(name: String, f: SymbolFilter) = abstracts.symbolMembers(symbol, name, f)
  }
}
