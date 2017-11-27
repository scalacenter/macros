package scala.macros.internal.plugins.socrates

import scala.macros.internal.engines.scalac.ScalacUniverse
import scala.reflect.internal.Flags
import scala.reflect.internal.util.ScalaClassLoader
import scala.reflect.macros.compiler.DefaultMacroCompiler
import scala.tools.nsc.Global
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.typechecker.Fingerprint

// "Socrates" is a dummy codename to avoid name conflicts inside scalac global._

// Most of the code in this plugin is copy-pasted from scala-compiler and adapted
// to support a custom Context (other than blackbox.Context) and different
// types for trees and type tags.
// Example support macro signature:
//
// def foo[T](e: T) = macro impl
// def impl[T:WeakTypeTag](c: Expansion)(e: tpd.Term): Term
//
// Note. The runtime values of `Expansion` is a subtype of blackbox.Context
// runtime values of `tpd.Term` and `Term` are universe.Tree.
// The main motivation for having different types for the same runtime values
// is portability. Expansion/tpd.Term/Term are abstract types that can be
// made to fit Dotty/IntelliJ trees while universe.Tree and blackbox.Context
// are tied to scala-compiler.
class SocratesCompilerPlugin(val global: Global) extends Plugin { self =>
  val name = "socrates"
  val description = "Implementation of new-style def macros for scalac"
  val components = Nil
  import global._
  import analyzer._

  global.analyzer.addMacroPlugin(SocratesMacroPlugin)

  object SocratesMacroPlugin extends analyzer.MacroPlugin { socratesMacro =>

    def isSocratesMacro(annots: List[AnnotationInfo]): Boolean =
      annots.exists(_.tpe <:< typeOf[scala.macros.socrates])

    override def pluginsMacroRuntime(
        expandee: global.analyzer.global.Tree
    ): Option[global.analyzer.MacroRuntime] = {
      if (!isSocratesMacro(expandee.symbol.annotations)) None
      else {
        macroLogVerbose(s"looking for macro implementation: ${expandee.symbol}")
        def mkResolver =
          new SocratesRuntimeResolver(expandee.symbol).resolveRuntime()
        Some(socratesRuntimesCache.getOrElseUpdate(expandee.symbol, mkResolver))
      }
    }

    override def pluginsMacroArgs(
        typer: global.analyzer.Typer,
        expandee: global.analyzer.global.Tree
    ): Option[global.analyzer.MacroArgs] = {
      if (!isSocratesMacro(expandee.symbol.annotations)) None
      else Some(socratesMacroArgs(typer, expandee))
    }

    override def pluginsTypedMacroBody(typer: Typer, ddef: DefDef): Option[Tree] = {
      val isSocratesMacro = ddef.mods.annotations.exists { annot =>
        if (annot.tpe == null) {
          // Untyped tree of the annotation
          typer.typed(annot).tpe <:< typeOf[scala.macros.socrates]
        } else {
          false
        }
      }
      if (!isSocratesMacro) None
      else socratesTypedMacroBody(typer, ddef)
    }

    // =============
    // Macro Runtime
    // =============
    private lazy val socratesClassloader: ClassLoader = {
      val classpath = global.classPath.asURLs
      macroLogVerbose("macro classloader: initializing from -cp: %s".format(classpath))
      ScalaClassLoader.fromURLs(classpath, this.getClass.getClassLoader)
    }

    private class SocratesRuntimeResolver(sym: Symbol) extends MacroRuntimeResolver(sym) {
      override def resolveJavaReflectionRuntime(defaultClassLoader: ClassLoader): MacroRuntime = {
        // NOTE: defaultClassLoader only includes libraryClasspath + toolClasspath.
        // We need to include pluginClasspath, so that the new macro shim can instantiate
        // ScalacUniverse and ScalacContext.
        super.resolveJavaReflectionRuntime(socratesClassloader)
      }
    }

    private val socratesRuntimesCache =
      perRunCaches.newWeakMap[Symbol, MacroRuntime]

    // ================
    // Macro Typed Body
    // ================

    // Copy-paste of standardTypedMacroBody and adapted to support socrates macros.
    def socratesTypedMacroBody(typer: Typer, ddef: DefDef): Option[Tree] = {
      val macroDef = ddef.symbol
      def fail() = {
        if (macroDef != null) macroDef setFlag Flags.IS_ERROR
        ddef setType ErrorType; EmptyTree
      }
      def success(macroImplRef: Tree) = {
        // +scalac deviation
        val pickle = socratesMacro.socratesPickle(macroImplRef) // custom socrates pickle.
        // -scalac deviation
        val annotInfo = AnnotationInfo(definitions.MacroImplAnnotation.tpe, List(pickle), Nil)
        macroDef withAnnotation annotInfo
        macroImplRef
      }
      val macroDdef1: ddef.type = ddef
      val typer1: typer.type = typer
      val macroCompiler = new {
        val global: self.global.type = self.global
        val typer: self.global.analyzer.Typer =
          typer1.asInstanceOf[self.global.analyzer.Typer]
        val macroDdef: self.global.DefDef = macroDdef1
      } with DefaultMacroCompiler {
        override def resolveMacroImpl: global.Tree = {
          def tryCompile(compiler: MacroImplRefCompiler): scala.util.Try[Tree] = {
            try {
              // +scalac deviation
              // TODO(olafur) implement validation on socrates macros.
              /* compiler.validateMacroImplRef(); // skip validation */
              // -scalac deviation
              scala.util.Success(compiler.macroImplRef)
            } catch {
              // +scalac deviation
              case SocratesMacroImplException(ex) =>
                // -scalac deviation
                scala.util.Failure(ex)
            }
          }
          val vanillaImplRef =
            MacroImplRefCompiler(macroDdef.rhs.duplicate, isImplBundle = false)
          val vanillaResult = tryCompile(vanillaImplRef)
          try {
            vanillaResult.get
          } catch {
            // +scalac deviation
            case SocratesMacroImplException(e) =>
              context.error(macroDdef1.pos, e.toString)
              EmptyTree
            // -scalac deviation
          }
        }
      }
      val macroImplRef = macroCompiler.resolveMacroImpl
      if (macroImplRef.isEmpty) fail() else success(macroImplRef)
      val untypedMacroImplRef = ddef.rhs.duplicate
      val typed = typer.silent(
        _.typed(markMacroImplRef(untypedMacroImplRef)),
        reportAmbiguousErrors = false
      )
      typed match {
        case SilentResultValue(macroImplRef @ SocratesMacroImplReference()) =>
          Some(macroImplRef)
        case SilentResultValue(macroError) =>
          reporter.error(macroError.pos, showCode(macroError, printTypes = true))
          None
        case SilentTypeError(err) =>
          reporter.error(err.errPos, err.errMsg)
          None
      }

    }

    // Wrapper to catch private DefaultMacroCompiler.MacroImplResolutionException.
    object SocratesMacroImplException {
      def unapply(ex: Exception): Option[Exception] = {
        if (ex.getClass.getName.contains("MacroImplResolutionException")) Some(ex)
        else None
      }
    }

    // Copy-paste of TreeInfo.MacroImplReference and adapted to socrates macros.
    object SocratesMacroImplReference {
      private def refPart(tree: Tree): Tree = tree match {
        case TypeApply(fun, _) => refPart(fun)
        case ref: RefTree => ref
        case _ => EmptyTree
      }
      def unapply(tree: Tree): Boolean = refPart(tree) match {
        case ref: RefTree =>
          // TODO(olafur) validate shape matches:
          // (arg1: tpd.Term, arg2: tpd.Term)(implicit c: socrates.Context): Term
          true
      }
    }

    object SocratesTypeTag {
      private val socratesTypeTag: Symbol =
        rootMirror.getPackageObject("scala.macros").info.member(TypeName("WeakTypeTag"))
      def unapply(symbol: Symbol): Boolean = symbol == socratesTypeTag
    }

    object SocratesTypedTree {
      private val socratesTypedTree = typeOf[scala.macros.tpd.Term]
      def unapply(arg: Type): Boolean = arg <:< socratesTypedTree
    }

    def isSocratesContext(tp: Type): Boolean = {
      tp <:< typeOf[scala.macros.Expansion]
    }

    // Copy-paste of scala.reflect.macros.util.Helpers.transformTypeTagEvidenceParams
    // and adapted to socrates macros.
    def socratesTransformTypeTagEvidenceParams(
        macroImplRef: Tree,
        transform: (Symbol, Symbol) => Symbol
    ): List[List[Symbol]] = {
      val runDefinitions = currentRun.runDefinitions
      import runDefinitions._

      val MacroContextUniverse = definitions.MacroContextUniverse
      val treeInfo.MacroImplReference(isBundle, _, _, macroImpl, _) =
        macroImplRef
      val paramss = macroImpl.paramss
      val ContextParam = paramss match {
        case Nil | _ :+ Nil =>
          NoSymbol // no implicit parameters in the signature => nothing to do
        case _ if isBundle => macroImpl.owner.tpe member nme.c
        case (cparam :: _) :: _ if definitions.isMacroContextType(cparam.tpe) => cparam
        // +scalac deviation
        case (cparam :: _) :: _ if isSocratesContext(cparam.tpe) => cparam
        // -scalac deviation
        case _ =>
          NoSymbol // no context parameter in the signature => nothing to do
      }
      def transformTag(param: Symbol): Symbol = {
        param.tpe.dealias match {
          case TypeRef(
              SingleType(SingleType(_, ContextParam), MacroContextUniverse),
              WeakTypeTagClass,
              targ :: Nil
              ) =>
            transform(param, targ.typeSymbol)
          // +scalac deviation
          case TypeRef(_, SocratesTypeTag(), targ :: Nil) =>
            transform(param, targ.typeSymbol)
          // -scalac deviation
          case _ => param
        }
      }
      ContextParam match {
        case NoSymbol => paramss
        case _ =>
          paramss.last.map(transformTag).filter(_.exists) match {
            case Nil => paramss.init
            case transformed => paramss.init :+ transformed
          }
      }
    }

    // Copy-paste of scala.tools.nsc.typechecker.Macros.MacroImplBinding.pickle
    // and adapted to socrates macros.
    def socratesPickle(macroImplRef: Tree): Tree = {
      val runDefinitions = currentRun.runDefinitions
      import runDefinitions._
      val treeInfo.MacroImplReference(isBundle, isBlackbox, owner, macroImpl, targs) =
        macroImplRef

      // todo. refactor when fixing scala/bug#5498
      def className: String = {
        def loop(sym: Symbol): String = sym match {
          case sym if sym.isTopLevel =>
            val suffix = if (sym.isModuleClass) "$" else ""
            sym.fullName + suffix
          case sym =>
            val separator = if (sym.owner.isModuleClass) "" else "$"
            loop(sym.owner) + separator + sym.javaSimpleName.toString
        }

        loop(owner)
      }
      import scala.tools.nsc.typechecker.Fingerprint._
      import definitions.RepeatedParamClass

      def signature: List[List[Fingerprint]] = {
        def fingerprint(tpe: Type): Fingerprint = {
          tpe.dealiasWiden match {
            case TypeRef(_, RepeatedParamClass, underlying :: Nil) =>
              fingerprint(underlying)
            case ExprClassOf(_) => LiftedTyped
            case TreeType() => LiftedUntyped
            // +scalac deviation
            case SocratesTypedTree() => LiftedUntyped
            // -scalac deviation
            case _ => Other
          }
        }

        val transformed =
          socratesTransformTypeTagEvidenceParams(macroImplRef, (param, tparam) => tparam)
        mmap(transformed)(p => if (p.isTerm) fingerprint(p.info) else Tagged(p.paramPos))
      }

      val payload = List[(String, Any)](
        // TODO(olafur) attach new macro engine ID while avoiding:
        // > macro cannot be expanded, because it was compiled by an incompatible macro engine
        "macroEngine" -> macroEngine,
        "isBundle" -> isBundle,
        "isBlackbox" -> isBlackbox,
        "className" -> className,
        "methodName" -> macroImpl.name.toString,
        "signature" -> signature
      )

      // the shape of the nucleus is chosen arbitrarily. it doesn't carry any payload.
      // it's only necessary as a stub `fun` for an Apply node that carries metadata in its `args`
      // so don't try to find a program element named "macro" that corresponds to the nucleus
      // I just named it "macro", because it's macro-related, but I could as well name it "foobar"
      val nucleus = Ident(newTermName("macro"))
      val wrapped = Apply(nucleus, payload map {
        case (k, v) =>
          Assign(MacroImplBinding.pickleAtom(k), MacroImplBinding.pickleAtom(v))
      })
      val pickle = gen.mkTypeApply(wrapped, targs map (_.duplicate))

      // assign NoType to all freshly created AST nodes
      // otherwise pickler will choke on tree.tpe being null
      // there's another gotcha
      // if you don't assign a ConstantType to a constant
      // then pickling will crash
      new Transformer {
        override def transform(tree: Tree): Tree = {
          tree match {
            case Literal(const @ Constant(x)) if tree.tpe == null =>
              tree setType ConstantType(const)
            case _ if tree.tpe == null => tree setType NoType
            case _ => ;
          }
          super.transform(tree)
        }
      }.transform(pickle)
    }

    // ==========
    // Macro Args
    // ==========

    // Copy-paste of standardMacroArgs and adapted to support socrates macros.
    def socratesMacroArgs(
        typer: global.analyzer.Typer,
        expandee: global.analyzer.global.Tree
    ): global.analyzer.MacroArgs = {
      val standardArgs = standardMacroArgs(typer, expandee)
      val prefix = new treeInfo.Applied(expandee).core match {
        case Select(qual, _) => qual
        case _ => EmptyTree
      }
      val socratesContext = expandee.attachments
        .get[MacroRuntimeAttachment]
        .flatMap(_.macroContext)
        .getOrElse(socratesMacroContext(typer, prefix, expandee))
      scala.macros.universeStore.set(ScalacUniverse(socratesContext))
      val socratesArgs = standardArgs.copy(c = socratesContext)
      socratesArgs
    }

    // Copy-paste of scala.tools.nsc.typechecker.Macsros.macroContext
    // and adapted to socrates macros.
    def socratesMacroContext(typer: Typer, prefixTree: Tree, expandeeTree: Tree): MacroContext = {
      new {
        val universe: self.global.type = self.global
        val callsiteTyper: universe.analyzer.Typer =
          typer.asInstanceOf[global.analyzer.Typer]
        val expandee = universe.analyzer
          .macroExpanderAttachment(expandeeTree)
          .original
          .orElse(duplicateAndKeepPositions(expandeeTree))
      } with UnaffiliatedMacroContext with scala.macros.Expansion {
        val prefix = Expr[Nothing](prefixTree)(TypeTag.Nothing)
        override def toString: String =
          "MacroContext(%s@%s +%d)".format(
            expandee.symbol.name,
            expandee.pos,
            enclosingMacros.length - 1 /* exclude myself */
          )
      }
    }

  }
}
