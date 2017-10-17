package scala.macros.core

// typed trees
trait TypedTrees { this: Universe =>
  val typed: typedApi
  trait typedApi {
    // No typed TypeTree, Type can be converted to type tree automatically
    type Tree
    type Term
    type Def

    def treePosition(tree: Tree): Position
    def treeSyntax(tree: Tree): String
    def treeStructure(tree: Tree): String

    def symOf(tree: Def): Symbol
    def typeOf(tree: Term): Type
    def ref(sym: Symbol): Term

    // only for terms, no extractor for type trees
    def NameUnapply(tree: Tree): Option[Denotation]

    def Select(tree: Term, name: String): Term
    def SelectUnapply(tree: Tree): Option[(Term, Symbol)]

    def Apply(fun: Term, args: List[Term]): Term
    def ApplyUnapply(tree: Tree): Option[(Term, List[Term])]

    def FunctionUnapply(tree: Tree): Option[(List[Symbol], Term)]
    def If(cond: Term, trueb: Term, falseb: Term): Term
    def ValDef(rhs: Term, tpOpt: Option[Type], mutable: Boolean): Tree

    def transform(tree: Tree)(pf: PartialFunction[Tree, Tree]): Tree
  }
}
