package scala.macros.internal
package plugins.scalac
package reflect

import scala.macros.internal.prettyprinters.EOL
import scala.macros.coreVersion

trait Errors { self: ReflectToolkit =>
  import global._
  import pluginDefinitions._

  def MissingLibraryDependencyOnScalamacros(pos: Position): Unit = {
    val version = coreVersion.syntax
    val dependency = s"""a library dependency on "org.scalamacros" %% "scalamacros" % "$version""""
    reporter.error(pos, s"new-style macros require $dependency")
  }

  def MissingPluginDependencyOnParadise(pos: Position): Unit = {
    val version = "2.1.0"
    val dependency = s"""a plugin dependency on "org.scalamacros" %% "paradise" % "$version""""
    reporter.error(pos, s"new-style macro annotations require $dependency")
  }

  def BadCoreVersion(pos: Position, found: String, required: String): Unit = {
    var message = "macro cannot be expanded, because it was compiled against an incompatible API"
    message += (EOL + " found   : " + found)
    message += (EOL + " required: " + required)
    reporter.error(pos, message)
  }

  def BadEngineVersion(pos: Position, found: String, required: String): Unit = {
    var message = "macro cannot be expanded, because it was compiled by an incompatible engine"
    message += (EOL + " found   : " + found)
    message += (EOL + " required: " + required)
    reporter.error(pos, message)
  }
}