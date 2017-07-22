package scala.macros.internal
package trees

import scala.macros.internal.prettyprinters._
import scala.macros.Universe

trait TreeSyntax { self: Universe =>

  private[macros] implicit def treeSyntax[T <: Tree]: Syntax[T] = Syntax { (p, tree) =>
    // TODO: implement this
    ???
  }
}
