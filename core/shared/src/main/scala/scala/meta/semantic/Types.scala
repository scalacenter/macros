package scala.meta
package semantic

private[scala] trait Types { self: Universe =>
  // TODO: Should types be merged with trees or separated from trees?
  // This design serves as a hopefully convincing argument for merging.

  implicit class XtensionTypesType(tpe: Type) extends MemberBasedOps[Denotation] {
    def =:=(other: Type): Boolean = abstracts.typeEqual(tpe, other)
    def <:<(other: Type): Boolean = abstracts.typeSubtype(tpe, other)
    def widen: Type = abstracts.typeWiden(tpe)
    def narrow: Type = abstracts.typeNarrow(tpe)
    protected def members(f: SymbolFilter) = abstracts.typeMembers(tpe, f)
    protected def members(name: String, f: SymbolFilter) = abstracts.typeMembers(tpe, name, f)
  }

  def lub(tpes: Type*): Type = lub(tpes.toList)
  def lub(tpes: List[Type]): Type = abstracts.typeLub(tpes)
  def glb(tpes: Type*): Type = glb(tpes.toList)
  def glb(tpes: List[Type]): Type = abstracts.typeGlb(tpes)
}
