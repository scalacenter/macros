package scala.macros.internal
package plugins.scalac
package reflect

import scala.macros.config.Version
import scala.macros.config.coreVersion
import scala.macros.config.scalaVersion
import scala.macros.internal.config.engineVersion

trait Definitions { self: ReflectToolkit =>
  import global._
  import rootMirror._
  import treeBuilder._

  object pluginDefinitions {
    def hasLibraryDependencyOnScalamacros: Boolean = {
      NewMacroMetadata != NoSymbol
    }

    def hasPluginDependencyOnParadise: Boolean = {
      plugins.exists(_.name == "macroparadise")
    }

    private lazy val NewMacroMetadata = getClassIfDefined("scala.macros.internal.newMacroMetadata")
    private def newMacroMetadata(args: List[Tree]): Option[newMacroMetadata] = args match {
      case List(Literal(Constant(coreVersion: String)), Literal(Constant(engineVersion: String))) =>
        Some(new newMacroMetadata(coreVersion, engineVersion))
      case _ =>
        None
    }
    private def newMacroMetadata(tree: Tree): Option[newMacroMetadata] = tree match {
      case Apply(Select(New(tpt), nme.CONSTRUCTOR), args) =>
        val isNewMacroMetadata = {
          NewMacroMetadata != NoSymbol &&
          tpt.tpe != null && tpt.tpe.typeSymbol == NewMacroMetadata
        }
        if (isNewMacroMetadata) newMacroMetadata(args) else None
      case _ =>
        None
    }
    private def newMacroMetadata(ann: AnnotationInfo): Option[newMacroMetadata] = {
      val isNewMacroMetadata = {
        NewMacroMetadata != NoSymbol &&
        ann.tpe.typeSymbol == NewMacroMetadata
      }
      if (isNewMacroMetadata) newMacroMetadata(ann.args) else None
    }

    implicit class XtensionDefinitionsModifiers(mods: Modifiers) {
      def markNewMacro(pos: Position): Modifiers = {
        if (NewMacroMetadata == NoSymbol) mods
        else {
          def arg(value: String) = Literal(Constant(value)).setType(ConstantType(Constant(value)))
          val args = List(arg(coreVersion.toString), arg(engineVersion.toString))
          mods.withAnnotations(List(atPos(pos)(New(NewMacroMetadata.tpe, args: _*))))
        }
      }
      def isNewMacro: Boolean = {
        newMacroMetadata.nonEmpty
      }
      def newMacroMetadata: Option[newMacroMetadata] = {
        mods.annotations.map(pluginDefinitions.newMacroMetadata).flatten.headOption
      }
    }

    implicit class XtensionDefinitionsSymbol(sym: Symbol) {
      def isNewMacro: Boolean = {
        newMacroMetadata.nonEmpty
      }
      def newMacroMetadata: Option[newMacroMetadata] = {
        sym.annotations.map(pluginDefinitions.newMacroMetadata).flatten.headOption
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
    private def tpdRef(name: String): Tree =
      Select(Select(scalaDot(TermName("macros")), TermName("tpd")), TypeName(name))
    lazy val MacrosStat: Tree = apiRef("Stat")
    lazy val MacrosTerm: Tree = apiRef("Term")
    lazy val MacrosTypedTerm: Tree = tpdRef("Term")
    lazy val MacrosType: Tree = apiRef("Type")
    lazy val MacrosMirror: Tree = apiRef("Mirror")
    lazy val MacrosExpansion: Tree = apiRef("Expansion")
  }
}
