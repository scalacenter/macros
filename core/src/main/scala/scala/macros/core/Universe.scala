package scala.macros.core

import scala.macros.internal.trees.Gensym

import scala.macros.internal.unsupported

trait Universe extends Gensym {
  // =========
  // Expansion
  // =========
  type Expansion

  // =========
  // Trees
  // =========
  type Stat
  type Type
  type Term
  type Name
  def nameValue(name: Name): String = unsupported
  def nameApply(value: String): Name = unsupported
  type TermRef
  type TermName
  def termNameApply(value: String): TermName = unsupported
  def termNameApplySymbol(symbol: Symbol): TermName = unsupported
  def termNameUnapply(arg: Any): Option[String] = unsupported
  type TermSelect
  def termSelectApply(qual: Term, name: TermName): TermSelect = unsupported
  def termSelectUnapply(arg: Any): Option[(TermRef, TermName)] = unsupported
  type TermApply
  def termApplyApply(fun: Term, args: List[Term]): TermApply = unsupported
  def termApplyUnapply(arg: Any): Option[(Term, List[Term])] = unsupported
  type TermApplyType
  def termApplyTypeApply(fun: Term, args: List[Type]): TermApplyType = unsupported
  type TermBlock
  def termBlockApply(stats: List[Stat]): TermApplyType = unsupported
  type LitString
  def litStringApply(value: String): LitString = unsupported
  type Mod
  type Self
  def selfApply(name: Name, decltpe: Option[Type]): Self = unsupported
  type Init
  def initApply(tpe: Type, name: Name, argss: List[List[Term]]): Init = unsupported
  type Template
  def templateApply(
      early: List[Stat],
      inits: List[Init],
      self: Self,
      stats: List[Stat]
  ): Template = unsupported
  type TermNew
  def termNewApply(init: Init): TermNew = unsupported
  type TermParam
  def termParamApply(
      mods: List[Mod],
      name: Name,
      decltpe: Option[Type],
      default: Option[Term]
  ): TermParam = unsupported
  type TypeName
  def typeNameApply(value: String): TypeName = unsupported
  type TypeSelect
  def typeSelectApply(qual: Term, name: TypeName): TypeSelect = unsupported
  type TypeApply
  def typeApplyApply(qual: Term, args: List[Type]): TypeApply = unsupported
  type TypeBounds
  type TypeParam
  def typeParamApply(
      mods: List[Mod],
      name: Name,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[Type],
      cbounds: List[Type]
  ): TypeParam = unsupported
  type DefnVal
  def defnValApply(
      mods: List[Mod],
      pats: List[Pat],
      decltpe: Option[Type],
      rhs: Term
  ): DefnVal = unsupported
  type DefnDef
  def defnDefApply(
      mods: List[Mod],
      name: TermName,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[Type],
      body: Term
  ): DefnDef = unsupported
  type DefnObject
  def defnObjectApply(
      mods: List[Mod],
      name: TermName,
      templ: Template
  ): DefnObject = unsupported

  type Pat
  type PatVar
  def patVarApply(name: TermName): PatVar = unsupported

  // =========
  // Semantic
  // =========
  type Mirror
  type Symbol

  type Denotation
  def denotInfo(denot: Denotation): Type = unsupported
  def denotName(denot: Denotation): Name = unsupported
  def denotSym(denot: Denotation): Symbol = unsupported

  def caseFields(tpe: Type)(implicit m: Mirror): List[Denotation] = unsupported

}
