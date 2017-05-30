package scala.macros.internal
package trees

import scala.macros.internal.prettyprinters._
import scala.macros.trees.Trees

trait TreeStructure { self: Trees =>

  private[scala] implicit def treeStructure[T <: Tree]: Structure[T] = Structure { (p, tree) =>
    // TODO: implement this
    ???
  }
}
