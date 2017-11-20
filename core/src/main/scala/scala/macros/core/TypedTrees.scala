package scala.macros.core

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
  }
}
