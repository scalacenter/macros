package scala.macros.internal
package plugins.scalac
package reflect

import scala.macros.Version
import scala.macros.{scalaVersion, coreVersion}
import scala.macros.internal.config.engineVersion
import scala.macros.internal.inlineMetadata

trait Definitions { self: ReflectToolkit =>
  import global._
  import definitions._
  import rootMirror._
  import treeBuilder._

  object pluginDefinitions {
    def hasLibraryDependencyOnScalamacros: Boolean = {
      InlineMetadata != NoSymbol
    }

    def hasPluginDependencyOnParadise: Boolean = {
      plugins.exists(_.name == "macroparadise")
    }

    private lazy val InlineMetadata = getClassIfDefined("scala.macros.internal.inlineMetadata")
    private def inlineMetadata(args: List[Tree]): Option[inlineMetadata] = args match {
      case List(
          Literal(Constant(coreVersion: String)),
          Literal(Constant(engineVersion: String))) =>
        Some(new inlineMetadata(coreVersion, engineVersion))
      case _ =>
        None
    }
    private def inlineMetadata(tree: Tree): Option[inlineMetadata] = tree match {
      case Apply(Select(New(tpt), nme.CONSTRUCTOR), args) =>
        val isInlineMetadata = {
          InlineMetadata != NoSymbol &&
          tpt.tpe != null && tpt.tpe.typeSymbol == InlineMetadata
        }
        if (isInlineMetadata) inlineMetadata(args) else None
      case _ =>
        None
    }
    private def inlineMetadata(ann: AnnotationInfo): Option[inlineMetadata] = {
      val isInlineMetadata = InlineMetadata != NoSymbol && ann.tpe.typeSymbol == InlineMetadata
      if (isInlineMetadata) inlineMetadata(ann.args) else None
    }

    implicit class XtensionDefinitionsModifiers(mods: Modifiers) {
      def markInline(pos: Position): Modifiers = {
        if (InlineMetadata == NoSymbol) mods
        else {
          def arg(value: String) = Literal(Constant(value)).setType(ConstantType(Constant(value)))
          val args = List(arg(coreVersion.toString), arg(engineVersion.toString))
          mods.withAnnotations(List(atPos(pos)(New(InlineMetadata.tpe, args: _*))))
        }
      }
      def isInline: Boolean = {
        inlineMetadata.nonEmpty
      }
      def inlineMetadata: Option[inlineMetadata] = {
        mods.annotations.map(pluginDefinitions.inlineMetadata).flatten.headOption
      }
    }

    implicit class XtensionDefinitionsSymbol(sym: Symbol) {
      def isInline: Boolean = {
        inlineMetadata.nonEmpty
      }
      def inlineMetadata: Option[inlineMetadata] = {
        sym.annotations.map(pluginDefinitions.inlineMetadata).flatten.headOption
      }
    }

    lazy val ImportScalaLanguageExperimentalMacros: Tree = {
      val qual = Select(scalaDot(TermName("language")), TermName("experimental"))
      Import(qual, List(ImportSelector(TermName("macros"), -1, TermName("macros"), -1)))
    }

    lazy val ReflectContext: Tree = {
      val symbol = scalaVersion match {
        case Version(2, 10, _, _, _) => getRequiredClass("scala.reflect.macros.Context")
        case _ => getRequiredClass("scala.reflect.macros.whitebox.Context")
      }
      gen.mkAttributedRef(symbol)
    }

    private def apiRef(name: String): Tree = Select(scalaDot(TermName("macros")), TypeName(name))
    lazy val MacrosStat: Tree = apiRef("Stat")
    lazy val MacrosTerm: Tree = apiRef("Term")
    lazy val MacrosType: Tree = apiRef("Type")
    lazy val MacrosDialect: Tree = apiRef("Dialect")
    lazy val MacrosExpansion: Tree = apiRef("Expansion")
  }
}
