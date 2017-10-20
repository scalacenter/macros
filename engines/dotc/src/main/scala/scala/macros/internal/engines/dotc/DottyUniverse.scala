// scalafmt: { maxColumn = 120 }
package scala.macros.internal.engines.dotc

import scala.language.implicitConversions

import java.nio.file.Path
import scala.macros.internal.unsupported
import dotty.tools.dotc.{macros => _, _}
import core._
import util._
import ast.{tpd, untpd}
import Decorators.PreNamedString
import Constants.Constant
import Contexts.Context
import StdNames._
import Symbols.NoSymbol

case class DottyUniverse(prefix: untpd.Tree)(implicit ctx: Context) extends macros.core.Universe {

  type Tree = untpd.Tree
  type Type = untpd.Tree
  type Term = untpd.Tree
  type Name = untpd.Tree
  type TermName = untpd.Tree
  type Lit = untpd.Tree
  type Mod
  type Self = untpd.ValDef
  type Init = untpd.Tree
  type Template = untpd.Template
  type Defn = untpd.Tree
  type TermParam = untpd.ValDef

  type TypeName = untpd.Tree
  type TypeBounds = untpd.Tree
  type TypeParam = untpd.TypeDef

  type Pat

  // =========
  // Utilities
  // =========

  implicit class XtensionTreeWithPosition(tree: Tree) {
    def autoPos[T <: Tree] = tree.withPos(prefix.pos).asInstanceOf[T]
  }

  object ApplySeq {
    def unapply(call: Tree): Option[(Tree, List[List[Tree]])] = {
      def recur(
          acc: List[List[Tree]],
          term: untpd.Tree
      ): (untpd.Tree, List[List[Tree]]) =
        term match {
          case untpd.Apply(fun, args) =>
            recur(args +: acc, fun) // inner-most is in the front
          case fun => (fun, acc)
        }

      Some(recur(Nil, call))
    }
  }

  def fresh(prefix: String): String = NameKinds.UniqueName.fresh(prefix.toTermName).toString

  def treePosition(tree: Tree): Position = Position(tree.pos)
  def treeSyntax(tree: Tree): String = tree.show
  def treeStructure(tree: Tree): String = unsupported

  // =========
  // Trees
  // =========

  def nameValue(name: Name): String = name.asInstanceOf[untpd.Ident].name.toString
  def Name(value: String): Name = untpd.Ident(value.toTermName).autoPos

  def TermName(value: String): TermName = untpd.Ident(value.toTermName).autoPos
  def TermNameSymbol(symbol: Symbol): TermName = tpd.ref(symbol).asInstanceOf[TermName].autoPos
  def TermNameUnapply(arg: Any): Option[String] = arg match {
    case untpd.Ident(name) => Some(name.toString)
    case _ => None
  }

  def TermSelect(qual: Term, name: TermName): Term = name match {
    case untpd.Select(_, name) =>
      // NOTE(olafur) TermName can sometimes be a Term.Select when
      // tpd.ref(Symbol) is used to construct a TermName
      untpd.Select(qual, name)
    case ident: untpd.Ident =>
      untpd.Select(qual, ident.name).autoPos
  }
  def TermSelectUnapply(arg: Any): Option[(Term, TermName)] = arg match {
    case untpd.Select(t, name) if name.isTermName =>
      Some((t, untpd.Ident(name)))
    case _ => None
  }

  def TermApply(fun: Term, args: List[Term]): Term = untpd.Apply(fun, args).autoPos
  def TermApplyUnapply(arg: Any): Option[(Term, List[Term])] = arg match {
    case untpd.Apply(fun, args) => Some((fun, args))
    case _ => None
  }

  def TermApplyType(fun: Term, targs: List[Type]): Term =
    untpd.TypeApply(fun, targs).autoPos

  def TermBlock(stats: List[Tree]): Term = stats match {
    case Nil => untpd.Block(stats, untpd.EmptyTree)
    case _ => untpd.Block(stats.init, stats.last)
  }

  def LitString(value: String): Lit = untpd.Literal(Constant(value)).autoPos
  def LitInt(value: Int): Lit = untpd.Literal(Constant(value)).autoPos

  def Self(name: Name, decltpe: Option[Type]): Self =
    untpd
      .ValDef(name.asInstanceOf[untpd.Ident].name.asTermName, decltpe.getOrElse(untpd.TypeTree()), untpd.EmptyTree)
      .autoPos

  def Init(tpe: Type, name: Name, argss: List[List[Term]]): Init =
    argss.foldLeft(tpe)(untpd.Apply)

  def Template(
      inits: List[Init],
      self: Self,
      stats: List[Tree]
  ): Template = {
    val constr = untpd.DefDef(nme.CONSTRUCTOR, Nil, Nil, untpd.TypeTree(), untpd.EmptyTree)
    untpd.Template(constr, inits, untpd.EmptyValDef, stats)
  }

  def TermNew(init: Init): Term = init match {
    case ApplySeq(fun, argss) =>
      argss.foldLeft(untpd.Select(untpd.New(fun), nme.CONSTRUCTOR): untpd.Tree)(untpd.Apply)
  }

  def TermParam(
      mods: List[Mod],
      name: Name,
      decltpe: Option[Type],
      default: Option[Term]
  ): TermParam =
    untpd
      .ValDef(
        name.asInstanceOf[untpd.Ident].name.asTermName,
        decltpe.getOrElse(untpd.TypeTree()),
        default.getOrElse(untpd.EmptyTree)
      )
      .withFlags(
        Flags.TermParam
      )
      .autoPos

  def TypeName(value: String): TypeName =
    untpd.Ident(value.toTypeName).autoPos

  def TypeNameSymbol(sym: Symbol): TypeName =
    tpd.ref(sym).asInstanceOf[TypeName].autoPos

  def TypeSelect(qual: Term, name: TypeName): Type =
    untpd.Select(qual, name.asInstanceOf[untpd.Ident].name.asTypeName)

  def TypeApply(tpe: Term, args: List[Type]): Type =
    untpd.AppliedTypeTree(tpe, args).autoPos

  def TypeParam(
      mods: List[Mod],
      name: Name,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[Type],
      cbounds: List[Type]
  ): TypeParam = ???

  def DefnVal(
      mods: List[Mod],
      name: TermName,
      decltpe: Option[Type],
      rhs: Term
  ): Defn = {
    untpd
      .ValDef(
        name.asInstanceOf[untpd.Ident].name.asTermName,
        decltpe.getOrElse(untpd.TypeTree()),
        rhs
      )
      .autoPos
  }

  def DefnDef(
      mods: List[Mod],
      name: TermName,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[Type],
      body: Term
  ): Defn = {
    untpd.DefDef(
      name.asInstanceOf[untpd.Ident].name.asTermName,
      tparams,
      paramss,
      decltpe.getOrElse(untpd.TypeTree()),
      body
    )
  }

  def DefnObject(
      mods: List[Mod],
      name: TermName,
      init: List[Init],
      self: Self,
      stats: List[Tree]
  ): Defn = {
    val templ = Template(init, self, stats)
    untpd
      .ModuleDef(
        name.asInstanceOf[untpd.Ident].name.asTermName,
        templ
      )
      .autoPos
  }

  // =========
  // Semantic
  // =========
  type Mirror = Context
  type Symbol = Symbols.Symbol
  def symName(sym: Symbol): Name = untpd.Ident(sym.name)
  def symOwner(sym: Symbol): Option[Symbol] = {
    val owner = sym.maybeOwner
    if (owner == NoSymbol) None
    else Some(owner)
  }

  type Denotation = Denotations.Denotation
  def denotInfo(denot: Denotation): Type = untpd.TypeTree(denot.info)
  def denotName(denot: Denotation): Name = untpd.Ident(denot.symbol.name)
  def denotSym(denot: Denotation): Symbol = denot.symbol

  def caseFields(tpe: Type): List[Denotation] = {
    val tp = tpe.asInstanceOf[tpd.Tree].tpe
    tp.memberDenots(
        Types.fieldFilter,
        (name, buf) => {
          buf ++= tp.member(name).altsWith(_ is Flags.ParamAccessor)
        }
      )
      .toList
  }

  // =========
  // Expansion
  // =========
  case class Expansion(c: Context)
  case class Input(underlying: SourceFile) extends macros.core.Input {
    override def path: Path = underlying.file.file.toPath
  }
  case class Position(underlying: Positions.Position) extends macros.core.Position {
    override def line: Int = {
      // first line is 0 in dotty but 1 in scalac.
      ctx.source.offsetToLine(underlying.start) + 1
    }
    override def input: Input = Input(ctx.source)
  }
  override def enclosingPosition: Position = Position(prefix.pos)
  override def enclosingOwner: Symbol = ctx.owner
}
