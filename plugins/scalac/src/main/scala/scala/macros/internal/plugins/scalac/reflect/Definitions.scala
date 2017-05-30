package scala.macros.internal
package plugins.scalac
package reflect

import scala.reflect.classTag
import scala.reflect.internal.Flags._
import scala.tools.nsc.typechecker.Fingerprint._
import scala.tools.reflect.FastTrack
import scala.macros.internal.config.engineVersion
import scala.macros.internal.inlineMetadata
import scala.macros.internal.plugins.scalac.quasiquotes.Macros
import scala.macros.{scalaVersion, coreVersion}
import scala.macros.Version

trait Definitions { self: ReflectToolkit =>
  import global._
  import analyzer._
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
      case List(Literal(Constant(coreVersion: String)), Literal(Constant(engineVersion: String))) =>
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
    lazy val MacrosMirror: Tree = apiRef("Mirror")
    lazy val MacrosExpansion: Tree = apiRef("Expansion")

    private def quasiquoteMethods(name: String): List[Symbol] = {
      val macrosPackage = getModuleIfDefined("scala.macros.package")
      val xtensionQuasiquotes = macrosPackage.info.member(TypeName("XtensionQuasiquotes"))
      val interpolators = xtensionQuasiquotes.info.decls.filter(_.isModule).toList
      val methods = interpolators.map(_.info.member(TermName(name)))
      methods.filter(_.exists).distinct
    }
    lazy val QuasiquoteApplies: List[Symbol] = quasiquoteMethods("apply")
    lazy val QuasiquoteUnapplies: List[Symbol] = quasiquoteMethods("unapply")
    lazy val QuasiquoteMethods: List[Symbol] = QuasiquoteApplies ++ QuasiquoteUnapplies

    def init(): Unit = {
      QuasiquoteMethods.foreach(sym => {
        sym.initialize
        if (!sym.hasFlag(MACRO)) {
          sym.setFlag(MACRO)
          val pickle = {
            import MacroImplBinding._
            val nucleus = Ident(newTermName("macro"))
            val payload = List[(String, Any)](
              "macroEngine" -> macroEngine,
              "isBundle" -> true,
              "isBlackbox" -> false,
              "className" -> classTag[Macros].runtimeClass.getName,
              "methodName" -> sym.name.toString,
              "signature" -> List(List(Other), List(LiftedUntyped))
            )
            Apply(nucleus, payload.map({ case (k, v) => Assign(pickleAtom(k), pickleAtom(v)) }))
          }
          sym.addAnnotation(AnnotationInfo(MacroImplAnnotation.tpe, List(pickle), Nil))
        }
      })
    }
  }
}
