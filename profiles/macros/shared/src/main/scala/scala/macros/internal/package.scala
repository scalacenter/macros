package scala
package macros

package object internal {
  def withUniverse[T](universe0: Any)(op: => T): T = {
    val oldUniverse = scala.macros.universe.get
    try {
      val universe = universe0.asInstanceOf[scala.macros.Universe]
      scala.macros.universe.set(universe)
      op
    } finally {
      scala.macros.universe.set(oldUniverse)
    }
  }
}
