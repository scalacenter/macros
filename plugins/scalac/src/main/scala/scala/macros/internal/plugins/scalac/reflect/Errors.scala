package scala.macros.internal
package plugins.scalac
package reflect

import scala.macros.internal.config.scalamacros.BuildInfo
import scala.macros.internal.prettyprinters.EOL

trait Errors { self: ReflectToolkit =>
  import global._
  import pluginDefinitions._

  def MissingLibraryDependencyOnScalamacros(pos: Position): Unit = {
    val moduleName = BuildInfo.moduleName
    val moduleVersion = BuildInfo.version
    val ivy = s""""org.scalamacros" %% "$moduleName" % "$moduleVersion""""
    reporter.error(pos, s"new-style macros require libraryDependencies += $ivy")
  }

  def MissingPluginDependencyOnParadise(pos: Position): Unit = {
    val version = "2.1.0"
    val ivy = s""""org.scalamacros" %% "paradise" % "$version" cross CrossVersion.full"""
    reporter.error(pos, s"new-style macro annotations require addCompilerPlugin($ivy)")
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