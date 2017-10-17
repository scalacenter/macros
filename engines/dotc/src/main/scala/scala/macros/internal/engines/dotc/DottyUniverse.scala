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

  type Stat = untpd.Tree
  type Term = untpd.Tree
  type Lit = untpd.Literal

  type Defn = untpd.Tree
  type TermParam = untpd.ValDef
  type Mod
  type Self = untpd.ValDef
  type Init = untpd.Tree
  type Template = untpd.Template

  type TypeTree = untpd.Tree
  type TypeBounds = untpd.TypeBoundsTree
  type TypeParam = untpd.TypeDef

  type Pat

  type Splice = untpd.TypedSplice

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

  def Splice(term: typed.Tree): Splice = untpd.TypedSplice(term)

  def TermName(value: String): Term = untpd.Ident(value.toTermName).autoPos

  def TermSelect(qual: Term, name: String): Term =
    untpd.Select(qual, name.toTermName).autoPos

  def TermApply(fun: Term, args: List[Term]): Term = untpd.Apply(fun, args).autoPos

  def TermApplyType(fun: Term, targs: List[TypeTree]): Term =
    untpd.TypeApply(fun, targs).autoPos

  def TermBlock(stats: List[Stat]): Term = stats match {
    case Nil => untpd.Block(stats, untpd.EmptyTree)
    case _ => untpd.Block(stats.init, stats.last)
  }

  def LitString(value: String): Lit = untpd.Literal(Constant(value)).autoPos
  def LitInt(value: Int): Lit = untpd.Literal(Constant(value)).autoPos

  def Self(name: String, decltpe: Option[TypeTree]): Self =
    untpd
      .ValDef(name.asInstanceOf[untpd.Ident].name.asTermName, decltpe.getOrElse(untpd.TypeTree()), untpd.EmptyTree)
      .autoPos

  def Init(tpe: TypeTree, argss: List[List[Term]]): Init =
    argss.foldLeft(tpe)(untpd.Apply)

  def Template(
      inits: List[Init],
      self: Self,
      stats: List[Stat]
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
      name: String,
      decltpe: Option[TypeTree],
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

  def TypeName(name: String): TypeTree =
    untpd.Ident(name.toTypeName).autoPos

  def TypeSelect(qual: Term, name: String): TypeTree =
    untpd.Select(qual, name.toTypeName)

  def TypeApply(tpe: TypeTree, args: List[TypeTree]): TypeTree =
    untpd.AppliedTypeTree(tpe, args).autoPos

  def TypeParam(
      mods: List[Mod],
      name: String,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[TypeTree],
      cbounds: List[TypeTree]
  ): TypeParam = ???

  def DefnVal(
      mods: List[Mod],
      name: String,
      decltpe: Option[TypeTree],
      rhs: Term
  ): Defn =
    untpd
      .ValDef(
        name.toTermName,
        decltpe.getOrElse(untpd.TypeTree()),
        rhs
      )
      .autoPos

  def DefnDef(
      mods: List[Mod],
      name: String,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[TypeTree],
      body: Term
  ): Defn =
    untpd.DefDef(
      name.toTermName,
      tparams,
      paramss,
      decltpe.getOrElse(untpd.TypeTree()),
      body
    )

  def DefnObject(
      mods: List[Mod],
      name: String,
      templ: Template
  ): Defn =
    untpd
      .ModuleDef(
        name.toTermName,
        templ
      )
      .autoPos

  // =========
  // typed trees
  // =========
  object typed extends typedApi {
    type Tree = tpd.Tree
    type Term = tpd.Tree

    def typeOf(tree: Term): Type = tree.tpe
    def ref(sym: Symbol): Term = tpd.ref(sym)

    // only for terms, no extractor for type trees
    def NameUnapply(tree: Tree): Option[Denotation] = tree match {
      case id: tpd.Ident if id.name.isTermName => Some(id.tpe.asInstanceOf[Types.NamedType].denot)
      case _ => None
    }

    def Select(qual: Term, name: String): Term = tpd.Select(qual, name.toTermName)
    def SelectUnapply(tree: Tree): Option[(Term, Symbol)] = tree match {
      case tpd.Select(qual, name) if name.isTermName => Some((qual, tree.tpe.termSymbol))
      case _ => None
    }

    def Apply(fun: Term, args: List[Term]): Term = tpd.Apply(fun, args)
    def ApplyUnapply(tree: Tree): Option[(Term, List[Term])] = tree match {
      case tpd.Apply(fun, args) => Some((fun, args))
      case _ => None
    }
  }

  // Semantic
  // =========
  type Mirror = Context
  type Symbol = Symbols.Symbol
  def root: Symbol = ctx.definitions.RootClass
  def symName(sym: Symbol): String = sym.name.show
  def symOwner(sym: Symbol): Option[Symbol] = {
    val owner = sym.maybeOwner
    if (owner == NoSymbol) None
    else Some(owner)
  }

  type Denotation = Denotations.Denotation
  def denotInfo(denot: Denotation): Type = denot.info
  def denotSym(denot: Denotation): Symbol = denot.symbol

  type Type = Types.Type
  def caseFields(tp: Type): List[Denotation] = {
    tp.memberDenots(
        Types.fieldFilter,
        (name, buf) => {
          buf ++= tp.member(name).altsWith(_ is Flags.ParamAccessor)
        }
      )
      .toList
  }
  def typeRef(path: String): Type = ctx.staticRef(path.toTypeName, false).symbol.typeRef
  def appliedType(tp: Type, args: List[Type]): Type = Types.AppliedType(tp, args)
  def typeTreeOf(tp: Type): TypeTree = tpd.TypeTree(tp)

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
