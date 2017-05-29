package scala.meta
package semantic

private[scala] trait Semantic
    extends Denotations
    with Flags
    with Mirrors
    with Symbols
    with Operations
    with Types { self: Universe =>

  implicit class XtensionSemanticRef(ref: Ref)(implicit protected val m: Mirror)
      extends SymbolBasedOps {
    def sym: Symbol = ref.denot.sym
    override def denot: Denotation = abstracts.refDenot(ref)
  }

  implicit class XtensionSemanticMember(member: Member)(implicit protected val m: Mirror)
      extends SymbolBasedOps {
    override def name: Name = abstracts.memberName(member)
    def sym: Symbol = name.sym
    override def denot: Denotation = name.denot
  }

  implicit class XtensionSemanticMemberTerm(member: Member.Term)(implicit m: Mirror)
      extends XtensionSemanticMember(member) {
    override def name: Term.Name = super.name.asInstanceOf[Term.Name]
  }

  implicit class XtensionSemanticMemberType(member: Member.Term)(implicit m: Mirror)
      extends XtensionSemanticMember(member) {
    override def name: Type.Name = super.name.asInstanceOf[Type.Name]
  }

  implicit class XtensionSemanticTerm(term: Term)(implicit m: Mirror) {
    def tpe: Type = abstracts.termTpe(term)
  }
}
