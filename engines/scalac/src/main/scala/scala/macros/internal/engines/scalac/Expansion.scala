package scala.macros.internal
package engines.scalac

import scala.reflect.macros.contexts.Context
import scala.macros.Expansion

// NOTE: This is here to provide a simplified ABI for Java reflection,
// because that's what new-style macro shims are doing to call us.
object Expansion {
  def apply(c: Context): Expansion = {
    val universe = scala.macros.universeStore.get.asInstanceOf[Universe]
    universe.Expansion(c).asInstanceOf[Expansion]
  }
}
