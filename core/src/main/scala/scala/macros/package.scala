package scala

import scala.language.higherKinds
import scala.language.implicitConversions

package object macros {

  private[macros] val universeStore = new ThreadLocal[core.Universe]
  private[macros] def universe: core.Universe = {
    val result = universeStore.get
    if (result == null) sys.error("this API can only be called in a macro expansion")
    else result
  }

  private implicit class XtensionBang[A](val a: A) extends AnyVal {
    def unary_![B]: B = a.asInstanceOf[B]
  }

  type Input = core.Input
  type Position = core.Position
  type Symbol <: AnyRef
  implicit class XtensionSymbol(val sym: Symbol) extends AnyVal {
    def name: String = universe.symName(!sym)
    def owner: Option[Symbol] = !universe.symOwner(!sym)
  }

  type Denotation
  implicit class XtensionDenotation(val denot: Denotation) extends AnyVal {
    def info: Type = !universe.denotInfo(!denot)
    def name: String = !universe.symName(!universe.denotSym(!denot))
    def sym: Symbol = !universe.denotSym(!denot)
  }
  type Mirror
  trait Expansion
  implicit class XtensionExpansion(val expansion: Expansion) extends AnyVal {
    def enclosingOwner: Symbol = !universe.enclosingOwner
    def enclosingPosition: Position = !universe.enclosingPosition
  }
  type Tree
  implicit class XtensionTree(val tree: Tree) extends AnyVal {
    def pos: Position = !universe.treePosition(!tree)
    def syntax: String = universe.treeSyntax(!tree)
    def structure: String = universe.treeStructure(!tree)
  }
  type Stat <: Tree

  type Term <: Stat
  implicit class XtensionTerm(val term: Term) extends AnyVal {
    def select(name: String): Term = Term.Select(term, name)
    def select(name: List[String]): Term = name.foldLeft(term) {
      case (qual, name) => Term.Select(qual, name)
    }
    def apply(args: List[Term]): Term = Term.Apply(term, args)
    def applyType(args: List[TypeTree]): Term = Term.ApplyType(term, args)
  }
  object Term {
    def fresh(prefix: String = "fresh"): String = universe.fresh(prefix)
    object Name {
      def apply(value: String): Term = !universe.TermName(value)
    }
    object Select {
      def apply(qual: Term, name: String): Term =
        !universe.TermSelect(!qual, !name)
    }

    object Apply {
      def apply(fun: Term, args: List[Term]): Term =
        !universe.TermApply(!fun, !args)
    }

    object ApplyType {
      def apply(fun: Term, args: List[TypeTree]): Term =
        !universe.TermApplyType(!fun, !args)
    }

    object Block {
      def apply(stats: List[Stat]): Term =
        !universe.TermBlock(!stats)
    }
    object New {
      def apply(init: Init): Term = !universe.TermNew(!init)
    }
    object If {
      def apply(cond: Term, truep: Term, elsep: Term): Term =
        !universe.TermIf(!cond, !truep, !elsep)
    }

    type Param
    object Param {
      def apply(
          mods: List[Mod],
          name: String,
          decltpe: Option[TypeTree],
          default: Option[Term]
      ): Term.Param =
        !universe.TermParam(!mods, !name, !decltpe, !default)
    }
  }
  type Template
  object Template {
    def apply(inits: List[Init], self: Self, stats: List[Stat]): Template =
      !universe.Template(!inits, !self, !stats)
  }
  type Lit <: Term
  object Lit {
    type String <: Lit
    object String {
      def apply(value: Predef.String): Lit.String = !universe.LitString(value)
    }
    type Int <: Lit
    object Int {
      def apply(value: scala.Int): Lit.Int = !universe.LitInt(value)
    }
    type Null <: Lit
    def Null: Lit.Null = !universe.LitNull
  }

  type TypeTree <: Tree
  object TypeTree {
    object Name {
      def apply(value: String): TypeTree = !universe.TypeName(value)
    }
    object Select {
      def apply(qual: Term, name: String): TypeTree = !universe.TypeSelect(!qual, name)
    }
    object Apply {
      def apply(fun: TypeTree, args: List[TypeTree]): TypeTree = !universe.TypeApply(!fun, !args)
    }

    type Bounds <: Tree
    type Param <: Tree
    object Param {
      def apply(
          mods: List[Mod],
          name: String,
          tparams: List[Param],
          tbounds: Bounds,
          vbounds: List[Type],
          cbounds: List[Type]
      ): Param =
        !universe.TypeParam(!mods, !name, !tparams, !tbounds, !vbounds, !cbounds)
    }
  }

  type WeakTypeTag[T]
  def weakTypeTag[T](implicit ev: WeakTypeTag[T]): WeakTypeTag[T] = ev
  implicit class XtensionTypeTag[T](val typeTag: WeakTypeTag[T]) extends AnyVal {
    def tpe: Type = !universe.weakTypeTagType(!typeTag)
  }
  type Type
  object Type {
    type TypeRef <: Type
    def typeRef(path: String): TypeRef = !universe.typeRef(path)
  }
  implicit class XtensionType(val tpe: Type) extends AnyVal {
    def caseFields: List[Denotation] = !universe.caseFields(!tpe)
    def toTypeTree: TypeTree = !universe.typeTreeOf(!tpe)
    def appliedTo(args: List[Type]): Type = !universe.appliedType(!tpe, !args)
  }

  type Self
  object Self {
    def apply(name: String, decltpe: Option[Type]): Self = !universe.Self(!name, !decltpe)
  }
  type Init
  object Init {
    def apply(tpe: TypeTree, argss: List[List[Term]]): Init =
      !universe.Init(!tpe, !argss)
  }

  type Mod
  type Defn <: Stat
  object Defn {
    object Val {
      def apply(
          mods: List[Mod],
          name: String,
          decltpe: Option[TypeTree],
          rhs: Term
      ): Defn =
        !universe.DefnVal(!mods, name, !decltpe, !rhs)
    }
    object Def {
      def apply(
          mods: List[Mod],
          name: String,
          tparams: List[TypeTree.Param],
          paramss: List[List[Term.Param]],
          decltpe: Option[TypeTree],
          body: Term
      ): Defn =
        !universe.DefnDef(!mods, !name, !tparams, !paramss, !decltpe, !body)
    }
    object Object {
      def apply(
          mods: List[Mod],
          name: String,
          templ: Template
      ): Defn =
        !universe.DefnObject(!mods, !name, !templ)
    }
  }

  object tpd {
    type Tree
    type Term <: Tree
    type DefTree <: Tree

    def root: Term = !universe.typed.ref(!universe.root)
    def typeOf(tree: Term): Type = !universe.typed.typeOf(!tree)
    def ref(sym: Symbol): Term = !universe.typed.ref(!sym)
  }

  type Splice <: Stat with Term
  implicit def tpd2splice(term: tpd.Tree): Splice = !universe.Splice(!term)

  implicit class XtensionTypedTree(val tree: tpd.Tree) extends AnyVal {
    def pos: Position = !universe.typed.treePosition(!tree)
    def syntax: String = universe.typed.treeSyntax(!tree)
    def structure: String = universe.typed.treeStructure(!tree)
  }

  implicit class XtensionTypedStatTree(val tree: tpd.Tree) extends AnyVal {
    def splice: Splice = !universe.Splice(!tree)
  }

  implicit class XtensionTypedDefTree(val tree: tpd.DefTree) extends AnyVal {
    def symbol: Symbol = !universe.typed.symOf(!tree)
  }

  implicit class XtensionTypedTermTree(val tree: tpd.Term) extends AnyVal {
    def tpe: Type = !universe.typed.typeOf(!tree)
  }
}
