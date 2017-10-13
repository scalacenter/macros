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
  def Name(value: String): Name
  def nameValue(name: Name): String
  // NOTE(olafur) if TermSelect.name is String instead of TermName, then we can
  // probably replace TermRef and TermName with Term.
  type TermRef
  type TermName
  def TermName(value: String): TermName
  def TermNameSymbol(symbol: Symbol)(implicit m: Mirror): TermName
  def TermNameUnapply(arg: Any): Option[String]
  def TermSelect(qual: Term, name: TermName): Term
  def TermSelectUnapply(arg: Any): Option[(TermRef, TermName)]
  def TermApply(fun: Term, args: List[Term]): Term
  def TermApplyUnapply(arg: Any): Option[(Term, List[Term])]
  def TermApplyType(fun: Term, args: List[Type]): Term
  def TermBlock(stats: List[Stat]): Term
  type Lit
  def LitString(value: String): Lit
  type Mod
  type Self
  def Self(name: Name, decltpe: Option[Type]): Self
  type Init
  def Init(tpe: Type, name: Name, argss: List[List[Term]]): Init
  type Template
  def Template(
      early: List[Stat],
      inits: List[Init],
      self: Self,
      stats: List[Stat]
  ): Template
  def TermNew(init: Init): Term
  type TermParam
  def TermParam(
      mods: List[Mod],
      name: Name,
      decltpe: Option[Type],
      default: Option[Term]
  ): TermParam
  type TypeName
  def TypeName(value: String): TypeName
  def TypeNameSymbol(sym: Symbol)(implicit m: Mirror): TypeName
  def TypeSelect(qual: TermRef, name: TypeName): Type
  def TypeApply(tpe: Term, args: List[Type]): Type
  type TypeBounds
  type TypeParam
  def TypeParam(
      mods: List[Mod],
      name: Name,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[Type],
      cbounds: List[Type]
  ): TypeParam
  type DefnVal
  def DefnVal(
      mods: List[Mod],
      pats: List[Pat],
      decltpe: Option[Type],
      rhs: Term
  ): DefnVal
  type DefnDef
  def DefnDef(
      mods: List[Mod],
      name: TermName,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[Type],
      body: Term
  ): DefnDef
  type DefnObject
  def DefnObject(
      mods: List[Mod],
      name: TermName,
      templ: Template
  ): DefnObject

  type Pat
  type PatVar
  def PatVar(name: TermName): PatVar

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
