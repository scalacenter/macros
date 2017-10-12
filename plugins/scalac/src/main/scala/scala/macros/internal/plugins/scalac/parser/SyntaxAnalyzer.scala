package scala.macros.internal
package plugins.scalac
package parser

import scala.reflect.internal.Flags // no wildcard import because of ambiguity with Tokens._
import scala.tools.nsc.ast.parser.{SyntaxAnalyzer => NscSyntaxAnalyzer, BracePatch}
import scala.tools.nsc.ast.parser.Tokens._
import scala.tools.nsc.Phase
import scala.macros.internal.config.engineVersion
import scala.macros.internal.plugins.scalac.reflect.ReflectToolkit
import scala.macros.coreVersion

abstract class SyntaxAnalyzer extends NscSyntaxAnalyzer with ReflectToolkit {
  import global._
  import definitions._
  import pluginDefinitions._
  import treeBuilder._

  val runsAfter = List[String]()
  val runsRightAfter = None
  override val initial = true

  private def initialUnitBody(unit: CompilationUnit): Tree = {
    if (unit.isJava) new JavaUnitParser(unit).parse()
    else if (currentRun.parsing.incompleteHandled) new PluginUnitParser(unit).parse()
    else new PluginUnitParser(unit).smartParse()
  }

  def newUnitParser(unit: CompilationUnit): UnitParser = new PluginUnitParser(unit)
  private class PluginUnitParser(unit: global.CompilationUnit, patches: List[BracePatch])
      extends UnitParser(unit, Nil) {
    def this(unit: global.CompilationUnit) = this(unit, Nil)

    override def funDefRest(start: Offset, nameOff: Offset, mods: Modifiers, name: Name): Tree = {
      super.funDefRest(start, nameOff, mods, name) match {
        case newmacro @ DefDef(mods, _, _, _, _, Block(_, _) | Apply(_, _)) if mods.isMacro =>
          if (!hasLibraryDependencyOnScalamacros) MissingLibraryDependencyOnScalamacros(r2p(start))
          copyDefDef(newmacro)(mods = mods.markNewMacro(r2p(start)))
        case other =>
          other
      }
    }

    // NOTE: This is an initial version of the transformation that implements new-style macros.
    // See Desugared.scala in `tests` for an example of the transformation.
    // TODO: In the future, it may make sense to perform this transformation during typechecking.
    // However that strategy is much more complicated, so it doesn't fit this prototype.

    override def topStatSeq(): List[Tree] = {
      super.topStatSeq().flatMap(stat => desugarNewMacros(stat, toplevel = true))
    }

    override def templateStats(): List[Tree] = {
      super.templateStats().flatMap(stat => desugarNewMacros(stat, toplevel = false))
    }

    override def blockStatSeq(): List[Tree] = {
      super.blockStatSeq().flatMap(stat => desugarNewMacros(stat, toplevel = false))
    }

    private def desugarNewMacros(owner: Tree, toplevel: Boolean): List[Tree] = {
      owner match {
        case owner: ImplDef =>
          val xstats1 = owner.impl.body.map {
            case stat: DefDef if stat.mods.isNewMacro =>
              if (toplevel) {
                desugarNewMacro(owner, stat)
              } else {
                val restr = "new-style macros must be declared in top-level classes and modules"
                syntaxError(stat.pos.point, s"implementation restriction: $restr")
                (List(stat), Nil)
              }
            case other =>
              (List(other), Nil)
          }
          val (statss1, mstatss1) = xstats1.unzip
          if (mstatss1.exists(_.nonEmpty)) {
            val impl1 = deriveTemplate(owner.impl)(_ => statss1.flatten)
            val owner1 = owner match {
              case cdef: ClassDef => copyClassDef(cdef)(impl = impl1)
              case mdef: ModuleDef => copyModuleDef(mdef)(impl = impl1)
            }
            val helperName = owner.name.newMacroModuleName
            val helper1 = atPos(owner.pos)(q"object $helperName { ..${mstatss1.flatten} }")
            List(atPos(owner1.pos)(ImportScalaLanguageExperimentalMacros), owner1, helper1)
          } else {
            List(owner)
          }
        case other =>
          List(other)
      }
    }

    private def desugarNewMacro(owner: ImplDef, tree: DefDef): (List[Tree], List[Tree]) = {
      val DefDef(mods, name, tparams, vparamss, tpt, rhs) = tree
      val isMacroAnnotation = {
        val isClass = owner.isInstanceOf[ClassDef]
        val extendsMacroAnnotation = owner.impl.parents.exists {
          case Ident(NewMacroAnnotationParentName) => true
          case _ => false
        }
        val hasTheRightName = name == NewMacroAnnotationMethodName
        isClass && extendsMacroAnnotation && hasTheRightName
      }
      if (isMacroAnnotation) {
        if (!hasPluginDependencyOnParadise) MissingPluginDependencyOnParadise(tree.pos)
      }
      val macroDef = atPos(tree.pos) {
        def rhs1(tparams1: List[TypeDef]) = {
          val result = Select(Ident(owner.name.newMacroModuleName), name.newMacroShimName)
          atPos(tree.pos)({
            val targs = tparams1.map(p => atPos(p.pos)(Ident(p.name)))
            if (targs.nonEmpty) TypeApply(result, targs) else result
          })
        }
        if (isMacroAnnotation) {
          val mods1 = mods | Flags.MACRO
          val name1 = TermName("macroTransform")
          val tparams1 = {
            if (tparams.nonEmpty) {
              val where = s"on the annotation class instead of the macro"
              val message = s"new-style macro annotations must have type parameters $where"
              syntaxError(tree.pos.point, message)
              return (List(tree), Nil)
            } else {
              Nil
            }
          }
          val vparamss1 = {
            val param = vparamss.flatten.headOption.getOrElse(tree)
            val tpt1 = repeatedApplication(scalaDot(TypeName("Any")))
            val param1 = ValDef(Modifiers(Flags.PARAM), TermName("annottees"), tpt1, EmptyTree)
            List(List(atPos(param.pos)(param1)))
          }
          val tpt1 = tpt // NOTE: intentionally not duplicated
          DefDef(mods1, name1, tparams1, vparamss1, tpt1, rhs1(Nil))
        } else {
          val mods1 = mods | Flags.MACRO
          val name1 = name.newMacroDefName
          val tparams1 = tparams // NOTE: intentionally not duplicated
          val vparamss1 = vparamss // NOTE: intentionally not duplicated
          val tpt1 = tpt // NOTE: intentionally not duplicated
          DefDef(mods1, name1, tparams1, vparamss1, tpt, rhs1(tparams1))
        }
      }
      val shimDef = atPos(tree.pos) {
        val mods2 = NoMods
        val name2 = name.newMacroShimName
        val tparams2 = tparams.map(_.duplicate)
        val cname2 = unit.freshTermName("c$")
        def cExprOf(tpt2: Tree): Tree = atPos(tpt2.pos) {
          tpt2 match {
            case AppliedTypeTree(fun2 @ Select(_, tpnme.REPEATED_PARAM_CLASS_NAME), List(tpt2)) =>
              AppliedTypeTree(fun2, List(cExprOf(tpt2)))
            case _ =>
              AppliedTypeTree(Select(Ident(cname2), TypeName("Expr")), List(tpt2))
          }
        }
        def cWeakTypeTagOf(tpt2: Tree): Tree = atPos(tpt2.pos) {
          AppliedTypeTree(Select(Ident(cname2), TypeName("WeakTypeTag")), List(tpt2))
        }
        val (vparamss2, rhs2) = {
          val c2 = {
            atPos(tree.pos)(ValDef(Modifiers(Flags.PARAM), cname2, ReflectContext, EmptyTree))
          }
          val vvparamss2 = {
            if (isMacroAnnotation) {
              val param = vparamss.flatten.headOption.getOrElse(tree)
              val tpt2 = cExprOf(repeatedApplication(scalaDot(TypeName("Any"))))
              val param2 = ValDef(Modifiers(Flags.PARAM), TermName("annottees"), tpt2, EmptyTree)
              List(List(atPos(param.pos)(param2)))
            } else {
              vparamss.map(_.map(p => copyValDef(p)(tpt = cExprOf(p.tpt.duplicate))))
            }
          }
          val vtparamss2 = {
            if (tparams.isEmpty) Nil
            else {
              List(tparams.map({
                case tparam @ TypeDef(_, name, _, _) =>
                  val vtparamName2 = TermName("typetag$" + name)
                  val vtparamTpt2 = cWeakTypeTagOf(Ident(name))
                  val vtparam2 =
                    ValDef(Modifiers(Flags.IMPLICIT), vtparamName2, vtparamTpt2, EmptyTree)
                  atPos(tparam.pos)(vtparam2)
              }))
            }
          }
          val vparamss2 = List(List(c2)) ++ vvparamss2 ++ vtparamss2
          val rhs2 = shimRhs(name, c2, vvparamss2.flatten, vtparamss2.flatten, isMacroAnnotation)
          (vparamss2, atPos(rhs.pos)(rhs2))
        }
        val tpt2 = {
          if (isMacroAnnotation) cExprOf(scalaDot(TypeName("Any")))
          else cExprOf(tpt.duplicate)
        }
        DefDef(mods2, name2, tparams2, vparamss2, tpt2, rhs2)
      }
      val implDef = atPos(rhs.pos) {
        val mods3 = NoMods
        val name3 = name.newMacroImplName
        val tparams3 = Nil
        val thisParamName3 = unit.freshTermName("prefix$")
        val vparamss3 = {
          val thisParam3 = atPos(tree.pos)(ValDef(NoMods, thisParamName3, MacrosTerm, EmptyTree))
          val vvparams3 = vparamss.flatten.map(p => {
            val tpt3 = if (isMacroAnnotation) MacrosStat else MacrosTerm
            atPos(p.pos)(ValDef(NoMods, p.name, tpt3, EmptyTree))
          })
          val vtparams3 = tparams.map(p => {
            atPos(p.pos)(ValDef(NoMods, p.name.toTermName, MacrosType, EmptyTree))
          })
          val mirrorParam3 = {
            val name3 = unit.freshTermName("mirror$")
            atPos(tree.pos)(ValDef(Modifiers(Flags.IMPLICIT), name3, MacrosMirror, EmptyTree))
          }
          val expansionParam3 = {
            val name3 = unit.freshTermName("expansion$")
            atPos(tree.pos)(ValDef(Modifiers(Flags.IMPLICIT), name3, MacrosExpansion, EmptyTree))
          }
          val vcapabilities3 = {
            if (isMacroAnnotation) List(expansionParam3)
            else List(mirrorParam3, expansionParam3)
          }
          List(List(thisParam3) ++ vvparams3 ++ vtparams3, vcapabilities3)
        }
        val tpt3 = if (isMacroAnnotation) MacrosStat else MacrosTerm
        val rhs3 = {
          object transformer extends Transformer {
            override def transform(tree: Tree): Tree = tree match {
              case This(tpnme.EMPTY) => atPos(tree.pos)(Ident(thisParamName3))
              case tree => super.transform(tree)
            }
          }
          transformer.transform(rhs)
        }
        DefDef(mods3, name3, tparams3, vparamss3, tpt3, rhs3)
      }
      val abiDef = atPos(rhs.pos) {
        // NOTE: abiDef is different from implDef,
        // because sometimes we may want implDef to be called in a particular way.
        // For instance, old-style macros trim stacktraces of macro-generated exceptions
        // by looking for a method whose name ends with `macroExpandWithRuntime`.
        // Therefore, for optimal user experience we really want implDef to be named like that.
        val DefDef(_, name3, _, vparamss3, tpt3, _) = implDef
        val mods4 = NoMods
        val name4 = name.newMacroAbiName
        val tparams4 = Nil
        val vparamss4 = vparamss3.map(_.map(_.duplicate))
        val tpt4 = tpt3.duplicate
        val rhs4 = {
          val vargss4 = vparamss3.map(_.map(p => Ident(p.name)))
          val core4 = Select(This(tpnme.EMPTY), name3)
          atPos(rhs.pos)(vargss4.foldLeft(core4: Tree)((curr, args) => Apply(curr, args)))
        }
        DefDef(mods4, name4, tparams4, vparamss4, tpt4, rhs4)
      }
      val stats = List(macroDef)
      val mstats = List(shimDef, implDef, abiDef)
      (stats, mstats)
    }

    private def shimRhs(
        name: TermName,
        c: ValDef,
        vvparams: List[ValDef],
        vtparams: List[ValDef],
        isMacroAnnotation: Boolean
    ): Tree = {
      val cName = c.name
      val thisArgName = unit.freshTermName("prefix$")
      val otherArgDefs = {
        if (isMacroAnnotation) {
          val List(defnParam) = vvparams
          val defnArgName = unit.freshTermName(defnParam.name.toString + "$")
          val defnArgDef = q"""
            val $defnArgName = {
              try {
                annottees.map(_.tree.asInstanceOf[_root_.scala.macros.Stat]) match {
                  case _root_.scala.Seq(tree) => tree
                  case trees => _root_.scala.macros.Term.Block(trees.toList)
                }
              } catch {
                case ex: _root_.java.lang.ClassCastException => failMacroEngine(ex)
              }
            }
          """
          List(defnArgDef)
        } else {
          val vargDefs = vvparams.map(vvparam => {
            val argName = unit.freshTermName(vvparam.name.toString + "$")
            q"""
              val $argName = {
                try {
                  ${vvparam.name}.tree.asInstanceOf[_root_.scala.macros.Term]
                } catch {
                  case ex: _root_.java.lang.ClassCastException => failMacroEngine(ex)
                }
              }
            """
          })
          val targDefs = vtparams.map(vtparam => {
            val argName = unit.freshTermName(vtparam.name.toString.stripSuffix("typetag$") + "$")
            q"""
              val $argName = {
                try {
                  val tpt = $cName.universe.TypeTree(${vtparam.name}.tpe)
                  tpt.asInstanceOf[_root_.scala.macros.Type]
                } catch {
                  case ex: _root_.java.lang.ClassCastException => failMacroEngine(ex)
                }
              }
            """
          })
          vargDefs ++ targDefs
        }
      }
      val otherArgNames = otherArgDefs.map(_.name)
      val mirrorArgName = unit.freshTermName("mirror$")
      val expansionArgName = unit.freshTermName("expansion$")
      val capabilityArgNames = {
        if (isMacroAnnotation) List(expansionArgName)
        else List(mirrorArgName, expansionArgName)
      }
      val implName = name.newMacroImplName
      q"""
        var foundEngine = "old-style " + _root_.scala.util.Properties.versionNumberString
        def failMacroEngine(ex: Exception): _root_.scala.Nothing = {
          val requiredEngine = ${"new-style " + engineVersion.toString}
          var msg = "macro cannot be expanded, because it was compiled by an incompatible engine"
          msg += (_root_.scala.macros.internal.prettyprinters.EOL + " found   : " + foundEngine)
          msg += (_root_.scala.macros.internal.prettyprinters.EOL + " required: " + requiredEngine)
          ex.printStackTrace
          $cName.abort($cName.enclosingPosition, msg)
        }
        def invokeEngineMethod(
            moduleName: _root_.java.lang.String,
            methodName: _root_.java.lang.String,
            args: _root_.scala.AnyRef*): _root_.scala.AnyRef = {
          try {
            val pluginClassLoader = this.getClass.getClassLoader
            val moduleClass = pluginClassLoader.loadClass(moduleName + "$$")
            val moduleField = moduleClass.getDeclaredField("MODULE$$")
            val module = moduleField.get(null)
            var methods = module.getClass.getDeclaredMethods.filter(_.getName == methodName).toList
            methods = methods.filter(m => !m.isBridge && !m.isSynthetic)
            methods match {
              case List(method) =>
                method.invoke(module, args: _*)
              case Nil =>
                val message = moduleName + "." + methodName + " matches no methods"
                failMacroEngine(new _root_.java.lang.IllegalStateException(message))
              case other =>
                val methods = other.toList.mkString(", ")
                val message = moduleName + "." + methodName + " matches multiple methods " + methods
                failMacroEngine(new _root_.java.lang.IllegalStateException(message))
            }
          } catch {
            case ex: _root_.java.lang.ClassNotFoundException => failMacroEngine(ex)
            case ex: _root_.java.lang.NoSuchFieldException => failMacroEngine(ex)
            case ex: _root_.java.lang.IllegalAccessException => failMacroEngine(ex)
            case ex: _root_.java.lang.IllegalArgumentException => failMacroEngine(ex)
            case ex: _root_.java.lang.reflect.InvocationTargetException => throw ex
          }
        }
        val ConfigPackage = "scala.macros.internal.config.package"
        val foundVersion = invokeEngineMethod(ConfigPackage, "engineVersion")
        try foundEngine = "new-style " + foundVersion.toString
        catch { case ex: _root_.java.lang.ClassCastException => failMacroEngine(ex) }

        val ScalacUniverse = "scala.macros.internal.engines.scalac.ScalacUniverse"
        val scalacUniverse = invokeEngineMethod(ScalacUniverse, "apply", $cName.universe)
        _root_.scala.macros.internal.withUniverse(scalacUniverse) {
          val $thisArgName = {
            try $cName.macroApplication.asInstanceOf[_root_.scala.macros.Term]
            catch { case ex: _root_.java.lang.ClassCastException => failMacroEngine(ex) }
          }
          ..$otherArgDefs
          val ScalacMirror = "scala.macros.internal.engines.scalac.semantic.Mirror"
          val $mirrorArgName = {
            val $mirrorArgName = invokeEngineMethod(ScalacMirror, "apply", $cName)
            try $mirrorArgName.asInstanceOf[_root_.scala.macros.Mirror]
            catch { case ex: _root_.java.lang.ClassCastException => failMacroEngine(ex) }
          }
          val ScalacExpansion = "scala.macros.internal.engines.scalac.Expansion"
          val $expansionArgName = {
            val $expansionArgName = invokeEngineMethod(ScalacExpansion, "apply", $cName)
            try $expansionArgName.asInstanceOf[_root_.scala.macros.Expansion]
            catch { case ex: _root_.java.lang.ClassCastException => failMacroEngine(ex) }
          }
          val result = $implName($thisArgName, ..$otherArgNames)(..$capabilityArgNames)
          $cName.Expr[_root_.scala.Nothing](result.asInstanceOf[$cName.Tree])
        }
      """
    }
  }

  override def newPhase(prev: Phase): StdPhase = new StdPhase(prev) {
    override val checkable = false
    override val keepsTypeParams = false

    def apply(unit: CompilationUnit) {
      informProgress("parsing " + unit)
      // if the body is already filled in, don't overwrite it
      // otherwise compileLate is going to overwrite bodies of synthetic source files
      if (unit.body == EmptyTree)
        unit.body = initialUnitBody(unit)

      if (settings.Yrangepos && !reporter.hasErrors)
        validatePositions(unit.body)

      if (settings.Ymemberpos.isSetByUser)
        new MemberPosReporter(unit) show (style = settings.Ymemberpos.value)
    }
  }
}
