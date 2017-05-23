package scala
package macros

package object internal {
  def withUniverse[T](universe: Universe)(op: => T): T = {
    val oldUniverse = scala.macros.universe.get
    try {
      scala.macros.universe.set(universe)
      op
    } finally {
      scala.macros.universe.set(oldUniverse)
    }
  }
}
