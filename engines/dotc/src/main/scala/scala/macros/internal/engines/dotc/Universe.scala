// scalafmt: { maxColumn = 120 }
package scala.macros.internal.engines.dotc

import scala.language.implicitConversions

import scala.macros.inputs.Position
import scala.macros.internal.prettyprinters.Prettyprinter
import dotty.tools.dotc.core.Decorators.PreNamedString
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.ast.untpd
import dotty.tools.dotc.core.Constants.Constant
import dotty.tools.dotc.core.Denotations
import dotty.tools.dotc.core.Symbols
import dotty.tools.dotc.core.Types
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Flags
import dotty.tools.dotc.util.Positions
import dotty.tools.dotc.core.StdNames._

case class Universe(prefix: untpd.Tree) extends macros.Universe {
  type Mirror = Context

  val XtensionDenotationsDenotation = null
  val XtensionSemanticMemberType = null
  val XtensionSymbolsSymbol = null
  object d {
    type Denotation = Denotations.Denotation
    type Symbol = Symbols.Symbol
  }
  type Tree = untpd.Tree
  type Name = untpd.Tree
  type Template = untpd.Template
  type Init = untpd.Tree
  type Ref = untpd.Tree
  type Term = untpd.Tree
  override val Term: TermCompanion.type = TermCompanion
  object TermCompanion extends TermCompanion {
    type Ref = untpd.Tree
    type Name = untpd.Tree
    type Param = untpd.ValDef
  }
  override val Pat: PatCompanion.type = PatCompanion
  object PatCompanion extends PatCompanion {
    type Var = Term.Name
  }
  type Type = untpd.Tree
  override val Type: TypeCompanion.type = TypeCompanion
  object TypeCompanion extends TypeCompanion {
    type Ref = untpd.Tree
    type Name = untpd.Tree
    type Param = untpd.TypeDef
  }
  type Member = untpd.Tree
  override val Member: MemberCompanion.type = MemberCompanion
  object MemberCompanion extends MemberCompanion {
    type Term = untpd.Tree
    type Type = untpd.TypeDef
  }
  type Defn = untpd.Tree
  override val Defn: DefnCompanion.type = DefnCompanion
  object DefnCompanion extends DefnCompanion {
    type Def = untpd.DefDef
    type Val = untpd.ValDef
  }
  type Pat = untpd.Tree
  type Stat = untpd.Tree
  type Lit = untpd.Tree
  type Denotation = d.Denotation
  type Symbol = Symbols.Symbol

  implicit class XtensionTreeWithPosition(tree: Tree) {
    def autoPos[T <: Tree] = tree.withPos(prefix.pos).asInstanceOf[T]
  }

  type Abstracts = TreeAbstracts with MirrorAbstracts with ExpansionAbstracts
  object abstracts extends TreeAbstracts with MirrorAbstracts with ExpansionAbstracts {
    import treeCompanions._
    override def treePos(tree: Tree): Position = ???
    override def nameValue(name: Name): String = {
      name.asInstanceOf[untpd.Ident].name.toString
    }
    override def nameApply(value: String): Name = untpd.Ident(value.toTermName).autoPos
    override def nameUnapply(tree: Any): Option[String] = ???
    override def litValue(lit: Lit): Any = ???
    override def litUnapply(tree: Any): Option[Any] = ???
    override def memberName(member: Member): Name = ???
    override def NameAnonymous: NameAnonymousCompanion = ???
    override def NameIndeterminate: NameIndeterminateCompanion = ???
    object LitUnit extends LitUnitCompanion {
      override def apply(): Lit = untpd.Literal(Constant(())).autoPos
      override def unapply(tree: Any): Boolean = ???
    }
    object LitBoolean extends LitBooleanCompanion {
      override def apply(value: Boolean): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[Boolean] = ???
    }
    object LitByte extends LitByteCompanion {
      override def apply(value: Byte): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[Byte] = ???
    }
    object LitShort extends LitShortCompanion {
      override def apply(value: Short): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[Short] = ???
    }
    object LitChar extends LitCharCompanion {
      override def apply(value: Char): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[Char] = ???
    }
    object LitInt extends LitIntCompanion {
      override def apply(value: Int): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[Int] = ???
    }
    object LitLong extends LitLongCompanion {
      override def apply(value: Long): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[Long] = ???
    }
    object LitFloat extends LitFloatCompanion {
      override def apply(value: Float): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[Float] = ???
    }
    object LitDouble extends LitDoubleCompanion {
      override def apply(value: Double): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[Double] = ???
    }
    object LitString extends LitStringCompanion {
      override def apply(value: String): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[String] = ???
    }
    object LitSymbol extends LitSymbolCompanion {
      override def apply(value: scala.Symbol): Lit = untpd.Literal(Constant(value)).autoPos
      override def unapply(tree: Any): Option[scala.Symbol] = ???
    }
    object LitNull extends LitNullCompanion {
      override def apply(): Lit = untpd.Literal(Constant(null)).autoPos
      override def unapply(tree: Any): Boolean = ???
    }
    override def TermThis: TermThisCompanion = ???
    override def TermSuper: TermSuperCompanion = ???
    object TermName extends TermNameCompanion {
      override def apply(value: String): Term.Name = untpd.Ident(value.toTermName).autoPos
      override def apply(sym: d.Symbol)(implicit m: Mirror): Term.Name =
        tpd.ref(sym).asInstanceOf[Term.Name].autoPos
      override def unapply(tree: Any): Option[String] = tree match {
        case untpd.Ident(name) => Some(name.toString)
        case _ => None
      }
    }
    object TermSelect extends TermSelectCompanion {
      def apply(qual: Term, name: Term.Name): Term.Ref = name match {
        case untpd.Select(_, name) =>
          untpd.Select(qual, name)
        case ident: untpd.Ident =>
          untpd.Select(qual, ident.name).autoPos
      }
      override def unapply(tree: Any): Option[(Term.Ref, Term.Name)] = tree match {
        case untpd.Select(t, name) if name.isTermName =>
          Some((t, untpd.Ident(name)))
        case _ => None
      }
    }
    override def TermInterpolate: TermInterpolateCompanion = ???
    override def TermXml: TermXmlCompanion = ???
    object TermApply extends TermApplyCompanion {
      override def apply(fun: Tree, args: List[Tree]): Tree = untpd.Apply(fun, args).autoPos
      override def unapply(tree: Any): Option[(untpd.Tree, List[untpd.Tree])] = tree match {
        case untpd.Apply(fun, args) => Some((fun, args))
        case _ => None
      }
    }
    object TermApplyType extends TermApplyTypeCompanion {
      override def apply(fun: Tree, targs: List[Type]): Tree = untpd.TypeApply(fun, targs).autoPos
      override def unapply(tree: Any): Option[(untpd.Tree, List[tpd.Tree])] = ???
    }
    object TermApplyInfix extends TermApplyInfixCompanion {
      def apply(lhs: Term, op: Term.Name, targs: List[Type], args: List[Term]): Term = args match {
        case arg :: Nil =>
          untpd.InfixOp(lhs, op.asInstanceOf[untpd.Ident], arg)
        case _ =>
          untpd.Apply(untpd.Select(lhs, op.asInstanceOf[untpd.Ident].name.asTermName), args)
      }
      def unapply(tree: Any): Option[(Term, Term.Name, List[Type], List[Term])] = ???
    }
    override def TermApplyUnary: TermApplyUnaryCompanion = ???
    override def TermAssign: TermAssignCompanion = ???
    override def TermReturn: TermReturnCompanion = ???
    override def TermThrow: TermThrowCompanion = ???
    override def TermAscribe: TermAscribeCompanion = ???
    override def TermAnnotate: TermAnnotateCompanion = ???
    override def TermTuple: TermTupleCompanion = ???
    object TermBlock extends TermBlockCompanion {
      def apply(stats: List[Stat]): Term =
        stats match {
          case Nil => untpd.Block(stats, untpd.EmptyTree)
          case _ => untpd.Block(stats.init, stats.last)
        }
      def unapply(tree: Any): Option[List[Stat]] = ???
    }
    override def TermIf: TermIfCompanion = ???
    override def TermMatch: TermMatchCompanion = ???
    override def TermTry: TermTryCompanion = ???
    override def TermTryWithHandler: TermTryWithHandlerCompanion = ???
    override def TermFunction: TermFunctionCompanion = ???
    override def TermPartialFunction: TermPartialFunctionCompanion = ???
    override def TermWhile: TermWhileCompanion = ???
    override def TermDo: TermDoCompanion = ???
    override def TermFor: TermForCompanion = ???
    override def TermForYield: TermForYieldCompanion = ???
    object TermNew extends TermNewCompanion {
      def apply(init: Init): Term = init match {
        case ApplySeq(fun, argss) =>
          argss.foldLeft(untpd.Select(untpd.New(fun), nme.CONSTRUCTOR): untpd.Tree)(untpd.Apply)
      }
      def unapply(tree: Any): Option[Init] = ???
    }
    override def TermNewAnonymous: TermNewAnonymousCompanion = ???
    override def TermPlaceholder: TermPlaceholderCompanion = ???
    override def TermEta: TermEtaCompanion = ???
    override def TermRepeated: TermRepeatedCompanion = ???
    object TermParam extends TermParamCompanion {
      def apply(
          mods: List[Mod],
          name: Name,
          decltpe: Option[Type],
          default: Option[Term]
      ): Term.Param = {
        untpd
          .ValDef(
            name.asInstanceOf[untpd.Ident].name.asTermName,
            decltpe.getOrElse(untpd.TypeTree()),
            default.getOrElse(untpd.EmptyTree)
          )
          .withFlags(
            Flags.TermParam
          )
          .autoPos
      }
      def unapply(tree: Any): Option[(List[Mod], Name, Option[Type], Option[Term])] = ???
    }
    object TypeName extends TypeNameCompanion {
      def apply(value: String): Type.Name = untpd.Ident(value.toTypeName).autoPos
      def apply(sym: Symbol)(implicit m: Mirror): Type.Name = tpd.ref(sym).asInstanceOf[Type.Name].autoPos
      def unapply(tree: Any): Option[String] = ???
    }
    object TypeSelect extends TypeSelectCompanion {
      def apply(qual: Term.Ref, name: Type.Name): Type.Ref =
        untpd.Select(qual, name.asInstanceOf[untpd.Ident].name.asTypeName)
      def unapply(tree: Any): Option[(Term.Ref, Type.Name)] = ???

    }
    override def TypeProject: TypeProjectCompanion = ???
    override def TypeSingleton: TypeSingletonCompanion = ???
    object TypeApply extends TypeApplyCompanion {
      def apply(tpe: Type, args: List[Type]): Type = untpd.AppliedTypeTree(tpe, args).autoPos
      def unapply(tree: Any): Option[(Type, List[Type])] = ???
    }
    override def TypeApplyInfix: TypeApplyInfixCompanion = ???
    override def TypeFunction: TypeFunctionCompanion = ???
    override def TypeTuple: TypeTupleCompanion = ???
    override def TypeWith: TypeWithCompanion = ???
    override def TypeAnd: TypeAndCompanion = ???
    override def TypeOr: TypeOrCompanion = ???
    override def TypeRefine: TypeRefineCompanion = ???
    override def TypeExistential: TypeExistentialCompanion = ???
    override def TypeAnnotate: TypeAnnotateCompanion = ???
    override def TypePlaceholder: TypePlaceholderCompanion = ???
    override def TypeBounds: TypeBoundsCompanion = ???
    override def TypeByName: TypeByNameCompanion = ???
    override def TypeRepeated: TypeRepeatedCompanion = ???
    override def TypeVar: TypeVarCompanion = ???
    override def TypeMethod: TypeMethodCompanion = ???
    override def TypeLambda: TypeLambdaCompanion = ???
    override def TypeParam: TypeParamCompanion = ???
    object PatVar extends PatVarCompanion {
      def apply(name: Term.Name): Pat.Var = name
      def unapply(tree: Any): Option[Term.Name] = tree match {
        case ident @ untpd.Ident(_) => Some(ident)
        case _ => None
      }
    }
    override def PatWildcard: PatWildcardCompanion = ???
    override def PatSeqWildcard: PatSeqWildcardCompanion = ???
    override def PatBind: PatBindCompanion = ???
    override def PatAlternative: PatAlternativeCompanion = ???
    override def PatTuple: PatTupleCompanion = ???
    override def PatExtract: PatExtractCompanion = ???
    override def PatExtractInfix: PatExtractInfixCompanion = ???
    override def PatInterpolate: PatInterpolateCompanion = ???
    override def PatXml: PatXmlCompanion = ???
    override def PatTyped: PatTypedCompanion = ???
    override def DeclVal: DeclValCompanion = ???
    override def DeclVar: DeclVarCompanion = ???
    override def DeclDef: DeclDefCompanion = ???
    override def DeclType: DeclTypeCompanion = ???
    object DefnVal extends DefnValCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Option[Type], rhs: Term): Defn.Val = {
        val name = pats match {
          case Pat.Var(name) :: Nil =>
            name.asInstanceOf[untpd.Ident].name.asTermName
          case els => sys.error(els.toString())
        }

        untpd
          .ValDef(
            name,
            decltpe.getOrElse(untpd.TypeTree()),
            rhs
          )
          .autoPos
      }
      def unapply(tree: Any): Option[(List[Mod], List[Pat], Option[Type], Term)] = ???

    }
    override def DefnVar: DefnVarCompanion = ???
    object DefnDef extends DefnDefCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Option[Type],
          body: Term
      ): Defn.Def = {
        untpd.DefDef(
          name.asInstanceOf[untpd.Ident].name.asTermName,
          tparams,
          paramss,
          decltpe.getOrElse(untpd.TypeTree()),
          body
        )
      }

      def unapply(tree: Any): Option[
        (List[Mod], Term.Name, List[Type.Param], List[List[Term.Param]], Option[Type], Term)
      ] = ???

    }
    override def DefnMacro: DefnMacroCompanion = ???
    override def DefnType: DefnTypeCompanion = ???
    override def DefnClass: DefnClassCompanion = ???
    override def DefnTrait: DefnTraitCompanion = ???
    object DefnObject extends DefnObjectCompanion {
      def apply(mods: List[Mod], name: Term.Name, templ: Template): Defn.Object = {
        untpd.ModuleDef(name.asInstanceOf[untpd.Ident].name.asTermName, templ).autoPos
      }
    }
    override def PkgProper: PkgProperCompanion = ???
    override def PkgObject: PkgObjectCompanion = ???
    override def CtorPrimary: CtorPrimaryCompanion = ???
    override def CtorSecondary: CtorSecondaryCompanion = ???

    object ApplySeq {
      def unapply(call: Tree): Option[(Tree, List[List[Tree]])] = {
        def recur(
            acc: List[List[Tree]],
            term: untpd.Tree
        ): (untpd.Tree, List[List[Tree]]) =
          term match {
            case Term.Apply(fun, args) =>
              recur(args +: acc, fun) // inner-most is in the front
            case fun => (fun, acc)
          }

        Some(recur(Nil, call))
      }
    }
    object Init extends InitCompanion {
      def apply(tpe: Type, name: Name, argss: List[List[Term]]): Init = {
        argss.foldLeft(tpe)(untpd.Apply)
      }
      def unapply(arg: Any): Option[(Type, Name, List[List[Term]])] = arg match {
        case ApplySeq(fun, argss) =>
          Some((fun, Name(""), argss))
        case _ => None
      }
    }
    object Self extends SelfCompanion {
      def apply(name: Name, decltpe: Option[Type]): Self = {
        untpd
          .ValDef(name.asInstanceOf[untpd.Ident].name.asTermName, decltpe.getOrElse(untpd.TypeTree()), untpd.EmptyTree)
          .autoPos
      }
    }
    object Template extends TemplateCompanion {
      def apply(early: List[Stat], inits: List[Init], self: Self, stats: List[Stat]): Template = {
        val constr = untpd.DefDef(nme.CONSTRUCTOR, Nil, Nil, untpd.TypeTree(), untpd.EmptyTree)
        untpd.Template(constr, inits, untpd.EmptyValDef, stats)
      }
    }
    override def ModAnnot: ModAnnotCompanion = ???
    override def ModPrivate: ModPrivateCompanion = ???
    override def ModProtected: ModProtectedCompanion = ???
    override def ModImplicit: ModImplicitCompanion = ???
    override def ModFinal: ModFinalCompanion = ???
    override def ModSealed: ModSealedCompanion = ???
    override def ModOverride: ModOverrideCompanion = ???
    override def ModCase: ModCaseCompanion = ???
    override def ModAbstract: ModAbstractCompanion = ???
    override def ModCovariant: ModCovariantCompanion = ???
    override def ModContravariant: ModContravariantCompanion = ???
    override def ModLazy: ModLazyCompanion = ???
    override def ModValParam: ModValParamCompanion = ???
    override def ModVarParam: ModVarParamCompanion = ???
    override def ModMacro: ModMacroCompanion = ???
    override def EnumeratorGenerator: EnumeratorGeneratorCompanion = ???
    override def EnumeratorVal: EnumeratorValCompanion = ???
    override def EnumeratorGuard: EnumeratorGuardCompanion = ???
    override def Import: ImportCompanion = ???
    override def Importer: ImporterCompanion = ???
    override def ImporteeWildcard: ImporteeWildcardCompanion = ???
    override def ImporteeName: ImporteeNameCompanion = ???
    override def ImporteeRename: ImporteeRenameCompanion = ???
    override def ImporteeUnimport: ImporteeUnimportCompanion = ???
    override def Case: CaseCompanion = ???
    override def Source: SourceCompanion = ???
    override def refDenot(ref: Ref)(implicit m: Mirror): Denotation = ???
    override def termTpe(term: Term)(implicit m: Mirror): Type = ???
    override def sym(id: String)(implicit m: Mirror): Symbol = ???
    override def symSyntax(p: Prettyprinter, sym: Symbol)(implicit m: Mirror): Unit = ???
    override def symStructure(p: Prettyprinter, sym: Symbol)(implicit m: Mirror): Unit = ???
    override def symName(sym: Symbol)(implicit m: Mirror): Name = {
      val x = untpd.Ident(sym.name)
      println(x.toString)
      x
    }
    override def symFlags(sym: Symbol)(implicit m: Mirror): Long = {
      var flags = 0L
      if (sym.is(Flags.Case)) flags |= CASE
      flags
    }
    override def symAnnots(sym: Symbol)(implicit m: Mirror): List[Init] = ???
    override def symWithin(sym: Symbol)(implicit m: Mirror): Symbol = ???
    override def symDenot(sym: Symbol)(implicit m: Mirror): Denotation = ???
    override def symDenot(sym: Symbol, pre: Type)(implicit m: Mirror): Denotation = ???
    override def symMembers(sym: Symbol, f: SymFilter)(implicit m: Mirror): List[Symbol] = ???
    override def symMembers(sym: Symbol, name: String, f: SymFilter)(implicit m: Mirror): List[Symbol] = ???
    override def denotSyntax(p: Prettyprinter, denot: Denotation)(implicit m: Mirror): Unit = ???
    override def denotStructure(p: Prettyprinter, denot: Denotation)(implicit m: Mirror): Unit = ???
    override def denotSym(denot: Denotation)(implicit m: Mirror): Symbol =
      denot.symbol
    override def denotInfo(denot: Denotation)(implicit m: Mirror): Type =
      untpd.TypeTree(denot.info(m))
    override def denotMembers(denot: Denotation, f: SymFilter)(implicit m: Mirror): List[Denotation] = ???
    override def denotMembers(denot: Denotation, name: String, f: SymFilter)(implicit m: Mirror): List[Denotation] =
      ???
    override def typeEqual(tpe1: Type, tpe2: Type)(implicit m: Mirror): Boolean = ???
    override def typeSubtype(tpe1: Type, tpe2: Type)(implicit m: Mirror): Boolean = ???
    override def typeWiden(tpe: Type)(implicit m: Mirror): Type = ???
    override def typeNarrow(tpe: Type)(implicit m: Mirror): Type = ???
    override def caseFields(tpe: Type)(implicit m: Mirror): List[Denotation] = {
      val tp = tpe.asInstanceOf[tpd.Tree].tpe
      tp.memberDenots(
          Types.fieldFilter,
          (name, buf) => {
            buf ++= tp.member(name).altsWith(_ is Flags.ParamAccessor)
          }
        )
        .toList
    }
    override def typeMembers(untpdTree: Type, f: SymFilter)(implicit m: Mirror): List[Denotation] = {
      val buf = List.newBuilder[Denotation]
      val tpe = untpdTree.asInstanceOf[tpd.Tree]
      tpe.tpe.memberDenots(
        Types.takeAllFilter,
        (name, _) => {
          val member = tpe.tpe.member(name)
          if (f(member.symbol)) {
            buf += member
          }
        }
      )
      buf.result()
    }
    override def typeMembers(tpe: Type, name: String, f: SymFilter)(implicit m: Mirror): List[Denotation] = ???
    override def typeLub(tpes: List[Type])(implicit m: Mirror): Type = ???
    override def typeGlb(tpes: List[Type])(implicit m: Mirror): Type = ???
    override def expandee(implicit e: Expansion): Term = ???
    override def abort(pos: _root_.scala.macros.Position, msg: String)(implicit e: Expansion): Nothing = ???
    override def error(pos: _root_.scala.macros.Position, msg: String)(implicit e: Expansion): Unit = ???
    override def warning(pos: _root_.scala.macros.Position, msg: String)(implicit e: Expansion): Unit = ???
  }
}
