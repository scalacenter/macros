package scala.macros.core

import scala.macros.internal.trees.Gensym

trait Universe extends Gensym {
  // =========
  // Expansion
  // =========
  type Expansion

  // =========
  // Trees
  // =========
  type Tree
  def treeSyntax(tree: Tree): String
  def treeStructure(tree: Tree): String
  type Stat
  type Type
  type Term
  type Name
  def nameValue(name: Name): String
  def nameApply(value: String): Name
  type TermRef
  type TermName
  def termNameApply(value: String): TermName
  def termNameApplySymbol(symbol: Symbol)(implicit m: Mirror): TermName
  def termNameUnapply(arg: Any): Option[String]
  type TermSelect
  def termSelectApply(qual: Term, name: TermName): TermSelect
  def termSelectUnapply(arg: Any): Option[(TermRef, TermName)]
  type TermApply
  def termApplyApply(fun: Term, args: List[Term]): TermApply
  def termApplyUnapply(arg: Any): Option[(Term, List[Term])]
  def termApplyTypeApply(fun: Term, args: List[Type]): Term
  def termBlockApply(stats: List[Stat]): Term
  type Lit
  def litStringApply(value: String): Lit
  type Mod
  type Self
  def selfApply(name: Name, decltpe: Option[Type]): Self
  type Init
  def initApply(tpe: Type, name: Name, argss: List[List[Term]]): Init
  type Template
  def templateApply(
      early: List[Stat],
      inits: List[Init],
      self: Self,
      stats: List[Stat]
  ): Template
  def termNewApply(init: Init): Term
  type TermParam
  def termParamApply(
      mods: List[Mod],
      name: Name,
      decltpe: Option[Type],
      default: Option[Term]
  ): TermParam
  type TypeName
  def typeNameApply(value: String): TypeName
  def typeNameApplySymbol(sym: Symbol)(implicit m: Mirror): TypeName
  type TypeSelect
  def typeSelectApply(qual: TermRef, name: TypeName): TypeSelect
  def typeApplyApply(tpe: Term, args: List[Type]): Type
  type TypeBounds
  type TypeParam
  def typeParamApply(
      mods: List[Mod],
      name: Name,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[Type],
      cbounds: List[Type]
  ): TypeParam
  type DefnVal
  def defnValApply(
      mods: List[Mod],
      pats: List[Pat],
      decltpe: Option[Type],
      rhs: Term
  ): DefnVal
  type DefnDef
  def defnDefApply(
      mods: List[Mod],
      name: TermName,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[Type],
      body: Term
  ): DefnDef
  type DefnObject
  def defnObjectApply(
      mods: List[Mod],
      name: TermName,
      templ: Template
  ): DefnObject

  type Pat
  type PatVar
  def patVarApply(name: TermName): PatVar

  // =========
  // Semantic
  // =========
  type Mirror
  type Symbol
  def symName(sym: Symbol)(implicit m: Mirror): Name

  type Denotation
  def denotInfo(denot: Denotation)(implicit m: Mirror): Type
  def denotName(denot: Denotation)(implicit m: Mirror): Name
  def denotSym(denot: Denotation)(implicit m: Mirror): Symbol

  def caseFields(tpe: Type)(implicit m: Mirror): List[Denotation]

}
