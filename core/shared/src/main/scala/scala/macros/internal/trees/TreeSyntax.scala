package scala.macros.internal
package trees

import scala.macros.internal.prettyprinters._
import scala.macros.trees.Trees

trait TreeSyntax { self: Trees =>

  private[scala] implicit def treeSyntax[T <: Tree]: Syntax[T] = Syntax { (p, tree) =>
    // TODO: implement this
    ???
  }
}
