package scala.macros.internal
package engines.scalac
package semantic

import scala.reflect.macros.contexts.Context
import scala.macros.Mirror

// NOTE: This is here to provide a simplified ABI for Java reflection,
// because that's what new-style macro shims are doing to call us.
object Mirror {
  def apply(c: Context): Mirror = {
    val universe = scala.macros.universe.get.asInstanceOf[Universe]
    universe.Mirror(c).asInstanceOf[Mirror]
  }
}
