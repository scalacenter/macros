package scala

package object macros
    extends scala.macros.config.Api
    with scala.macros.config.Aliases
    with scala.macros.inputs.Api
    with scala.macros.inputs.Aliases
    with scala.macros.prettyprinters.Api
    with scala.macros.prettyprinters.Aliases {

  private[macros] val universeStore = new ThreadLocal[scala.macros.Universe]
  private[macros] def universe: Universe = universeStore.get
  private[macros] def abstracts: Universe#Abstracts = {
    if (universeStore.get == null) sys.error("this API can only be called in a macro expansion")
    universeStore.get.abstracts
  }

  private implicit class XtensionBang[A](val a: A) extends AnyVal {
    @inline def unary_![B]: B = a.asInstanceOf[B]
  }

  type Symbol
  type Denotation
  implicit class XtensionDenotation(val denot: Denotation) extends AnyVal {
    def info: Type = ???
    def name: Name = ???
    def sym: Symbol = ???
  }
  type Mirror
  type Expansion
  type Tree
  type Stat <: Tree
  implicit class XtensionTree(val tree: Tree) extends AnyVal {
    def syntax: String = ???
    def structure: String = ???
  }
  type Name
  implicit class XtensionName(val name: Name) extends AnyVal {
    def value: String = ???
  }
  object Name {
    def apply(value: String): Name = ???
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
      def apply(value: String): Term.Name = ???
      def apply(symbol: Symbol): Term.Name = ???
      def unapply(tree: Any): Option[String] = ???
    }
    type Select <: Term.Ref
    object Select {
      def apply(qual: Term, name: Term.Name): Term.Select =
        !abstracts.TermSelect.apply(!qual, !name)
      def unapply(arg: Any): Option[(Term.Ref, Term.Name)] =
        !abstracts.TermSelect.unapply(arg)
    }
    type Apply <: Term
    object Apply {
      def apply(fun: Term, args: List[Term]): Term.Apply =
        !abstracts.TermApply.apply(!fun, !args)
      def unapply(arg: Any): Option[(Term.Ref, List[Term])] =
        !abstracts.TermApply.unapply(arg)
    }
    type ApplyType <: Term
    object ApplyType {
      def apply(fun: Term, args: List[Type]): Term.ApplyType =
        !abstracts.TermApplyType.apply(!fun, !args)
      def unapply(arg: Any): Option[(Term.Ref, List[Term])] =
        !abstracts.TermApplyType.unapply(arg)
    }
    type Block <: Term
    object Block {
      def apply(stats: List[Stat]): Term =
        !abstracts.TermBlock.apply(!stats)
    }
    object New {
      def apply(init: Init): Term = ???
    }
    type Param
    object Param {
      def apply(
          mods: List[Mod],
          name: Name,
          decltpe: Option[Type],
          default: Option[Term]
      ): Term.Param = ???
    }
  }
  type Template
  object Template {
    def apply(early: List[Stat], inits: List[Init], self: Self, stats: List[Stat]): Template = ???
  }
  type Lit <: Term
  object Lit {
    type String <: Lit
    object String {
      def apply(value: Predef.String): Lit.String = ???
    }
  }
  type Type
  implicit class XtensionType(val tpe: Type) extends AnyVal {
    def caseFields(implicit m: Mirror): List[Denotation] = !abstracts.caseFields(!tpe)(!m)
  }
  object Type {
    type Ref <: Type
    type Name <: Type.Ref
    object Name {
      def apply(value: String) = ???
      def unapply(tree: Any) = ???
    }
    type Select <: Type.Ref
    object Select {
      def apply(qual: Term, name: Type.Name): Type.Select =
        !abstracts.TypeSelect.apply(!qual, !name)
      def unapply(arg: Any): Option[(Term.Ref, Type.Name)] =
        !abstracts.TypeSelect.unapply(arg)
    }
    type Apply <: Type
    object Apply {
      def apply(fun: Type, args: List[Type]): Type.Apply =
        !abstracts.TypeApply.apply(!fun, !args)
      def unapply(arg: Any): Option[(Type, List[Type])] =
        !abstracts.TypeApply.unapply(arg)
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
      ): Type.Param = ???
    }
  }
  type Self
  object Self {
    def apply(name: Name, decltpe: Option[Type]): Self = ???
  }
  type Init
  object Init {
    def apply(tpe: Type, name: Name, argss: List[List[Term]]): Init = ???
  }
  type Pat
  object Pat {
    type Var <: Pat
    object Var {
      def apply(name: Term.Name): Pat.Var =
        !abstracts.PatVar.apply(!name)
      def unapply(arg: Any): Option[Term.Name] =
        !abstracts.PatVar.unapply(arg)
    }
  }
  type Mod
  type Defn <: Stat
  object Defn {
    type Val <: Defn
    object Val {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Option[Type], rhs: Term): Defn.Val = ???
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
      ): Defn.Def = ???
    }
    type Object <: Defn
    object Object {
      def apply(mods: List[Mod], name: Term.Name, templ: Template): Defn.Object = ???
    }
  }
}
