package scala.meta.internal
package trees

import scala.meta.internal.prettyprinters._
import scala.meta.trees.Trees

trait TreeStructure { self: Trees =>

  private[scala] implicit def treeStructure[T <: Tree]: Structure[T] = Structure { (p, tree) =>
    // TODO: implement this
    ???
  }
}
