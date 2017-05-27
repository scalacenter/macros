package scala.meta
package trees

import scala.reflect._
import scala.meta.inputs._
import scala.meta.internal.prettyprinters._
import scala.meta.internal.trees._

private[scala] trait Extensions extends Gensym with TreeSyntax with TreeStructure { self: Trees =>

  implicit class XtensionTreesTree(tree: Tree) extends Prettyprinted {
    def pos: Position = abstracts.treePos(tree)
    protected def syntax(p: Prettyprinter): Unit = treeSyntax(p, tree)
    protected def structure(p: Prettyprinter): Unit = treeStructure(p, tree)
  }

  implicit class XtensionTreesName(name: Name) {
    def value: String = abstracts.nameValue(name)
  }

  implicit class XtensionTreesLit(lit: Lit) {
    def value: Any = abstracts.litValue(lit)
  }

  implicit class XtensionTreesTerm(term: Term.type) {
    def fresh(prefix: String = "fresh") = Term.Name(gensym(prefix))
  }

  implicit class XtensionTreesType(term: Type.type) {
    def fresh(prefix: String = "fresh") = Type.Name(gensym(prefix))
  }

  implicit class XtensionTreesPat(pat: Pat.type) {
    def fresh(prefix: String = "fresh") = Pat.Var(Term.Name(gensym(prefix)))
  }

  implicit class XtensionTreesMember(member: Member) {
    def name: Name = abstracts.memberName(member)
  }

  implicit class XtensionTreesMemberTerm(member: Member.Term) {
    def name: Term.Name = (member: Member).name.asInstanceOf[Term.Name]
  }

  implicit class XtensionTreesMemberType(member: Member.Type) {
    def name: Type.Name = (member: Member).name.asInstanceOf[Type.Name]
  }

  implicit class XtensionTreesPkg(pkg: Pkg) {
    def name: Name = pkg match {
      case Term.Name(_) => name
      case Term.Select(_, name @ Term.Name(_)) => name
    }
  }
}
