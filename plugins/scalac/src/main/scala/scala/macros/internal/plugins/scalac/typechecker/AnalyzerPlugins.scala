package scala.macros.internal
package plugins.scalac
package typechecker

import scala.reflect.internal.util.ScalaClassLoader
import scala.macros.internal.config.{engineVersion => foundEngineVersion}
import scala.macros.internal.newMacroMetadata
import scala.macros.internal.plugins.scalac.reflect.ReflectToolkit
import scala.macros.config.{coreVersion => foundCoreVersion}
import scala.macros.config.Version

trait AnalyzerPlugins extends ReflectToolkit {
  import global._
  import pluginDefinitions._
  import analyzer.{MacroPlugin => _, _}

  object MacroPlugin extends analyzer.MacroPlugin {
    private lazy val pluginMacroClassloader: ClassLoader = {
      val classpath = global.classPath.asURLs
      macroLogVerbose("macro classloader: initializing from -cp: %s".format(classpath))
      ScalaClassLoader.fromURLs(classpath, this.getClass.getClassLoader)
    }

    private class PluginRuntimeResolver(sym: Symbol) extends MacroRuntimeResolver(sym) {
      override def resolveJavaReflectionRuntime(defaultClassLoader: ClassLoader): MacroRuntime = {
        // NOTE: defaultClassLoader only includes libraryClasspath + toolClasspath.
        // We need to include pluginClasspath, so that the new macro shim can instantiate
        // ScalacUniverse and ScalacExpansion.
        super.resolveJavaReflectionRuntime(pluginMacroClassloader)
      }
    }

    private val newMacroRuntimesCache = perRunCaches.newWeakMap[Symbol, MacroRuntime]
    private def newMacroRuntime(expandee: Tree): Option[MacroRuntime] = {
      def ensureCompatible(
          found: Version,
          required: Option[Version],
          onError: (Position, String, String) => Unit
      ): Boolean = {
        val Version(foundMajor, _, _, foundSnapshot, _) = found
        val compatible = required match {
          case Some(Version(requiredMajor, _, _, requiredSnapshot, _)) =>
            foundMajor == requiredMajor && foundSnapshot == requiredSnapshot
          case _ =>
            false
        }
        if (!compatible) {
          val requiredExplanation = required match {
            case Some(Version(requiredMajor, _, _, "", _)) => s"$requiredMajor.x.y"
            case Some(prereleaseVersion) => prereleaseVersion.toString
            case None => "unknown (failed to detect required version)"
          }
          onError(expandee.pos, found.toString, requiredExplanation)
        }
        compatible
      }
      val metadata = expandee.symbol.newMacroMetadata.get
      val requiredCoreVersion = Version.parse(metadata.coreVersion)
      val requiredEngineVersion = Version.parse(metadata.engineVersion)
      val ok1 = ensureCompatible(foundCoreVersion, requiredCoreVersion, BadCoreVersion)
      val ok2 = ensureCompatible(foundEngineVersion, requiredEngineVersion, BadEngineVersion)
      if (ok1 && ok2) {
        macroLogVerbose(s"looking for macro implementation: ${expandee.symbol}")
        def mkResolver = new PluginRuntimeResolver(expandee.symbol).resolveRuntime()
        Some(newMacroRuntimesCache.getOrElseUpdate(expandee.symbol, mkResolver))
      } else {
        None
      }
    }

    override def pluginsMacroRuntime(expandee: Tree): Option[MacroRuntime] = {
      if (expandee.symbol.newMacroMetadata.nonEmpty) newMacroRuntime(expandee)
      else None
    }
  }
}
