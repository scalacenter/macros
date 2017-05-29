package scala.meta
package semantic

private[scala] trait Semantic
    extends Abstracts
    with Denotations
    with Flags
    with Symbols
    with Operations
    with Types { self: Universe =>

  implicit class XtensionSemanticRef(ref: Ref) extends SymbolBasedOps {
    def symbol: Symbol = ref.denot.symbol
    override def denot: Denotation = abstracts.refDenot(ref)
  }

  implicit class XtensionSemanticMem(member: Member) extends SymbolBasedOps {
    override def name: Name = abstracts.memberName(member)
    def symbol: Symbol = name.symbol
    override def denot: Denotation = name.denot
  }

  implicit class XtensionSemanticMemTerm(member: Member.Term) extends XtensionSemanticMem(member) {
    override def name: Term.Name = super.name.asInstanceOf[Term.Name]
  }

  implicit class XtensionSemanticMemType(member: Member.Term) extends XtensionSemanticMem(member) {
    override def name: Type.Name = super.name.asInstanceOf[Type.Name]
  }

  implicit class XtensionSemanticTerm(term: Term) {
    def tpe: Type = abstracts.termTpe(term)
  }
}
