package scala.macros.internal
package plugins.scalac

import scala.tools.nsc.Global
import scala.tools.nsc.plugins.{Plugin => NscPlugin}
import reflect.ReflectToolkit
import parser.HijackSyntaxAnalyzer
import typechecker.AnalyzerPlugins

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
