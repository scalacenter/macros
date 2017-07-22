package scala.macros.internal
package trees

import scala.macros.internal.prettyprinters._
import scala.macros.Universe

trait TreeStructure { self: Universe =>

  private[macros] implicit def treeStructure[T <: Tree]: Structure[T] = Structure { (p, tree) =>
    // TODO: implement this
    ???
  }
}
