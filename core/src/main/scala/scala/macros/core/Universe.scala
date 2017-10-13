package scala.macros.core

import scala.macros.internal.trees.Gensym

trait Universe extends Gensym {
  // =========
  // Trees
  // =========
  type Tree
  type Stat
  type Type
  type Term
  type Name
  type TermRef // NOTE(olafur) subject for removal
  type TermName // NOTE(olafur) subject for removal
  type TermParam
  type Lit
  type Mod
  type Self
  type Init
  type Template
  type TypeName
  type TypeBounds
  type TypeParam
  type Pat
  type PatVar // NOTE(olafur) subject for removal
  type Defn

  def treeSyntax(tree: Tree): String
  def treeStructure(tree: Tree): String
  def Name(value: String): Name
  def nameValue(name: Name): String
  def TermName(value: String): TermName
  def TermNameSymbol(symbol: Symbol)(implicit m: Mirror): TermName
  def TermNameUnapply(arg: Any): Option[String]
  def TermSelect(qual: Term, name: TermName): Term
  def TermSelectUnapply(arg: Any): Option[(TermRef, TermName)]
  def TermApply(fun: Term, args: List[Term]): Term
  def TermApplyUnapply(arg: Any): Option[(Term, List[Term])]
  def TermApplyType(fun: Term, args: List[Type]): Term
  def TermBlock(stats: List[Stat]): Term
  def LitString(value: String): Lit
  def Self(name: Name, decltpe: Option[Type]): Self
  def Init(tpe: Type, name: Name, argss: List[List[Term]]): Init
  def Template(inits: List[Init], self: Self, stats: List[Stat]): Template
  def TermNew(init: Init): Term
  def TermParam(
      mods: List[Mod],
      name: Name,
      decltpe: Option[Type],
      default: Option[Term]
  ): TermParam
  def TypeName(value: String): TypeName
  def TypeNameSymbol(sym: Symbol)(implicit m: Mirror): TypeName
  def TypeSelect(qual: TermRef, name: TypeName): Type
  def TypeApply(tpe: Term, args: List[Type]): Type
  def TypeParam(
      mods: List[Mod],
      name: Name,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[Type],
      cbounds: List[Type]
  ): TypeParam
  def DefnObject(mods: List[Mod], name: TermName, templ: Template): Defn
  def DefnVal(mods: List[Mod], pats: List[Pat], decltpe: Option[Type], rhs: Term): Defn
  def DefnDef(
      mods: List[Mod],
      name: TermName,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[Type],
      body: Term
  ): Defn
  def PatVar(name: TermName): PatVar

  // =========
  // Semantic
  // =========
  type Mirror
  type Symbol
  type Denotation

  def symName(sym: Symbol)(implicit m: Mirror): Name
  def denotInfo(denot: Denotation)(implicit m: Mirror): Type
  def denotName(denot: Denotation)(implicit m: Mirror): Name
  def denotSym(denot: Denotation)(implicit m: Mirror): Symbol
  def caseFields(tpe: Type)(implicit m: Mirror): List[Denotation]

  // =========
  // Expansion
  // =========
  type Expansion

}
