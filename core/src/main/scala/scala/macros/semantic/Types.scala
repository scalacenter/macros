package scala.macros
package semantic

private[macros] trait Types { self: Universe =>
  // TODO: Should types be merged with trees or separated from trees?
  // This design serves as a hopefully convincing argument for merging.

  implicit class XtensionType(tpe: Type)(implicit m0: Mirror) extends MemberBasedOps[Denotation] {
    protected def m: Mirror = m0
    def =:=(other: Type): Boolean = abstracts.typeEqual(tpe, other)
    def <:<(other: Type): Boolean = abstracts.typeSubtype(tpe, other)
    def widen: Type = abstracts.typeWiden(tpe)
    def narrow: Type = abstracts.typeNarrow(tpe)
    def caseFields: List[Denotation] = abstracts.caseFields(tpe)
    protected def members(f: SymFilter) = abstracts.typeMembers(tpe, f)
    protected def members(name: String, f: SymFilter) = abstracts.typeMembers(tpe, name, f)
  }

  def lub(tpes: Type*)(implicit m: Mirror): Type = lub(tpes.toList)
  def lub(tpes: List[Type])(implicit m: Mirror): Type = abstracts.typeLub(tpes)
  def glb(tpes: Type*)(implicit m: Mirror): Type = glb(tpes.toList)
  def glb(tpes: List[Type])(implicit m: Mirror): Type = abstracts.typeGlb(tpes)
}
