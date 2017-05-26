package scala.macros.internal
package plugins.scalac
package reflect

import scala.macros.coreVersion
import scala.macros.internal.config.engineVersion

trait Errors { self: ReflectToolkit =>
  import global._
  import pluginDefinitions._

  def ensureInlineMacrosAllowed(pos: Position): Unit = {
    if (!isInlineMacrosAllowed) MissingLibraryDependencyOnScalamacros(pos)
  }

  def ensureInlineAnnotationsAllowed(pos: Position): Unit = {
    if (!isInlineAnnotationsAllowed) MissingPluginDependencyOnParadise(pos)
  }

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
}