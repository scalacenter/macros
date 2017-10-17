package scala

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

  def enclosingOwner: Symbol = !universe.enclosingOwner
  def enclosingPosition: Position = !universe.enclosingPosition
  type Input = core.Input
  type Position = core.Position
  type Symbol
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
  type Expansion
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
    type Param
    object Param {
      def apply(
          mods: List[Mod],
          name: String,
          decltpe: Option[Type],
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
  }

  type TypeTree <: Tree
  object TypeTree {
    object Name {
      def apply(value: String): TypeTree = !universe.TypeName(value)
    }

    type Bounds <: Tree
    type Param  <: Tree
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
    type Val <: Defn
    object Val {
      def apply(
          mods: List[Mod],
          name: String,
          decltpe: Option[TypeTree],
          rhs: Term
      ): Defn.Val =
        !universe.DefnVal(!mods, name, !decltpe, !rhs)
    }
    type Def <: Defn
    object Def {
      def apply(
          mods: List[Mod],
          name: String,
          tparams: List[TypeTree.Param],
          paramss: List[List[Term.Param]],
          decltpe: Option[TypeTree],
          body: Term
      ): Defn.Def =
        !universe.DefnDef(!mods, !name, !tparams, !paramss, !decltpe, !body)
    }
    type Object <: Defn
    object Object {
      def apply(
          mods: List[Mod],
          name: String,
          templ: Template
      ): Defn.Object =
        !universe.DefnObject(!mods, !name, !templ)
    }
  }

  object tpd {
    type Tree
    type Stat
    type Term <: Stat
    type Ref <: Term

    def root: Term = !universe.typed.ref(!universe.root)
    def typeOf(tree: Term): Type  = !universe.typed.typeOf(!tree)
    def ref(sym: Symbol): Ref = !universe.typed.ref(!sym)

    object Name {
      def unapply(tree: Tree): Option[Denotation] = !universe.typed.NameUnapply(!tree)
    }

    object Select {
      def apply(qual: Term, name: String): Term = !universe.typed.Select(!qual, name)
      def unapply(tree: Tree): Option[(Term, Symbol)] = !universe.typed.SelectUnapply(!tree)
    }

    object Apply {
      def apply(qual: Term, args: List[Term]): Term = !universe.typed.Apply(!qual, !args)
      def unapply(tree: Tree): Option[(Term, List[Term])] = !universe.typed.ApplyUnapply(!tree)
    }
  }

  implicit def tpd2splice(term: tpd.Stat): Stat = !universe.Splice(!term)
  implicit class XtensionTypedStatTree(val tree: tpd.Stat) extends AnyVal {
    def splice: Stat = !universe.Splice(!tree)
  }

  implicit class XtensionTypedTermTree(val tree: tpd.Term) extends AnyVal {
    def tpe: Type = !universe.typed.typeOf(!tree)
    def select(name: String): tpd.Term = tpd.Select(tree, name)
    def select(name: List[String]): tpd.Term = name.foldLeft(tree) {
      case (qual, name) => tpd.Select(qual, name)
    }
    def apply(args: List[tpd.Term]): tpd.Term = tpd.Apply(tree, args)
  }
}
