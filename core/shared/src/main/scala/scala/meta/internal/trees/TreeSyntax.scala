package scala.meta.internal
package trees

import scala.meta.internal.prettyprinters._
import scala.meta.trees.Trees

trait TreeSyntax { self: Trees =>

  private[scala] implicit def treeSyntax[T <: Tree]: Syntax[T] = Syntax { (p, tree) =>
    // TODO: implement this
    ???
  }
}
