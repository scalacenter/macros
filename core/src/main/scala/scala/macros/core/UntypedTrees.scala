package scala.macros.core

trait UntypedTrees { this: Universe =>
  // =========
  // Trees
  // =========
  type Tree
  type Stat
  type Term
  type TermParam
  type Lit
  type Mod
  type Self
  type Init
  type Template
  type Pat
  type Defn

  type TypeTree
  type TypeBounds
  type TypeParam

  type Splice    // Splice is special, worth a type

  def treePosition(tree: Tree): Position
  def treeSyntax(tree: Tree): String
  def treeStructure(tree: Tree): String

  def Splice(term: tpd.Tree): Splice

  def TermName(value: String): Term
  def TermSelect(qual: Term, name: String): Term
  def TermApply(fun: Term, args: List[Term]): Term
  def TermApplyType(fun: Term, args: List[TypeTree]): Term
  def TermBlock(stats: List[Stat]): Term
  def LitString(value: String): Lit
  def LitInt(value: Int): Lit
  def Self(name: String, decltpe: Option[TypeTree]): Self
  def Init(tpe: TypeTree, argss: List[List[Term]]): Init
  def Template(inits: List[Init], self: Self, stats: List[Stat]): Template
  def TermNew(init: Init): Term
  def TermParam(
      mods: List[Mod],
      name: String,
      decltpe: Option[TypeTree],
      default: Option[Term]
  ): TermParam

  def TypeName(value: String): TypeTree

  def TypeParam(
      mods: List[Mod],
      name: String,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[TypeTree],
      cbounds: List[TypeTree]
  ): TypeParam
  def DefnObject(mods: List[Mod], name: String, templ: Template): Defn
  def DefnVal(mods: List[Mod], name: String, decltpe: Option[TypeTree], rhs: Term): Defn
  def DefnDef(
      mods: List[Mod],
      name: String,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[TypeTree],
      body: Term
  ): Defn

  def typeTreeOf(tp: Type): TypeTree
}
