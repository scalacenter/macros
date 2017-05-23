package scala.macros.internal
package plugins.scalac
package parser

import scala.reflect.internal.Flags // no wildcard import because of ambiguity with Tokens._
import scala.tools.nsc.ast.parser.{SyntaxAnalyzer => NscSyntaxAnalyzer, BracePatch}
import scala.tools.nsc.ast.parser.Tokens._
import scala.tools.nsc.Phase
import scala.macros.coreVersion
import scala.macros.internal.config.engineVersion
import reflect.ReflectToolkit

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

    private val INLINEkw = TermName("inline")
    private def isInlineDef: Boolean = {
      val isInline = in.token == IDENTIFIER && in.name == INLINEkw
      isInline && skippingModifiers(in.token == DEF)
    }
    private def skippingModifiers[T](op: => T): T = {
      lookingAhead(if (isModifier) lookingAhead(skippingModifiers(op)) else op)
    }
    override def isExprIntroToken(token: Token) = !isInlineDef && super.isExprIntroToken(token)
    override def isDclIntro: Boolean = isInlineDef || super.isDclIntro

    private def invoke(name: String, args: Any*): Any = {
      val meth = classOf[Parser].getDeclaredMethods().find(_.getName == name).get
      meth.setAccessible(true)
      meth.invoke(this, args.asInstanceOf[Seq[AnyRef]]: _*)
    }
    private def normalizeModifiers(mods: Modifiers): Modifiers = {
      invoke("normalizeModifiers", mods).asInstanceOf[Modifiers]
    }
    private def addMod(mods: Modifiers, mod: Long, pos: Position): Modifiers = {
      invoke("addMod", mods, mod, pos).asInstanceOf[Modifiers]
    }
    private def tokenRange(token: TokenData): Position = {
      invoke("tokenRange", token).asInstanceOf[Position]
    }
    private def flagTokens: Map[Int, Long] = {
      invoke("flagTokens").asInstanceOf[Map[Int, Long]]
    }
    private def ensureScalaMacrosOnClasspath(): Unit = {
      if (!isScalaMacrosOnClasspath) {
        val version = "\"" + coreVersion.syntax + "\""
        val dependency = s"a library dependency on org.scalamacros %% scalamacros % $version"
        syntaxError(in.offset, s"new-style macros require $dependency")
      }
    }
    override def modifiers(): Modifiers = normalizeModifiers {
      def loop(mods: Modifiers): Modifiers = in.token match {
        case IDENTIFIER if isInlineDef =>
          ensureScalaMacrosOnClasspath()
          val offset = in.offset
          in.nextToken()
          loop(mods.markInline(r2p(offset)))
        case PRIVATE | PROTECTED =>
          loop(accessQualifierOpt(addMod(mods, flagTokens(in.token), tokenRange(in))))
        case ABSTRACT | FINAL | SEALED | OVERRIDE | IMPLICIT | LAZY =>
          loop(addMod(mods, flagTokens(in.token), tokenRange(in)))
        case NEWLINE =>
          in.nextToken()
          loop(mods)
        case _ =>
          mods
      }
      loop(NoMods)
    }
    override def localModifiers(): Modifiers = {
      def loop(mods: Modifiers): Modifiers = {
        if (isInlineDef) {
          ensureScalaMacrosOnClasspath()
          val offset = in.offset
          in.nextToken()
          loop(mods.markInline(r2p(offset)))
        } else if (isLocalModifier) {
          loop(addMod(mods, flagTokens(in.token), tokenRange(in)))
        } else {
          mods
        }
      }
      loop(NoMods)
    }

    // NOTE: This is an initial version of the transformation that implements new-style macros.
    // See Desugared.scala in `tests` for an example of the transformation.
    // TODO: In the future, it may make sense to perform this transformation during typechecking.
    // However that strategy is much more complicated, so it doesn't fit this prototype.

    override def topStatSeq(): List[Tree] = {
      super.topStatSeq().flatMap(stat => desugarNestedInlineDefs(stat, toplevel = true))
    }

    override def templateStats(): List[Tree] = {
      super.templateStats().flatMap(stat => desugarNestedInlineDefs(stat, toplevel = false))
    }

    override def blockStatSeq(): List[Tree] = {
      super.blockStatSeq().flatMap(stat => desugarNestedInlineDefs(stat, toplevel = false))
    }

    private def desugarNestedInlineDefs(owner: Tree, toplevel: Boolean): List[Tree] = {
      owner match {
        case owner: ImplDef =>
          val xstats1 = owner.impl.body.map {
            case stat: DefDef if stat.mods.isInline =>
              if (toplevel) {
                desugarInlineDef(owner, stat)
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
            val helperName = owner.name.inlineModuleName
            val helper1 = atPos(owner.pos)(q"object $helperName { ..${mstatss1.flatten} }")
            List(owner1, helper1)
          } else {
            List(owner)
          }
        case other =>
          List(other)
      }
    }

    private def desugarInlineDef(owner: ImplDef, tree: DefDef): (List[Tree], List[Tree]) = {
      val DefDef(mods, name, tparams, vparamss, tpt, rhs) = tree
      val isMacroAnnotation = {
        val isClass = owner.isInstanceOf[ClassDef]
        val extendsMacroAnnotation = owner.impl.parents.exists {
          case Ident(InlineAnnotationParentName) => true
          case _ => false
        }
        val hasTheRightName = name == InlineAnnotationMethodName
        isClass && extendsMacroAnnotation && hasTheRightName
      }
      val macroDef = atPos(tree.pos) {
        val mods1 = mods | Flags.MACRO
        def rhs1(tparams: List[Tree]) = {
          val result = Select(Ident(owner.name.inlineModuleName), name.inlineShimName)
          atPos(tree.pos)(if (tparams.nonEmpty) TypeApply(result, tparams) else result)
        }
        if (isMacroAnnotation) {
          val tparams1 = {
            if (tparams.nonEmpty) {
              val where = s"on the annotation class instead of the inline method"
              val message = s"new-style macro annotations must have type parameters $where"
              syntaxError(tree.pos.point, message)
              return (List(tree), Nil)
            } else {
              Nil
            }
          }
          val vparamss1 = {
            val param = vparamss.flatten.headOption.getOrElse(tree)
            val tpt = repeatedApplication(scalaDot(TypeName("Any")))
            val param1 = ValDef(Modifiers(Flags.PARAM), TermName("annottees"), tpt, EmptyTree)
            List(List(atPos(param.pos)(param1)))
          }
          DefDef(mods1, TermName("macroTransform"), Nil, vparamss1, tpt, rhs1(Nil))
        } else {
          DefDef(mods1, name.inlineMacroName, tparams, vparamss, tpt, rhs1(tparams))
        }
      }
      val shimDef = atPos(tree.pos) {
        val cname = unit.freshTermName("c$")
        def cExprOf(tpt: Tree): Tree = atPos(tpt.pos) {
          tpt match {
            case AppliedTypeTree(fun @ Select(_, tpnme.REPEATED_PARAM_CLASS_NAME), List(tpt)) =>
              AppliedTypeTree(fun, List(cExprOf(tpt)))
            case _ =>
              AppliedTypeTree(Select(Ident(cname), TypeName("Expr")), List(tpt))
          }
        }
        def cWeakTypeTagOf(tpt: Tree): Tree = atPos(tpt.pos) {
          AppliedTypeTree(Select(Ident(cname), TypeName("WeakTypeTag")), List(tpt))
        }
        val (vparamss2, rhs2) = {
          val c = atPos(tree.pos)(ValDef(Modifiers(Flags.PARAM), cname, ReflectContext, EmptyTree))
          val vvparamss = {
            if (isMacroAnnotation) {
              val param = vparamss.flatten.headOption.getOrElse(tree)
              val tpt = cExprOf(repeatedApplication(scalaDot(TypeName("Any"))))
              val param1 = ValDef(Modifiers(Flags.PARAM), TermName("annottees"), tpt, EmptyTree)
              List(List(atPos(param.pos)(param1)))
            } else {
              vparamss.map(_.map(p => copyValDef(p)(tpt = cExprOf(tpt))))
            }
          }
          val vtparamss = {
            if (tparams.isEmpty) Nil
            else {
              List(tparams.map({
                case tparam @ TypeDef(_, name, _, _) =>
                  val vtparamName = TermName("typetag$" + name)
                  val vtparamTpt = cWeakTypeTagOf(Ident(name))
                  val vtparam =
                    ValDef(Modifiers(Flags.IMPLICIT), vtparamName, vtparamTpt, EmptyTree)
                  atPos(tparam.pos)(vtparam)
              }))
            }
          }
          val vparamss2 = List(List(c)) ++ vvparamss ++ vtparamss
          val rhs2 = shimRhs(c, vvparamss.flatten, vtparamss.flatten, isMacroAnnotation)
          (vparamss2, atPos(rhs.pos)(rhs2))
        }
        val tpt2 = cExprOf(scalaDot(TypeName("Any")))
        DefDef(NoMods, name.inlineShimName, tparams, vparamss2, tpt2, rhs2)
      }
      val implDef = atPos(rhs.pos) {
        val thisParamName = unit.freshTermName("prefix$")
        val vparamss3 = {
          val thisParam = atPos(tree.pos)(ValDef(NoMods, thisParamName, MacrosTerm, EmptyTree))
          val vvparams = vparamss.flatten.map(p => {
            val tpt = if (isMacroAnnotation) MacrosStat else MacrosTerm
            atPos(p.pos)(ValDef(NoMods, p.name, tpt, EmptyTree))
          })
          val vtparams = tparams.map(p => {
            atPos(p.pos)(ValDef(NoMods, p.name.toTermName, MacrosType, EmptyTree))
          })
          val dialectParam = {
            val name = unit.freshTermName("dialect$")
            atPos(tree.pos)(ValDef(Modifiers(Flags.IMPLICIT), name, MacrosDialect, EmptyTree))
          }
          val expansionParam = {
            val name = unit.freshTermName("expansion$")
            atPos(tree.pos)(ValDef(Modifiers(Flags.IMPLICIT), name, MacrosExpansion, EmptyTree))
          }
          List(List(thisParam) ++ vvparams ++ vtparams, List(dialectParam, expansionParam))
        }
        val tpt3 = if (isMacroAnnotation) MacrosStat else MacrosTerm
        val rhs3 = rhs match {
          case Apply(Ident(TermName("meta")), List(block)) =>
            object transformer extends Transformer {
              override def transform(tree: Tree): Tree = tree match {
                case This(tpnme.EMPTY) => atPos(tree.pos)(Ident(thisParamName))
                case tree => super.transform(tree)
              }
            }
            transformer.transform(block)
          case _ =>
            val restr = "new-style macros must have bodies consisting of a single meta block"
            syntaxError(rhs.pos.point, s"implementation restriction: $restr")
            rhs
        }
        DefDef(NoMods, name.inlineImplName, Nil, vparamss3, tpt3, rhs3)
      }
      val stats = List(atPos(tree.pos)(ImportScalaLanguageExperimentalMacros), macroDef)
      val mstats = List(shimDef, implDef)
      (stats, mstats)
    }

    private def shimRhs(c: ValDef,
                        vvparams: List[ValDef],
                        vtparams: List[ValDef],
                        isMacroAnnotation: Boolean): Tree = {
      val cName = c.name
      val thisArgName = unit.freshTermName("prefix$")
      val otherArgDefs = {
        if (isMacroAnnotation) {
          val List(defnParam) = vvparams
          val defnArgName = unit.freshTermName(defnParam.name.toString + "$")
          val defnArgDef = q"""
            val $defnArgName = annottees.map(_.asInstanceOf[_root_.scala.macros.Stat]) match {
              case _root_.scala.Seq(tree) => tree
              case trees => _root_.scala.macros.Term.Block(trees.toList)
            }
          """
          List(defnArgDef)
        } else {
          val vargDefs = vvparams.map(vvparam => {
            val argName = unit.freshTermName(vvparam.name.toString + "$")
            q"val $argName = ${vvparam.name}.asInstanceOf[_root_.scala.macros.Term]"
          })
          val targDefs = vtparams.map(vtparam => {
            val argName = unit.freshTermName(vtparam.name.toString.stripSuffix("typetag$") + "$")
            q"val $argName = ${vtparam.name}.tpe.asInstanceOf[_root_.scala.macros.Type]"
          })
          vargDefs ++ targDefs
        }
      }
      val otherArgNames = otherArgDefs.map(_.name)
      val dialectArgName = unit.freshTermName("dialect$")
      val expansionArgName = unit.freshTermName("expansion$")
      q"""
        var availableEngine = "scalac " + _root_.scala.util.Properties.versionNumberString
        def failMacroEngine(): _root_.scala.Nothing = {
          val requiredEngine = ${engineVersion.toString}
          var msg = "macro cannot be expanded, because it was compiled by an incompatible engine"
          msg += (_root_.scala.meta.prettyprinters.EOL + " available: " + availableEngine)
          msg += (_root_.scala.meta.prettyprinters.EOL + " required : " + requiredEngine)
          $cName.abort($cName.enclosingPosition, msg)
        }
        def invokeBackendMethod(
            moduleName: _root_.java.lang.String,
            methodName: _root_.java.lang.String,
            args: _root_.scala.AnyRef*): _root_.scala.AnyRef = {
          try {
            val pluginClassLoader = this.getClass.getClassLoader
            val moduleClass = _root_.java.lang.Class.forName(moduleName, true, pluginClassLoader)
            val moduleField = moduleClass.getDeclaredField("MODULE$$")
            val module = moduleField.get(null)
            val methods = module.getClass.getDeclaredMethods.filter(_.getName == methodName).toList
            methods match {
              case List(method) => method.invoke(module, args: _*)
              case _ => failMacroEngine()
            }
          } catch {
            case _: _root_.java.lang.ClassNotFoundException => failMacroEngine()
            case _: _root_.java.lang.NoSuchFieldException => failMacroEngine()
            case _: _root_.java.lang.IllegalAccessException => failMacroEngine()
            case _: _root_.java.lang.IllegalArgumentException => failMacroEngine()
          }
        }
        invokeBackendMethod("scala.macros.internal.config.package", "engineVersion") match {
          case version: _root_.scala.macros.Version => availableEngine = version.toString
          case _ => failMacroEngine()
        }

        val ScalacUniverse = "scala.macros.internal.engines.scalac.Universe"
        val scalacUniverse = invokeBackendMethod(ScalacUniverse, "apply", $cName.universe)
        _root_.scala.macros.internal.withUniverse(scalacUniverse) {
          val $thisArgName = $cName.macroApplication.asInstanceOf[_root_.scala.macros.Term]
          ..$otherArgDefs
          val $dialectArgName = _root_.scala.macros.Dialect.current
          val ScalacExpansion = "scala.macros.backends.scalac.Expansion"
          val $expansionArgName = invokeBackendMethod(ScalacExpansion, "apply", $cName) match {
            // TODO: We can't say `expansion: _root_.scala.macros.Expansion`,
            // because it produces a pattern matcher warning in 2.12.x.
            case expansion if expansion.isInstanceOf[_root_.scala.macros.Expansion] =>
              expansion.asInstanceOf[_root_.scala.macros.Expansion]
            case _ =>
              failMacroEngine()
          }
          val result = apply($thisArgName, ..$otherArgNames)($dialectArgName, $expansionArgName)
          $cName.Expr[_root_.scala.Any](result.asInstanceOf[$cName.Tree])
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
