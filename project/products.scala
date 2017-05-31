package org.scalamacros
package build

import scala.compat.Platform.EOL
import scala.io._
import org.scalamacros.os._

trait Products { self: ScalamacrosBuild =>

  lazy val BuildProduct = "build"
  lazy val CoreProduct = "scalamacros"
  lazy val EngineScalacProduct = "scalac-engine"
  lazy val PluginScalacProduct = "scalac-plugin"
}
