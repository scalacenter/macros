package scala.macros.internal
package plugins.scalac
package reflect

import scala.tools.nsc.Global

trait ReflectToolkit extends Errors with Definitions with Names {
  val global: Global
}