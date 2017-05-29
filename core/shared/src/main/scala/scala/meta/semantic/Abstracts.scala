package scala.meta
package semantic

import scala.meta.internal.prettyprinters._

private[scala] trait Abstracts extends scala.meta.trees.Abstracts { self: Semantic =>
  // NOTE: Check out Companions.scala to see why this is private.
  private[scala] def abstracts: SemanticAbstracts
  private[scala] trait SemanticAbstracts extends TreeAbstracts {
    def refDenot(ref: Ref): Denotation
    def termTpe(term: Term): Type

    def symbolSyntax(p: Prettyprinter, symbol: Symbol): Unit
    def symbolStructure(p: Prettyprinter, symbol: Symbol): Unit
    def symbolName(symbol: Symbol): Name
    def symbolFlags(symbol: Symbol): Long
    def symbolAnnots(symbol: Symbol): List[Init]
    def symbolWithin(symbol: Symbol): Symbol
    def symbolDenot(symbol: Symbol): Denotation
    def symbolDenot(symbol: Symbol, pre: Type): Denotation
    def symbolMembers(symbol: Symbol, f: Symbol => Boolean): List[Symbol]
    def symbolMembers(symbol: Symbol, name: String, f: Symbol => Boolean): List[Symbol]

    def denotSyntax(p: Prettyprinter, denot: Denotation): Unit
    def denotStructure(p: Prettyprinter, denot: Denotation): Unit
    def denotSymbol(denot: Denotation): Symbol
    def denotInfo(denot: Denotation): Type
    def denotMembers(denot: Denotation, f: Symbol => Boolean): List[Denotation]
    def denotMembers(denot: Denotation, name: String, f: Symbol => Boolean): List[Denotation]

    def typeEqual(tpe1: Type, tpe2: Type): Boolean
    def typeSubtype(tpe1: Type, tpe2: Type): Boolean
    def typeWiden(tpe: Type): Type
    def typeNarrow(tpe: Type): Type
    def typeMembers(tpe: Type, f: Symbol => Boolean): List[Denotation]
    def typeMembers(tpe: Type, name: String, f: Symbol => Boolean): List[Denotation]
    def typeLub(tpes: List[Type]): Type
    def typeGlb(tpes: List[Type]): Type
  }
}
