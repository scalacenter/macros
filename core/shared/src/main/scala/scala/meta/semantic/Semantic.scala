package scala.meta
package semantic

private[scala] trait Semantic
    extends scala.meta.trees.Trees
    with Abstracts
    with Denotations
    with Flags
    with Symbols
    with Operations
    with Types {
  implicit class XtensionSemanticRef(ref: Ref) extends SymbolBasedOps {
    def symbol: Symbol = ref.denot.symbol
    override def denot: Denotation = abstracts.refDenot(ref)
  }

  implicit class XtensionSemanticMember(member: Member) extends SymbolBasedOps {
    def symbol: Symbol = member.name.symbol
    override def denot: Denotation = member.name.denot
  }

  implicit class XtensionSemanticTerm(term: Term) {
    def tpe: Type = abstracts.termTpe(term)
  }
}
