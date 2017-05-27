package scala.macros.internal
package plugins.scalac
package typechecker

import scala.macros.{coreVersion => foundCoreVersion}
import scala.macros.internal.config.{engineVersion => foundEngineVersion}
import scala.macros.internal.inlineMetadata
import scala.macros.Version
import reflect.ReflectToolkit

trait AnalyzerPlugins extends ReflectToolkit {
  import global._
  import pluginDefinitions._
  import analyzer.{MacroPlugin => NscMacroPlugin, _}

  object MacroPlugin extends NscMacroPlugin {
    override def pluginsMacroRuntime(expandee: Tree): Option[MacroRuntime] = {
      def ensureCompatible(
          found: Version,
          required: Option[Version],
          onError: (Position, String, String) => Unit): Unit = {
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
      }
      expandee.symbol.inlineMetadata.map {
        case metadata: inlineMetadata =>
          val requiredCoreVersion = Version.parse(metadata.coreVersion)
          val requiredEngineVersion = Version.parse(metadata.engineVersion)
          ensureCompatible(foundCoreVersion, requiredCoreVersion, IncompatibleCoreVersion)
          ensureCompatible(foundEngineVersion, requiredEngineVersion, IncompatibleEngineVersion)
          ???
      }
    }
  }
}
