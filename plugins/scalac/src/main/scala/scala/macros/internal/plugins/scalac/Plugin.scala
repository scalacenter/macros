package scala.macros.internal
package plugins.scalac

import scala.tools.nsc.Global
import scala.tools.nsc.plugins.{Plugin => NscPlugin}
import scala.macros.internal.plugins.scalac.reflect.ReflectToolkit
import scala.macros.internal.plugins.scalac.parser.HijackSyntaxAnalyzer
import scala.macros.internal.plugins.scalac.typechecker.AnalyzerPlugins

class Plugin(val global: Global)
    extends NscPlugin
    with ReflectToolkit
    with HijackSyntaxAnalyzer
    with AnalyzerPlugins {
  val name = "scalamacros-plugins-scalac"
  val description = "Implementation of new-style Scala macros for scalac"
  val components = Nil
  hijackSyntaxAnalyzer()
  global.analyzer.addMacroPlugin(MacroPlugin)
}
