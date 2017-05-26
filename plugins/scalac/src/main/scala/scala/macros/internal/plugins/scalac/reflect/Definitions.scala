package scala.macros.internal
package plugins.scalac
package reflect

import scala.macros.scalaVersion
import scala.macros.Version

trait Definitions { self: ReflectToolkit =>
  import global._
  import definitions._
  import rootMirror._
  import treeBuilder._

  object pluginDefinitions {
    def isInlineMacrosAllowed: Boolean = {
      InternalInline != NoSymbol
    }

    def isInlineAnnotationsAllowed: Boolean = {
      isInlineMacrosAllowed && plugins.exists(_.name == "macroparadise")
    }

    private lazy val InternalInline = getClassIfDefined("scala.macros.internal.inline")

    implicit class XtensionDefinitionsModifiers(mods: Modifiers) {
      def markInline(pos: Position): Modifiers = {
        if (InternalInline == NoSymbol) mods
        else mods.withAnnotations(List(atPos(pos)(New(InternalInline))))
      }
      def isInline: Boolean = mods.annotations.exists {
        case Apply(Select(New(tpt), nme.CONSTRUCTOR), Nil) =>
          val isInline = tpt.tpe != null && tpt.tpe.typeSymbol == InternalInline
          InternalInline != NoSymbol && isInline
        case _ =>
          false
      }
    }

    implicit class XtensionDefinitionsSymbol(sym: Symbol) {
      def isInline: Boolean = sym.annotations.exists(_.tpe.typeSymbol == InternalInline)
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
