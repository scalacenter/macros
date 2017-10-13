// scalafmt: { maxColumn = 120 }
package scala.macros.internal.engines.dotc

import scala.language.implicitConversions

import scala.macros.internal.unsupported
import dotty.tools.dotc.core.Decorators.PreNamedString
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.ast.untpd
import dotty.tools.dotc.core.Constants.Constant
import dotty.tools.dotc.core.Denotations
import dotty.tools.dotc.core.Symbols
import dotty.tools.dotc.core.Types
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Flags
import dotty.tools.dotc.core.StdNames._

case class DottyUniverse(prefix: untpd.Tree) extends macros.core.Universe {

  type Tree = untpd.Tree
  type Stat = untpd.Tree
  type Type = untpd.Tree
  type Term = untpd.Tree
  type Name = untpd.Tree
  type TermRef = untpd.Tree
  type TermName = untpd.Tree
  type Lit = untpd.Tree
  type Mod
  type Self = untpd.ValDef
  type Init = untpd.Tree
  type Template = untpd.Template
  type DefnDef = untpd.Tree
  type DefnVal = untpd.Tree
  type DefnObject = untpd.Tree
  type TermParam = untpd.ValDef

  type TypeName = untpd.Tree
  type TypeBounds = untpd.Tree
  type TypeParam = untpd.TypeDef

  type Pat
  type PatVar = untpd.Tree

  // =========
  // Trees
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

  def treeSyntax(tree: Tree): String = unsupported
  def treeStructure(tree: Tree): String = unsupported

  def nameValue(name: Name): String = name.asInstanceOf[untpd.Ident].name.toString
  def Name(value: String): Name = untpd.Ident(value.toTermName).autoPos

  def TermName(value: String): TermName = untpd.Ident(value.toTermName).autoPos
  def TermNameSymbol(symbol: Symbol)(implicit m: Mirror): TermName = tpd.ref(symbol).asInstanceOf[TermName].autoPos
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
  def TermSelectUnapply(arg: Any): Option[(TermRef, TermName)] = arg match {
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

  def TermBlock(stats: List[Stat]): Term = stats match {
    case Nil => untpd.Block(stats, untpd.EmptyTree)
    case _ => untpd.Block(stats.init, stats.last)
  }

  def LitString(value: String): Lit = untpd.Literal(Constant(value)).autoPos

  def Self(name: Name, decltpe: Option[Type]): Self =
    untpd
      .ValDef(name.asInstanceOf[untpd.Ident].name.asTermName, decltpe.getOrElse(untpd.TypeTree()), untpd.EmptyTree)
      .autoPos

  def Init(tpe: Type, name: Name, argss: List[List[Term]]): Init =
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

  def TypeNameSymbol(sym: Symbol)(implicit m: Mirror): TypeName =
    tpd.ref(sym).asInstanceOf[TypeName].autoPos

  def TypeSelect(qual: TermRef, name: TypeName): Type =
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
      pats: List[Pat],
      decltpe: Option[Type],
      rhs: Term
  ): DefnVal = {
    val name = pats match {
      case untpd.Ident(name) :: Nil =>
        name.asTermName
      case els => sys.error(els.toString())
    }

    untpd
      .ValDef(
        name,
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
  ): DefnDef = {
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
      templ: Template
  ): DefnObject =
    untpd
      .ModuleDef(
        name.asInstanceOf[untpd.Ident].name.asTermName,
        templ
      )
      .autoPos

  def PatVar(name: TermName): PatVar = name

  // =========
  // Semantic
  // =========
  type Mirror = Context
  type Symbol = Symbols.Symbol
  def symName(sym: Symbol)(implicit m: Mirror): Name = untpd.Ident(sym.name)

  type Denotation = Denotations.Denotation
  def denotInfo(denot: Denotation)(implicit m: Mirror): Type = untpd.TypeTree(denot.info)
  def denotName(denot: Denotation)(implicit m: Mirror): Name = untpd.Ident(denot.symbol.name)
  def denotSym(denot: Denotation)(implicit m: Mirror): Symbol = denot.symbol

  def caseFields(tpe: Type)(implicit m: Mirror): List[Denotation] = {
    val tp = tpe.asInstanceOf[tpd.Tree].tpe
    tp.memberDenots(
        Types.fieldFilter,
        (name, buf) => {
          buf ++= tp.member(name).altsWith(_ is Flags.ParamAccessor)
        }
      )
      .toList
  }
}
