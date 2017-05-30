package scala.macros
package trees

import scala.reflect._
import scala.macros.internal.prettyprinters._
import scala.macros.internal.trees._

private[scala] trait Extensions extends Gensym with TreeSyntax with TreeStructure {
  self: Universe =>

  implicit class XtensionTreesTree(tree: Tree) extends Prettyprinted {
    def pos: Position = abstracts.treePos(tree)
    protected def syntax(p: Prettyprinter): Unit = treeSyntax.render(p, tree)
    protected def structure(p: Prettyprinter): Unit = treeStructure.render(p, tree)
  }

  implicit class XtensionTreesName(name: Name) {
    def value: String = abstracts.nameValue(name)
  }

  implicit class XtensionTreesLit(lit: Lit) {
    def value: Any = abstracts.litValue(lit)
  }

  implicit class XtensionTreesTerm(term: TermCompanion) {
    def fresh(prefix: String = "fresh") = Term.Name(gensym(prefix))
  }

  implicit class XtensionTreesType(term: TypeCompanion) {
    def fresh(prefix: String = "fresh") = Type.Name(gensym(prefix))
  }

  implicit class XtensionTreesPat(pat: PatCompanion) {
    def fresh(prefix: String = "fresh") = Pat.Var(Term.Name(gensym(prefix)))
  }
}
