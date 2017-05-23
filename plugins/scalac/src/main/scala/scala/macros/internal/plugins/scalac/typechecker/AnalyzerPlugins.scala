package scala.macros.internal
package plugins.scalac
package typechecker

import reflect.ReflectToolkit

trait AnalyzerPlugins extends ReflectToolkit {
  import global._
  import pluginDefinitions._
  import analyzer.{MacroPlugin => NscMacroPlugin, _}

  object MacroPlugin extends NscMacroPlugin {
    override def pluginsMacroRuntime(expandee: Tree): Option[MacroRuntime] = {
      if (expandee.symbol.isInline) {
        ???
      } else {
        None
      }
    }
  }
}
