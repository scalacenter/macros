package scala.macros.core

import java.nio.file.Path

trait Universe {
  // =========
  // Trees
  // =========
  type Tree

  type Term
  type TermName
  type TermParam

  type Defn

  type Type
  type TypeName
  type TypeParam
  type TypeBounds

  type Name

  type Pat
  type Lit
  type Mod
  type Self
  type Init

  def fresh(prefix: String): String

  def treePosition(tree: Tree): Position
  def treeSyntax(tree: Tree): String
  def treeStructure(tree: Tree): String

  def Name(value: String): Name
  def nameValue(name: Name): String
  def TermName(value: String): TermName
  def TermNameSymbol(symbol: Symbol): TermName
  def TermNameUnapply(arg: Any): Option[String]
  def TermSelect(qual: Term, name: TermName): Term
  def TermSelectUnapply(arg: Any): Option[(Term, TermName)]
  def TermApply(fun: Term, args: List[Term]): Term
  def TermApplyUnapply(arg: Any): Option[(Term, List[Term])]
  def TermApplyType(fun: Term, args: List[Type]): Term
  def TermBlock(stats: List[Tree]): Term
  def LitString(value: String): Lit
  def LitInt(value: Int): Lit
  def Self(name: Name, decltpe: Option[Type]): Self
  def Init(tpe: Type, argss: List[List[Term]]): Init
  def TermNew(init: Init): Term
  def TermParam(
      mods: List[Mod],
      name: Name,
      decltpe: Option[Type],
      default: Option[Term]
  ): TermParam
  def TypeName(value: String): TypeName
  def TypeNameSymbol(sym: Symbol): TypeName
  def TypeSelect(qual: Term, name: TypeName): Type
  def TypeApply(tpe: Term, args: List[Type]): Type
  def TypeParam(
      mods: List[Mod],
      name: Name,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[Type],
      cbounds: List[Type]
  ): TypeParam
  def DefnObject(
      mods: List[Mod],
      name: TermName,
      init: List[Init],
      self: Self,
      stats: List[Tree]
  ): Defn
  def DefnVal(mods: List[Mod], name: TermName, decltpe: Option[Type], rhs: Term): Defn
  def DefnDef(
      mods: List[Mod],
      name: TermName,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[Type],
      body: Term
  ): Defn

  // =========
  // Semantic
  // =========
  type Mirror

  type Symbol
  def symName(sym: Symbol): Name
  def symOwner(sym: Symbol): Option[Symbol]

  type Denotation
  def denotInfo(denot: Denotation): Type
  def denotName(denot: Denotation): Name
  def denotSym(denot: Denotation): Symbol
  def caseFields(tpe: Type): List[Denotation]

  // =========
  // Expansion
  // =========
  type Expansion
  def enclosingPosition: Position
  def enclosingOwner: Symbol
}

trait Input extends Any {
  def path: Path
}

trait Position extends Any {
  def line: Int
  def input: Input
}
