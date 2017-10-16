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
  type Input
  implicit class XtensionInput(val input: Input) extends AnyVal {
    def path(implicit m: Mirror): java.nio.file.Path = universe.inputPath(!input)(!m)
    def contents(implicit m: Mirror): String = universe.inputContent(!input, 0, Int.MaxValue)(!m)
  }
  type Position
  implicit class XtensionPosition(val pos: Position) extends AnyVal {
    def start(implicit m: Mirror): Int = universe.posStart(!pos)(!m)
    def end(implicit m: Mirror): Int = universe.posEnd(!pos)(!m)
    def text(implicit m: Mirror): String = universe.inputContent(!pos.input, pos.start, pos.end)(!m)
    def line(implicit m: Mirror): Int = universe.posLine(!pos)(!m)
    def input(implicit m: Mirror): Input = !universe.posInput(!pos)(!m)
  }
  type Symbol
  implicit class XtensionSymbol(val sym: Symbol) extends AnyVal {
    def name: Name = !universe.symName(!sym)
    def owner: Option[Symbol] = !universe.symOwner(!sym)
  }

  type Denotation
  implicit class XtensionDenotation(val denot: Denotation) extends AnyVal {
    def info: Type = !universe.denotInfo(!denot)
    def name: Name = !universe.denotName(!denot)
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
  type Name
  implicit class XtensionName(val name: Name) extends AnyVal {
    def value: String = universe.nameValue(!name)
  }
  object Name {
    def apply(value: String): Name = !universe.Name(value)
  }
  type Term <: Stat
  implicit class XtensionTerm(val term: Term) extends AnyVal {
    def select(name: String): Term.Select = Term.Select(term, Term.Name(name))
    def select(name: List[String]): Term = name.foldLeft(term) {
      case (qual, name) => Term.Select(qual, Term.Name(name))
    }
    def apply(args: List[Term]): Term.Apply = Term.Apply(term, args)
    def applyType(args: List[Type]): Term.ApplyType = Term.ApplyType(term, args)
  }
  object Term {
    def fresh(prefix: String = "fresh"): Term.Name = Term.Name(universe.fresh(prefix))
    type Ref <: Term
    type Name <: Term.Ref
    object Name {
      def apply(value: String): Term.Name = !universe.TermName(value)
      def apply(symbol: Symbol): Term.Name =
        !universe.TermNameSymbol(!symbol)
      def unapply(arg: Any): Option[String] = !universe.TermNameUnapply(arg)
    }
    type Select <: Term.Ref
    object Select {
      def apply(qual: Term, name: Term.Name): Term.Select =
        !universe.TermSelect(!qual, !name)
      def unapply(arg: Any): Option[(Term.Ref, Term.Name)] =
        !universe.TermSelectUnapply(arg)
    }
    type Apply <: Term
    object Apply {
      def apply(fun: Term, args: List[Term]): Term.Apply =
        !universe.TermApply(!fun, !args)
      def unapply(arg: Any): Option[(Term.Ref, List[Term])] =
        !universe.TermApplyUnapply(arg)
    }
    type ApplyType <: Term
    object ApplyType {
      def apply(fun: Term, args: List[Type]): Term.ApplyType =
        !universe.TermApplyType(!fun, !args)
    }
    type Block <: Term
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
          name: Name,
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
  type Type
  implicit class XtensionType(val tpe: Type) extends AnyVal {
    def caseFields: List[Denotation] = !universe.caseFields(!tpe)
  }
  object Type {
    type Ref <: Type
    type Name <: Type.Ref
    object Name {
      def apply(value: String): Type.Name = !universe.TypeName(value)
    }
    type Select <: Type.Ref
    object Select {
      def apply(qual: Term, name: Type.Name): Type.Select =
        !universe.TypeSelect(!qual, !name)
    }
    type Apply <: Type
    object Apply {
      def apply(fun: Type, args: List[Type]): Type.Apply =
        !universe.TypeApply(!fun, !args)
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
        !universe.TypeParam(!mods, !name, !tparams, !tbounds, !vbounds, !cbounds)
    }
  }
  type Self
  object Self {
    def apply(name: Name, decltpe: Option[Type]): Self = !universe.Self(!name, !decltpe)
  }
  type Init
  object Init {
    def apply(tpe: Type, name: Name, argss: List[List[Term]]): Init =
      !universe.Init(!tpe, !name, !argss)
  }
  type Pat
  object Pat {
    type Var <: Pat
    object Var {
      def apply(name: Term.Name): Pat.Var =
        !universe.PatVar(!name)
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
        !universe.DefnVal(!mods, !pats, !decltpe, !rhs)
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
        !universe.DefnDef(!mods, !name, !tparams, !paramss, !decltpe, !body)
    }
    type Object <: Defn
    object Object {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          templ: Template
      ): Defn.Object =
        !universe.DefnObject(!mods, !name, !templ)
    }
  }
}
