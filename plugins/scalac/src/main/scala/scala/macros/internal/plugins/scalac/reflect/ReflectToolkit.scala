package scala.macros.internal
package plugins.scalac
package reflect

import scala.tools.nsc.Global

trait ReflectToolkit extends Definitions with Names {
  val global: Global
}