package scala
package macros

package object internal {
  def unsupported: Nothing = {
    val st = new Exception().getStackTrace.drop(1)
    val name = st.head.getMethodName
    val ex = new UnsupportedOperationException(name)
    ex.setStackTrace(st)
    throw ex
  }
  def withUniverse[T](universe0: Any)(op: => T): T = {
    val oldUniverse = scala.macros.universeStore.get
    try {
      val universe = universe0.asInstanceOf[core.Universe]
      scala.macros.universeStore.set(universe)
      op
    } finally {
      scala.macros.universeStore.set(oldUniverse)
    }
  }
}
