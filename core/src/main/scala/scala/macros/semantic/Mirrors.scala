// NOTE: I think that having every signature fit in one line is more important than dogma.
/* scalafmt: { maxColumn = 120 }*/
package scala.macros
package semantic

import scala.macros.internal.prettyprinters._

private[macros] trait Mirrors { self: Universe =>
  type Mirror >: Null <: AnyRef

  // NOTE: Not moved to Abstracts.scala to be consistent with Expansion.scala in profiles/macros.
  private[macros] def abstracts: MirrorAbstracts
  private[macros] trait MirrorAbstracts {
    def refDenot(ref: Ref)(implicit m: Mirror): Denotation
    def termTpe(term: Term)(implicit m: Mirror): Type
    def sym(id: String)(implicit m: Mirror): Symbol
    def symSyntax(p: Prettyprinter, sym: Symbol)(implicit m: Mirror): Unit
    def symStructure(p: Prettyprinter, sym: Symbol)(implicit m: Mirror): Unit
    def symName(sym: Symbol)(implicit m: Mirror): Name
    def symFlags(sym: Symbol)(implicit m: Mirror): Long
    def symAnnots(sym: Symbol)(implicit m: Mirror): List[Init]
    def symWithin(sym: Symbol)(implicit m: Mirror): Symbol
    def symDenot(sym: Symbol)(implicit m: Mirror): Denotation
    def symDenot(sym: Symbol, pre: Type)(implicit m: Mirror): Denotation
    def symMembers(sym: Symbol, f: SymFilter)(implicit m: Mirror): List[Symbol]
    def symMembers(sym: Symbol, name: String, f: SymFilter)(implicit m: Mirror): List[Symbol]
    def denotSyntax(p: Prettyprinter, denot: Denotation)(implicit m: Mirror): Unit
    def denotStructure(p: Prettyprinter, denot: Denotation)(implicit m: Mirror): Unit
    def denotSym(denot: Denotation)(implicit m: Mirror): Symbol
    def denotInfo(denot: Denotation)(implicit m: Mirror): Type
    def denotMembers(denot: Denotation, f: SymFilter)(implicit m: Mirror): List[Denotation]
    def denotMembers(denot: Denotation, name: String, f: SymFilter)(implicit m: Mirror): List[Denotation]
    // NOTE(olafur) caseFields is fairly ad-hoc, ideally we should
    // find a more principled api to find the primary ctor parameters of
    // a class that is still portable across compilers.
    // T.vals.filter(_.isCase) works in scalac but not in dotc.
    def caseFields(tpe: Type)(implicit m: Mirror): List[Denotation]
    def typeEqual(tpe1: Type, tpe2: Type)(implicit m: Mirror): Boolean
    def typeSubtype(tpe1: Type, tpe2: Type)(implicit m: Mirror): Boolean
    def typeWiden(tpe: Type)(implicit m: Mirror): Type
    def typeNarrow(tpe: Type)(implicit m: Mirror): Type
    def typeMembers(tpe: Type, f: SymFilter)(implicit m: Mirror): List[Denotation]
    def typeMembers(tpe: Type, name: String, f: SymFilter)(implicit m: Mirror): List[Denotation]
    def typeLub(tpes: List[Type])(implicit m: Mirror): Type
    def typeGlb(tpes: List[Type])(implicit m: Mirror): Type
  }
}
