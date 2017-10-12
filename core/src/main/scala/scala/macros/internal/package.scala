package scala
package macros

package object internal {
  def withUniverse[T](universe0: Any)(op: => T): T = {
    val oldUniverse = scala.macros.universeStore.get
    try {
      val universe = universe0.asInstanceOf[scala.macros.Universe]
      scala.macros.universeStore.set(universe)
      op
    } finally {
      scala.macros.universeStore.set(oldUniverse)
    }
  }
}
