package scala

package object macros {

  private[macros] val universeStore = new ThreadLocal[core.Universe]
  private[macros] def universe: ghost.type = {
    val result = universeStore.get
    if (result == null) sys.error("this API can only be called in a macro expansion")
    else result.asInstanceOf[ghost.type]
  }

  // Note(fengyun): required for Dotty, otherwise `!t` will infer Nothing for `B`,
  // causing run-time cast exceptions
  private[macros] val ghost: core.Universe = null

  private implicit class XtensionBang[A](val a: A) extends AnyVal {
    @inline def unary_![B]: B = a.asInstanceOf[B]
  }

  type Symbol
  type Denotation
  implicit class XtensionDenotation(val denot: Denotation) extends AnyVal {
    def info(implicit m: Mirror): Type = !universe.denotInfo(!denot)(!m)
    def name(implicit m: Mirror): Name = !universe.denotName(!denot)(!m)
    def sym(implicit m: Mirror): Symbol = !universe.denotSym(!denot)(!m)
  }
  type Mirror
  type Expansion
  type Tree
  implicit class XtensionTree(val tree: Tree) extends AnyVal {
    def syntax: String = universe.treeSyntax(!tree)
    def structure: String = universe.treeStructure(!tree)
  }
  type Stat <: Tree
  type Name
  implicit class XtensionName(val name: Name) extends AnyVal {
    def value: String = universe.nameValue(!name)
  }
  object Name {
    def apply(value: String): Name = !universe.nameApply(value)
  }
  type Term <: Stat
  implicit class XtensionTerm(val term: Term) extends AnyVal {
    def select(name: String): Term.Select = Term.Select(term, Term.Name(name))
    def apply(args: List[Term]): Term.Apply = Term.Apply(term, args)
    def applyType(args: List[Type]): Term.ApplyType = Term.ApplyType(term, args)
  }
  object Term {
    def fresh(prefix: String = "fresh"): Term.Name = Term.Name(universe.gensym(prefix))
    type Ref <: Term
    type Name <: Term.Ref
    object Name {
      def apply(value: String): Term.Name = !universe.termNameApply(value)
      def apply(symbol: Symbol)(implicit m: Mirror): Term.Name = !universe.termNameApplySymbol(!symbol)(!m)
      def unapply(arg: Any): Option[String] = !universe.termNameUnapply(arg)
    }
    type Select <: Term.Ref
    object Select {
      def apply(qual: Term, name: Term.Name): Term.Select =
        !universe.termSelectApply(!qual, !name)
      def unapply(arg: Any): Option[(Term.Ref, Term.Name)] =
        !universe.termSelectUnapply(arg)
    }
    type Apply <: Term
    object Apply {
      def apply(fun: Term, args: List[Term]): Term.Apply =
        !universe.termApplyApply(!fun, !args)
      def unapply(arg: Any): Option[(Term.Ref, List[Term])] =
        !universe.termApplyUnapply(arg)
    }
    type ApplyType <: Term
    object ApplyType {
      def apply(fun: Term, args: List[Type]): Term.ApplyType =
        !universe.termApplyTypeApply(!fun, !args)
    }
    type Block <: Term
    object Block {
      def apply(stats: List[Stat]): Term =
        !universe.termBlockApply(!stats)
    }
    object New {
      def apply(init: Init): Term = !universe.termNewApply(!init)
    }
    type Param
    object Param {
      def apply(
          mods: List[Mod],
          name: Name,
          decltpe: Option[Type],
          default: Option[Term]
      ): Term.Param =
        !universe.termParamApply(!mods, !name, !decltpe, !default)
    }
  }
  type Template
  object Template {
    def apply(early: List[Stat], inits: List[Init], self: Self, stats: List[Stat]): Template =
      !universe.templateApply(!early, !inits, !self, !stats)
  }
  type Lit <: Term
  object Lit {
    type String <: Lit
    object String {
      def apply(value: Predef.String): Lit.String = !universe.litStringApply(value)
    }
  }
  type Type
  implicit class XtensionType(val tpe: Type) extends AnyVal {
    def caseFields(implicit m: Mirror): List[Denotation] = !universe.caseFields(!tpe)(!m)
  }
  object Type {
    type Ref <: Type
    type Name <: Type.Ref
    object Name {
      def apply(value: String): Type.Name = !universe.typeNameApply(value)
    }
    type Select <: Type.Ref
    object Select {
      def apply(qual: Term, name: Type.Name): Type.Select =
        !universe.typeSelectApply(!qual, !name)
    }
    type Apply <: Type
    object Apply {
      def apply(fun: Type, args: List[Type]): Type.Apply =
        !universe.typeApplyApply(!fun, !args)
    }
    type Bounds
    type Param
    object Param {
      def apply(
          mods: List[Mod],
          name: Name,
          tparams: List[Type.Param],
          tbounds: Type.Bounds,
          vbounds: List[Type],
          cbounds: List[Type]
      ): Type.Param =
        !universe.typeParamApply(!mods, !name, !tparams, !tbounds, !vbounds, !cbounds)
    }
  }
  type Self
  object Self {
    def apply(name: Name, decltpe: Option[Type]): Self = !universe.selfApply(!name, !decltpe)
  }
  type Init
  object Init {
    def apply(tpe: Type, name: Name, argss: List[List[Term]]): Init =
      !universe.initApply(!tpe, !name, !argss)
  }
  type Pat
  object Pat {
    type Var <: Pat
    object Var {
      def apply(name: Term.Name): Pat.Var =
        !universe.patVarApply(!name)
    }
  }
  type Mod
  type Defn <: Stat
  object Defn {
    type Val <: Defn
    object Val {
      def apply(
          mods: List[Mod],
          pats: List[Pat],
          decltpe: Option[Type],
          rhs: Term
      ): Defn.Val =
        !universe.defnValApply(!mods, !pats, !decltpe, !rhs)
    }
    type Def <: Defn
    object Def {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Option[Type],
          body: Term
      ): Defn.Def =
        !universe.defnDefApply(!mods, !name, !tparams, !paramss, !decltpe, !body)
    }
    type Object <: Defn
    object Object {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          templ: Template
      ): Defn.Object =
        !universe.defnObjectApply(!mods, !name, !templ)
    }
  }
}
