package scala.macros.internal
package engines.scalac
package trees

import scala.reflect.internal.{Flags => gf}
import scala.reflect.internal.util.Collections._
import scala.macros.inputs._
import scala.macros.internal.engines.scalac.inputs._

trait Abstracts extends scala.macros.trees.Abstracts with Positions { self: Universe =>
  import treeCompanions._

  trait TreeAbstracts extends super.TreeAbstracts {
    def treePos(tree: Tree): Position = tree.pos

    def nameValue(name: Name): String = name.value

    def nameUnapply(gtree: Any): Option[String] = gtree match {
      case tree: c.Name => Some(tree.value)
      case _ => None
    }

    def litValue(lit: Lit): Any = litUnapply(lit).get

    def litUnapply(gtree: Any): Option[Any] = gtree match {
      case tree: g.Literal => Some(tree.value.value)
      case _ => None
    }

    def memberName(member: Member): Name = member.name match {
      case _: g.TermName => member.toTermName
      case _: g.TypeName => member.toTypeName
    }

    // NOTE: The methods below are supposed to take care of statement-level desugaring/resugaring.
    // For more information, see this code from the early days of scalahost:
    // https://github.com/xeno-by/scalahost/blob/4ca12dfa3f204b91efe2eba3e5991dbb6ea1879d/interface/src/main/scala/scala/meta/internal/hosts/scalac/converters/ToMtree.scala#L131-L284.
    // For even more information, check out this code from even earlier days:
    // TreeInfo.untypecheckedTemplBody (from the Scala compiler).

    implicit class XtensionStats(stats: List[g.Tree]) {
      def toGStats: List[g.Tree] = {
        // TODO: implement me
        stats
      }
    }

    implicit class XtensionGStats(gstats: List[g.Tree]) {
      def toStats: List[g.Tree] = {
        // TODO: implement me
        gstats
      }
    }

    object NameAnonymous extends NameAnonymousCompanion {
      def apply(): Term.Name with Type.Name = c.NameAnonymous()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.NameAnonymous => true
        case _ => false
      }
    }

    object NameIndeterminate extends NameIndeterminateCompanion {
      def apply(value: String): Name = c.NameIndeterminate(value)
      def unapply(gtree: Any): Option[String] = gtree match {
        case tree: c.NameIndeterminate => c.NameIndeterminate.unapply(tree)
        case _ => None
      }
    }

    object LitUnit extends LitUnitCompanion {
      def apply(): Lit = g.Literal(g.Constant(()))
      def unapply(gtree: Any): Boolean = ???
    }

    object LitBoolean extends LitBooleanCompanion {
      def apply(value: Boolean): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[Boolean] = ???
    }

    object LitByte extends LitByteCompanion {
      def apply(value: Byte): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[Byte] = ???
    }

    object LitShort extends LitShortCompanion {
      def apply(value: Short): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[Short] = ???
    }

    object LitChar extends LitCharCompanion {
      def apply(value: Char): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[Char] = ???
    }

    object LitInt extends LitIntCompanion {
      def apply(value: Int): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[Int] = ???
    }

    object LitFloat extends LitFloatCompanion {
      def apply(value: Float): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[Float] = ???
    }

    object LitLong extends LitLongCompanion {
      def apply(value: Long): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[Long] = ???
    }

    object LitDouble extends LitDoubleCompanion {
      def apply(value: Double): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[Double] = ???
    }

    object LitString extends LitStringCompanion {
      def apply(value: String): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[String] = ???
    }

    object LitSymbol extends LitSymbolCompanion {
      def apply(value: scala.Symbol): Lit = g.Literal(g.Constant(value))
      def unapply(gtree: Any): Option[scala.Symbol] = ???
    }

    object LitNull extends LitNullCompanion {
      def apply(): Lit = g.Literal(g.Constant(null))
      def unapply(gtree: Any): Boolean = ???
    }

    object TermThis extends TermThisCompanion {
      def apply(qual: Name): Term.Ref = ???
      def unapply(gtree: Any): Option[Name] = ???
    }

    object TermSuper extends TermSuperCompanion {
      def apply(thisp: Name, superp: Name): Term.Ref = ???
      def unapply(gtree: Any): Option[(Name, Name)] = ???
    }

    object TermName extends TermNameCompanion {
      def apply(value: String): Term.Name = {
        new c.TermNameImpl(value)
      }
      def apply(sym: Symbol): Term.Name = {
        apply(sym.name.decoded).setSymbol(sym)
      }
      def unapply(gtree: Any): Option[String] = gtree match {
        case g.Ident(name: g.TermName) => Some(name.decoded)
        case _ => None
      }
    }

    implicit class XtensionTermName(tree: Term.Name) {
      def toGTermName: g.TermName = tree.name.toTermName
    }

    implicit class XtensionGTermName(gtree: g.SymTree with g.NameTree) {
      def toTermName: Term.Name = TermName(gtree.name.decoded).copyAttrs(gtree)
    }

    object TermSelect extends TermSelectCompanion {
      def apply(qual: Term, name: Term.Name): Term.Ref = g.Select(qual, name.toGTermName)
      def unapply(gtree: Any): Option[(Term, Term.Name)] = ???
    }

    object TermInterpolate extends TermInterpolateCompanion {
      def apply(prefix: Term.Name, parts: List[Lit], args: List[Term]): Term = ???
      def unapply(gtree: Any): Option[(Term.Name, List[Lit], List[Term])] = ???
    }

    object TermXml extends TermXmlCompanion {
      def apply(parts: List[Lit], args: List[Term]): Term = ???
      def unapply(gtree: Any): Option[(List[Lit], List[Term])] = ???
    }

    object TermApply extends TermApplyCompanion {
      def apply(fun: Term, args: List[Term]): Term = g.Apply(fun, args)
      def unapply(gtree: Any): Option[(Term, List[Term])] = ???
    }

    object TermApplyType extends TermApplyTypeCompanion {
      def apply(fun: Term, targs: List[Type]): Term = g.TypeApply(fun, targs)
      def unapply(gtree: Any): Option[(Term, List[Type])] = ???
    }

    object TermApplyInfix extends TermApplyInfixCompanion {
      def apply(lhs: Term, op: Term.Name, targs: List[Type], args: List[Term]): Term = {
        val isLeftAssoc = !op.value.endsWith(":")
        if (isLeftAssoc) {
          var method: g.Tree = g.Select(lhs, op.toGTermName).setPos(op.pos)
          if (targs.nonEmpty) method = g.TypeApply(method, targs).setPos(op.pos)
          g.Apply(method, args)
        } else {
          ???
        }
      }
      def unapply(gtree: Any): Option[(Term, Term.Name, List[Type], List[Term])] = ???
    }

    object TermApplyUnary extends TermApplyUnaryCompanion {
      def apply(op: Name, arg: Term): Term.Ref = ???
      def unapply(gtree: Any): Option[(Name, Term)] = ???
    }

    object TermAssign extends TermAssignCompanion {
      def apply(lhs: Term, rhs: Term): Term = ???
      def unapply(gtree: Any): Option[(Term, Term)] = ???
    }

    object TermReturn extends TermReturnCompanion {
      def apply(expr: Term): Term = ???
      def unapply(gtree: Any): Option[Term] = ???
    }

    object TermThrow extends TermThrowCompanion {
      def apply(expr: Term): Term = ???
      def unapply(gtree: Any): Option[Term] = ???
    }

    object TermAscribe extends TermAscribeCompanion {
      def apply(expr: Term, tpe: Type): Term = ???
      def unapply(gtree: Any): Option[(Term, Type)] = ???
    }

    object TermAnnotate extends TermAnnotateCompanion {
      def apply(expr: Term, annots: List[Mod]): Term = ???
      def unapply(gtree: Any): Option[(Term, List[Mod])] = ???
    }

    object TermTuple extends TermTupleCompanion {
      def apply(args: List[Term]): Term = ???
      def unapply(gtree: Any): Option[List[Term]] = ???
    }

    object TermBlock extends TermBlockCompanion {
      def apply(stats: List[Stat]): Term = g.gen.mkBlock(stats.toGStats)
      def unapply(gtree: Any): Option[List[Stat]] = ???
    }

    object TermIf extends TermIfCompanion {
      def apply(cond: Term, thenp: Term, elsep: Term): Term = ???
      def unapply(gtree: Any): Option[(Term, Term, Term)] = ???
    }

    object TermMatch extends TermMatchCompanion {
      def apply(expr: Term, cases: List[Case]): Term = ???
      def unapply(gtree: Any): Option[(Term, List[Case])] = ???
    }

    object TermTry extends TermTryCompanion {
      def apply(expr: Term, catchp: List[Case], finallyp: Option[Term]): Term = ???
      def unapply(gtree: Any): Option[(Term, List[Case], Option[Term])] = ???
    }

    object TermTryWithHandler extends TermTryWithHandlerCompanion {
      def apply(expr: Term, catchp: Term, finallyp: Option[Term]): Term = ???
      def unapply(gtree: Any): Option[(Term, Term, Option[Term])] = ???
    }

    object TermFunction extends TermFunctionCompanion {
      def apply(params: List[Term.Param], body: Term): Term = ???
      def unapply(gtree: Any): Option[(List[Term.Param], Term)] = ???
    }

    object TermPartialFunction extends TermPartialFunctionCompanion {
      def apply(cases: List[Case]): Term = ???
      def unapply(gtree: Any): Option[List[Case]] = ???
    }

    object TermWhile extends TermWhileCompanion {
      def apply(expr: Term, body: Term): Term = ???
      def unapply(gtree: Any): Option[(Term, Term)] = ???
    }

    object TermDo extends TermDoCompanion {
      def apply(body: Term, expr: Term): Term = ???
      def unapply(gtree: Any): Option[(Term, Term)] = ???
    }

    object TermFor extends TermForCompanion {
      def apply(enums: List[Enumerator], body: Term): Term = ???
      def unapply(gtree: Any): Option[(List[Enumerator], Term)] = ???
    }

    object TermForYield extends TermForYieldCompanion {
      def apply(enums: List[Enumerator], body: Term): Term = ???
      def unapply(gtree: Any): Option[(List[Enumerator], Term)] = ???
    }

    object TermNew extends TermNewCompanion {
      def apply(init: Init): Term = init.toGNew
      def unapply(gtree: Any): Option[Init] = ???
    }

    object TermNewAnonymous extends TermNewAnonymousCompanion {
      def apply(templ: Template): Term = ???
      def unapply(gtree: Any): Option[Template] = ???
    }

    object TermPlaceholder extends TermPlaceholderCompanion {
      def apply(): Term = ???
      def unapply(gtree: Any): Boolean = ???
    }

    object TermEta extends TermEtaCompanion {
      def apply(expr: Term): Term = ???
      def unapply(gtree: Any): Option[Term] = ???
    }

    object TermRepeated extends TermRepeatedCompanion {
      def apply(expr: Term): Term = ???
      def unapply(gtree: Any): Option[Term] = ???
    }

    object TermParam extends TermParamCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          decltpe: Option[Type],
          default: Option[Term]): Term.Param = {
        val gtpt = decltpe.getOrElse(g.TypeTree())
        val gdefault = default.getOrElse(g.EmptyTree)
        g.ValDef(mods.toGModifiers | gf.PARAM, name.toGTermName, gtpt, gdefault)
      }
      def unapply(gtree: Any): Option[(List[Mod], Term.Name, Option[Type], Option[Term])] = ???
    }

    object TypeName extends TypeNameCompanion {
      def apply(value: String): Type.Name = {
        new c.TypeNameImpl(value)
      }
      def apply(sym: Symbol): Type.Name = {
        apply(sym.name.decoded).setSymbol(sym)
      }
      def unapply(gtree: Any): Option[String] = gtree match {
        case g.Ident(name: g.TypeName) => unapply(name.decoded)
        case _ => None
      }
    }

    implicit class XtensionTypeName(tree: Type.Name) {
      def toGTypeName: g.TypeName = tree.name.toTypeName
    }

    implicit class XtensionGTypeName(gtree: g.SymTree with g.NameTree) {
      def toTypeName: Type.Name = TypeName(gtree.name.decoded).copyAttrs(gtree)
    }

    object TypeSelect extends TypeSelectCompanion {
      def apply(qual: Term.Ref, name: Type.Name): Type.Ref = g.Select(qual, name.toGTypeName)
      def unapply(gtree: Any): Option[(Term.Ref, Type.Name)] = ???
    }

    object TypeProject extends TypeProjectCompanion {
      def apply(qual: Type, name: Type.Name): Type.Ref = ???
      def unapply(gtree: Any): Option[(Type, Type.Name)] = ???
    }

    object TypeSingleton extends TypeSingletonCompanion {
      def apply(ref: Term.Ref): Type.Ref = ???
      def unapply(gtree: Any): Option[Term.Ref] = ???
    }

    object TypeApply extends TypeApplyCompanion {
      def apply(tpe: Type, args: List[Type]): Type = g.AppliedTypeTree(tpe, args)
      def unapply(gtree: Any): Option[(Type, List[Type])] = ???
    }

    object TypeApplyInfix extends TypeApplyInfixCompanion {
      def apply(lhs: Type, op: Type.Name, rhs: Type): Type = ???
      def unapply(gtree: Any): Option[(Type, Type.Name, Type)] = ???
    }

    object TypeFunction extends TypeFunctionCompanion {
      def apply(params: List[Type], res: Type): Type = ???
      def unapply(gtree: Any): Option[(List[Type], Type)] = ???
    }

    object TypeTuple extends TypeTupleCompanion {
      def apply(args: List[Type]): Type = ???
      def unapply(gtree: Any): Option[List[Type]] = ???
    }

    object TypeWith extends TypeWithCompanion {
      def apply(lhs: Type, rhs: Type): Type = ???
      def unapply(gtree: Any): Option[(Type, Type)] = ???
    }

    object TypeAnd extends TypeAndCompanion {
      def apply(lhs: Type, rhs: Type): Type = ???
      def unapply(gtree: Any): Option[(Type, Type)] = ???
    }

    object TypeOr extends TypeOrCompanion {
      def apply(lhs: Type, rhs: Type): Type = ???
      def unapply(gtree: Any): Option[(Type, Type)] = ???
    }

    object TypeRefine extends TypeRefineCompanion {
      def apply(tpe: Option[Type], stats: List[Stat]): Type = ???
      def unapply(gtree: Any): Option[(Option[Type], List[Stat])] = ???
    }

    object TypeExistential extends TypeExistentialCompanion {
      def apply(tpe: Type, stats: List[Stat]): Type = ???
      def unapply(gtree: Any): Option[(Type, List[Stat])] = ???
    }

    object TypeAnnotate extends TypeAnnotateCompanion {
      def apply(tpe: Type, annots: List[Mod]): Type = ???
      def unapply(gtree: Any): Option[(Type, List[Mod])] = ???
    }

    object TypePlaceholder extends TypePlaceholderCompanion {
      def apply(bounds: Type.Bounds): Type = ???
      def unapply(gtree: Any): Option[Type.Bounds] = ???
    }

    object TypeBounds extends TypeBoundsCompanion {
      def apply(lo: Option[Type], hi: Option[Type]): Type.Bounds = ???
      def unapply(gtree: Any): Option[(Option[Type], Option[Type])] = ???
    }

    object TypeByName extends TypeByNameCompanion {
      def apply(tpe: Type): Type = ???
      def unapply(gtree: Any): Option[Type] = ???
    }

    object TypeRepeated extends TypeRepeatedCompanion {
      def apply(tpe: Type): Type = ???
      def unapply(gtree: Any): Option[Type] = ???
    }

    object TypeVar extends TypeVarCompanion {
      def apply(name: Type.Name): Type.Var = ???
      def unapply(gtree: Any): Option[Type.Name] = ???
    }

    object TypeMethod extends TypeMethodCompanion {
      def apply(paramss: List[List[Term.Param]], tpe: Type): Type = ???
      def unapply(tree: Any): Option[(List[List[Term.Param]], Type)] = ???
    }

    object TypeLambda extends TypeLambdaCompanion {
      def apply(tparams: List[Type.Param], tpe: Type): Type = ???
      def unapply(tree: Any): Option[(List[Type.Param], Type)] = ???
    }

    object TypeParam extends TypeParamCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          tbounds: Type.Bounds,
          vbounds: List[Type],
          cbounds: List[Type]): Type.Param = ???
      def unapply(gtree: Any)
        : Option[(List[Mod], Type.Name, List[Type.Param], Type.Bounds, List[Type], List[Type])] =
        ???
    }

    object PatVar extends PatVarCompanion {
      def apply(name: Term.Name): Pat.Var = g.Bind(name.toGTermName, g.Ident(g.nme.WILDCARD))
      def unapply(gtree: Any): Option[Term.Name] = gtree match {
        case gtree: g.Bind => Some(gtree.toTermName)
        case _ => None
      }
    }

    object PatWildcard extends PatWildcardCompanion {
      def apply(): Pat = ???
      def unapply(gtree: Any): Boolean = ???
    }

    object PatSeqWildcard extends PatSeqWildcardCompanion {
      def apply(): Pat = ???
      def unapply(gtree: Any): Boolean = ???
    }

    object PatBind extends PatBindCompanion {
      def apply(lhs: Pat, rhs: Pat): Pat = ??? // TODO: auto-promote Term.Name in lhs
      def unapply(gtree: Any): Option[(Pat, Pat)] = ???
    }

    object PatAlternative extends PatAlternativeCompanion {
      def apply(lhs: Pat, rhs: Pat): Pat = ???
      def unapply(gtree: Any): Option[(Pat, Pat)] = ???
    }

    object PatTuple extends PatTupleCompanion {
      def apply(args: List[Pat]): Pat = ???
      def unapply(gtree: Any): Option[List[Pat]] = ???
    }

    object PatExtract extends PatExtractCompanion {
      def apply(fun: Term, args: List[Pat]): Pat = ???
      def unapply(gtree: Any): Option[(Term, List[Pat])] = ???
    }

    object PatExtractInfix extends PatExtractInfixCompanion {
      def apply(lhs: Pat, op: Term.Name, rhs: List[Pat]): Pat = ???
      def unapply(gtree: Any): Option[(Pat, Term.Name, List[Pat])] = ???
    }

    object PatInterpolate extends PatInterpolateCompanion {
      def apply(prefix: Term.Name, parts: List[Lit], args: List[Pat]): Pat = ???
      def unapply(gtree: Any): Option[(Term.Name, List[Lit], List[Pat])] = ???
    }

    object PatXml extends PatXmlCompanion {
      def apply(parts: List[Lit], args: List[Pat]): Pat = ???
      def unapply(gtree: Any): Option[(List[Lit], List[Pat])] = ???
    }

    object PatTyped extends PatTypedCompanion {
      def apply(lhs: Pat, rhs: Type): Pat = ??? // TODO: auto-promote Term.Name in lhs
      def unapply(gtree: Any): Option[(Pat, Type)] = ???
    }

    object DeclVal extends DeclValCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Type): Decl.Val = ???
      def unapply(gtree: Any): Option[(List[Mod], List[Pat], Type)] = ???
    }

    object DeclVar extends DeclVarCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Type): Decl.Var = ???
      def unapply(gtree: Any): Option[(List[Mod], List[Pat], Type)] = ???
    }

    object DeclDef extends DeclDefCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Type): Decl.Def = ???
      def unapply(gtree: Any)
        : Option[(List[Mod], Term.Name, List[Type.Param], List[List[Term.Param]], Type)] = ???
    }

    object DeclType extends DeclTypeCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          bounds: Type.Bounds): Decl.Type = ???
      def unapply(gtree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Type.Bounds)] = ???
    }

    object DefnVal extends DefnValCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Option[Type], rhs: Term): Defn.Val = {
        pats match {
          case List(name @ g.Ident(_: g.TermName)) =>
            apply(mods, List(Pat.Var(name.toTermName).setPos(name.pos)), decltpe, rhs)
          case List(Pat.Var(name)) =>
            g.ValDef(mods.toGModifiers, name.toGTermName, decltpe.getOrElse(g.TypeTree()), rhs)
          case _ =>
            ???
        }
      }
      def unapply(gtree: Any): Option[(List[Mod], List[Pat], Option[Type], Term)] = ???
    }

    object DefnVar extends DefnVarCompanion {
      def apply(
          mods: List[Mod],
          pats: List[Pat],
          decltpe: Option[Type],
          rhs: Option[Term]): Defn.Var = ??? // TODO: auto-promote Term.Name in lhs
      def unapply(gtree: Any): Option[(List[Mod], List[Pat], Option[Type], Option[Term])] = ???
    }

    object DefnDef extends DefnDefCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Option[Type],
          body: Term): Defn.Def = {
        val gparamss = paramss // TODO: view bounds, context bounds
        val gtpt = decltpe.getOrElse(g.TypeTree())
        g.DefDef(mods.toGModifiers, name.toGTermName, tparams, gparamss, gtpt, body)
      }
      def unapply(gtree: Any): Option[
        (List[Mod], Term.Name, List[Type.Param], List[List[Term.Param]], Option[Type], Term)] = ???
    }

    object DefnMacro extends DefnMacroCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Option[Type],
          body: Term): Defn.Macro = ???
      def unapply(gtree: Any): Option[
        (List[Mod], Term.Name, List[Type.Param], List[List[Term.Param]], Option[Type], Term)] = ???
    }

    object DefnType extends DefnTypeCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          body: Type): Defn.Type = ???
      def unapply(gtree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Type)] = ???
    }

    object DefnClass extends DefnClassCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          ctor: Ctor.Primary,
          templ: Template): Defn.Class = ???
      def unapply(
          gtree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Ctor.Primary, Template)] =
        ???
    }

    object DefnTrait extends DefnTraitCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          ctor: Ctor.Primary,
          templ: Template): Defn.Trait = ???
      def unapply(
          gtree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Ctor.Primary, Template)] =
        ???
    }

    object DefnObject extends DefnObjectCompanion {
      def apply(mods: List[Mod], name: Term.Name, templ: Template): Defn.Object = {
        g.ModuleDef(mods.toGModifiers, name.toGTermName, templ.toGTemplate(g.Modifiers(), Nil))
      }
      def unapply(gtree: Any): Option[(List[Mod], Term.Name, Template)] = gtree match {
        case tree @ g.ModuleDef(gmods, _, gtemplate) =>
          Some((gmods.toModifiers, tree.toTermName, gtemplate.toTemplate))
        case _ =>
          None
      }
    }

    object PkgProper extends PkgProperCompanion {
      def apply(ref: Term.Ref, stats: List[Stat]): Pkg = ???
      def unapply(gtree: Any): Option[(Term.Ref, List[Stat])] = ???
    }

    object PkgObject extends PkgObjectCompanion {
      def apply(mods: List[Mod], name: Term.Name, templ: Template): Pkg.Object = ???
      def unapply(gtree: Any): Option[(List[Mod], Term.Name, Template)] = ???
    }

    object CtorPrimary extends CtorPrimaryCompanion {
      def apply(mods: List[Mod], name: Name, paramss: List[List[Term.Param]]): Ctor.Primary = ???
      def unapply(gtree: Any): Option[(List[Mod], Name, List[List[Term.Param]])] = ???
    }

    object CtorSecondary extends CtorSecondaryCompanion {
      def apply(
          mods: List[Mod],
          name: Name,
          paramss: List[List[Term.Param]],
          init: Init,
          stats: List[Stat]): Ctor.Secondary = ???
      def unapply(gtree: Any): Option[(List[Mod], Name, List[List[Term.Param]], Init, List[Stat])] =
        ???
    }

    object Init extends InitCompanion {
      def apply(tpe: Type, name: Name, argss: List[List[Term]]): Init = c.Init(tpe, name, argss)
      def unapply(gtree: Any): Option[(Type, Name, List[List[Term]])] = gtree match {
        case tree: c.Init => c.Init.unapply(tree)
        case _ => None
      }
    }

    implicit class XtensionInit(tree: Init) {
      def toGParent: g.Tree = {
        val Init(gtpt, _, gargss) = tree
        g.build.SyntacticApplied(gtpt, gargss).setPos(tree.pos)
      }
      def toGNew: g.Tree = {
        val Init(gtpt, _, gargss) = tree
        g.New(gtpt, gargss).setPos(tree.pos)
      }
    }

    implicit class XtensionGInit(gtree: g.Tree) {
      def toInitFromGParent: Init = {
        val applied = g.treeInfo.Applied(gtree)
        val name = NameAnonymous().copyAttrs(gtree).setPos(applied.callee.pos.focusEnd)
        Init(applied.callee, name, applied.argss).copyAttrs(gtree)
      }
      def toInitFromGNew: Init = {
        ???
      }
    }

    object Self extends SelfCompanion {
      def apply(name: Term.Name, decltpe: Option[Type]): Self = c.Self(name, decltpe)
      def unapply(gtree: Any): Option[(Term.Name, Option[Type])] = gtree match {
        case tree: c.Self => c.Self.unapply(tree)
        case _ => None
      }
    }

    implicit class XtensionSelf(self: Self) {
      def toGSelf: g.ValDef = {
        val Self(name, tpe) = self
        val gmods = g.Modifiers(gf.PRIVATE)
        val gtpt = tpe.getOrElse(g.TypeTree())
        g.ValDef(gmods, name.toGTermName, gtpt, g.EmptyTree).setPos(self.pos)
      }
    }

    implicit class XtensionGSelf(gtree: g.ValDef) {
      def toSelf: Self = {
        val g.ValDef(gmods, _, gtpt, g.EmptyTree) = gtree
        val name = gtree.toTermName
        val tpe = gtpt match { case g.TypeTree() => None; case gtpt => Some(gtpt) }
        c.Self(name, tpe).copyAttrs(gtree)
      }
    }

    // NOTE: Mostly copy/pasted from ReificationSupport.scala.
    protected object UnCtor {
      def unapply(gtree: g.Tree): Option[(g.Modifiers, List[List[g.ValDef]], List[g.Tree])] = {
        gtree match {
          case g.DefDef(
              mods,
              g.nme.MIXIN_CONSTRUCTOR,
              _,
              _,
              _,
              g.build.SyntacticBlock(lvdefs :+ _)) =>
            Some((mods | gf.TRAIT, Nil, lvdefs))
          case g.DefDef(
              mods,
              g.nme.CONSTRUCTOR,
              Nil,
              vparamss,
              _,
              g.build.SyntacticBlock(lvdefs :+ _ :+ _)) =>
            Some((mods, vparamss, lvdefs))
          case _ =>
            None
        }
      }
    }

    // NOTE: Mostly copy/pasted from ReificationSupport.scala.
    protected object UnMkTemplate {
      def unapply(gtree: g.Template): Option[
        (List[g.Tree], g.ValDef, g.Modifiers, List[List[g.ValDef]], List[g.Tree], List[g.Tree])] = {
        val g.Template(parents, selfType, tbody) = gtree

        def result(
            ctorMods: g.Modifiers,
            vparamss: List[List[g.ValDef]],
            edefs: List[g.Tree],
            body: List[g.Tree]) =
          Some((parents, selfType, ctorMods, vparamss, edefs, body))
        def indexOfCtor(trees: List[g.Tree]) =
          trees.indexWhere { case UnCtor(_, _, _) => true; case _ => false }

        if (tbody forall g.treeInfo.isInterfaceMember)
          result(g.NoMods | gf.TRAIT, Nil, Nil, tbody)
        else if (indexOfCtor(tbody) == -1)
          None
        else {
          val (rawEdefs, rest) = tbody.span(g.treeInfo.isEarlyDef)
          val (gvdefs, etdefs) = rawEdefs.partition(g.treeInfo.isEarlyValDef)
          val (fieldDefs, UnCtor(ctorMods, ctorVparamss, lvdefs) :: body) =
            rest.splitAt(indexOfCtor(rest))
          val evdefs = gvdefs.zip(lvdefs).map {
            case (gvdef @ g.ValDef(_, _, tpt: g.TypeTree, _), g.ValDef(_, _, _, rhs)) =>
              g.copyValDef(gvdef)(tpt = tpt.original, rhs = rhs)
            case _ =>
              ??? // this is impossible
          }
          val edefs = evdefs ::: etdefs
          if (ctorMods.isTrait)
            result(ctorMods, Nil, edefs, body)
          else {
            // undo conversion from (implicit ... ) to ()(implicit ... ) when it's the only parameter section
            val vparamssRestoredImplicits = ctorVparamss match {
              case Nil :: (tail @ ((head :: _) :: _)) if head.mods.isImplicit => tail
              case other => other
            }
            // undo flag modifications by merging flag info from constructor args and fieldDefs
            val modsMap = fieldDefs.map { case g.ValDef(mods, name, _, _) => name -> mods }.toMap
            def ctorArgsCorrespondToFields = vparamssRestoredImplicits.flatten.forall { vd =>
              modsMap.contains(vd.name)
            }
            if (!ctorArgsCorrespondToFields) None
            else {
              val vparamss = mmap(vparamssRestoredImplicits) { vd =>
                val originalMods = modsMap(vd.name) | (vd.mods.flags & gf.DEFAULTPARAM)
                g.copyValDef(vd)(mods = originalMods)
              }
              result(ctorMods, vparamss, edefs, body)
            }
          }
        }
      }
    }

    object Template extends TemplateCompanion {
      def apply(early: List[Stat], inits: List[Init], self: Self, stats: List[Stat]): Template = {
        c.Template(early, inits, self, stats)
      }
      def unapply(gtree: Any): Option[(List[Stat], List[Init], Self, List[Stat])] = gtree match {
        case tree: c.Template => c.Template.unapply(tree)
        case _ => None
      }
    }

    implicit class XtensionTemplate(tree: Template) {
      def toGTemplate(gctorMods: g.Modifiers, gctorParamss: List[List[g.ValDef]]): g.Template = {
        val Template(early, inits, self, stats) = tree
        val gearly = early.toGStats
        val gparents = inits.map(_.toGParent)
        val gself = self.toGSelf
        val gstats = gearly ++ stats.toGStats
        g.gen.mkTemplate(gparents, gself, gctorMods, gctorParamss, gstats).setPos(tree.pos)
      }
    }

    implicit class XtensionGTemplate(gtree: g.Template) {
      def toTemplate: Template = {
        val UnMkTemplate(gparents, gself, _, _, gearly, gstats) = gtree
        val early = gearly.toStats
        val inits = gparents.map(_.toInitFromGParent)
        val self = gself.toSelf
        val stats = gstats.toStats
        Template(early, inits, self, stats).copyAttrs(gtree)
      }
    }

    implicit class XtensionMods(mods: List[Mod]) {
      def toGModifiers: g.Modifiers = {
        // TODO: implement me
        g.Modifiers()
      }
    }

    implicit class XtensionGMods(gmods: g.Modifiers) {
      def toModifiers: List[Mod] = {
        // TODO: implement me
        Nil
      }
    }

    object ModAnnot extends ModAnnotCompanion {
      def apply(init: Init): Mod = c.ModAnnot(init)
      def unapply(gtree: Any): Option[Init] = gtree match {
        case tree: c.ModAnnot => c.ModAnnot.unapply(tree)
        case _ => None
      }
    }

    object ModPrivate extends ModPrivateCompanion {
      def apply(within: Ref): Mod = c.ModPrivate(within)
      def unapply(gtree: Any): Option[Ref] = gtree match {
        case tree: c.ModPrivate => c.ModPrivate.unapply(tree)
        case _ => None
      }
    }

    object ModProtected extends ModProtectedCompanion {
      def apply(within: Ref): Mod = c.ModProtected(within)
      def unapply(gtree: Any): Option[Ref] = gtree match {
        case tree: c.ModProtected => c.ModProtected.unapply(tree)
        case _ => None
      }
    }

    object ModImplicit extends ModImplicitCompanion {
      def apply(): Mod = c.ModImplicit()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModImplicit => c.ModImplicit.unapply(tree)
        case _ => false
      }
    }

    object ModFinal extends ModFinalCompanion {
      def apply(): Mod = c.ModFinal()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModFinal => c.ModFinal.unapply(tree)
        case _ => false
      }
    }

    object ModSealed extends ModSealedCompanion {
      def apply(): Mod = c.ModSealed()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModSealed => c.ModSealed.unapply(tree)
        case _ => false
      }
    }

    object ModOverride extends ModOverrideCompanion {
      def apply(): Mod = c.ModOverride()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModOverride => c.ModOverride.unapply(tree)
        case _ => false
      }
    }

    object ModCase extends ModCaseCompanion {
      def apply(): Mod = c.ModCase()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModCase => c.ModCase.unapply(tree)
        case _ => false
      }
    }

    object ModAbstract extends ModAbstractCompanion {
      def apply(): Mod = c.ModAbstract()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModAbstract => c.ModAbstract.unapply(tree)
        case _ => false
      }
    }

    object ModCovariant extends ModCovariantCompanion {
      def apply(): Mod = c.ModCovariant()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModCovariant => c.ModCovariant.unapply(tree)
        case _ => false
      }
    }

    object ModContravariant extends ModContravariantCompanion {
      def apply(): Mod = c.ModContravariant()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModContravariant => c.ModContravariant.unapply(tree)
        case _ => false
      }
    }

    object ModLazy extends ModLazyCompanion {
      def apply(): Mod = c.ModLazy()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModLazy => c.ModLazy.unapply(tree)
        case _ => false
      }
    }

    object ModValParam extends ModValParamCompanion {
      def apply(): Mod = c.ModValParam()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModValParam => c.ModValParam.unapply(tree)
        case _ => false
      }
    }

    object ModVarParam extends ModVarParamCompanion {
      def apply(): Mod = c.ModVarParam()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModVarParam => c.ModVarParam.unapply(tree)
        case _ => false
      }
    }

    object ModInline extends ModInlineCompanion {
      def apply(): Mod = c.ModInline()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ModInline => c.ModInline.unapply(tree)
        case _ => false
      }
    }

    object EnumeratorGenerator extends EnumeratorGeneratorCompanion {
      def apply(pat: Pat, rhs: Term): Enumerator = c.EnumeratorGenerator(pat, rhs)
      def unapply(gtree: Any): Option[(Pat, Term)] = gtree match {
        case tree: c.EnumeratorGenerator => c.EnumeratorGenerator.unapply(tree)
        case _ => None
      }
    }

    object EnumeratorVal extends EnumeratorValCompanion {
      def apply(pat: Pat, rhs: Term): Enumerator = c.EnumeratorVal(pat, rhs)
      def unapply(gtree: Any): Option[(Pat, Term)] = gtree match {
        case tree: c.EnumeratorVal => c.EnumeratorVal.unapply(tree)
        case _ => None
      }
    }

    object EnumeratorGuard extends EnumeratorGuardCompanion {
      def apply(cond: Term): Enumerator = c.EnumeratorGuard(cond)
      def unapply(gtree: Any): Option[Term] = gtree match {
        case tree: c.EnumeratorGuard => c.EnumeratorGuard.unapply(tree)
        case _ => None
      }
    }

    object Import extends ImportCompanion {
      def apply(importers: List[Importer]): Import = ???
      def unapply(gtree: Any): Option[List[Importer]] = ???
    }

    object Importer extends ImporterCompanion {
      def apply(ref: Term.Ref, importees: List[Importee]): Importer = c.Importer(ref, importees)
      def unapply(gtree: Any): Option[(Term.Ref, List[Importee])] = gtree match {
        case tree: c.Importer => c.Importer.unapply(tree)
        case _ => None
      }
    }

    object ImporteeWildcard extends ImporteeWildcardCompanion {
      def apply(): Importee = c.ImporteeWildcard()
      def unapply(gtree: Any): Boolean = gtree match {
        case tree: c.ImporteeWildcard => c.ImporteeWildcard.unapply(tree)
        case _ => false
      }
    }

    object ImporteeName extends ImporteeNameCompanion {
      def apply(name: Name): Importee = c.ImporteeName(name)
      def unapply(gtree: Any): Option[Name] = gtree match {
        case tree: c.ImporteeName => c.ImporteeName.unapply(tree)
        case _ => None
      }
    }

    object ImporteeRename extends ImporteeRenameCompanion {
      def apply(name: Name, rename: Name): Importee = c.ImporteeRename(name, rename)
      def unapply(gtree: Any): Option[(Name, Name)] = gtree match {
        case tree: c.ImporteeRename => c.ImporteeRename.unapply(tree)
        case _ => None
      }
    }

    object ImporteeUnimport extends ImporteeUnimportCompanion {
      def apply(name: Name): Importee = c.ImporteeUnimport(name)
      def unapply(gtree: Any): Option[Name] = gtree match {
        case tree: c.ImporteeUnimport => c.ImporteeUnimport.unapply(tree)
        case _ => None
      }
    }

    object Case extends CaseCompanion {
      def apply(pat: Pat, cond: Option[Term], body: Term): Case = ???
      def unapply(gtree: Any): Option[(Pat, Option[Term], Term)] = ???
    }

    object Source extends SourceCompanion {
      def apply(stats: List[Stat]): Source = ???
      def unapply(gtree: Any): Option[List[Stat]] = ???
    }
  }

  val c: customTrees.type = customTrees
  object customTrees {
    sealed trait Name extends g.RefTree {
      def value: String
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = {
        if (this.isInstanceOf[TypeName]) g.TypeName(value).encode
        else g.TermName(value).encode
      }
    }

    case class NameAnonymous() extends TermName with TypeName {
      def value: String = "_"
    }

    case class NameIndeterminate(value: String) extends Name

    // TODO: TermName and TypeName are special in the sense that the are only custom trees
    // that are stats, i.e. they can be returned from macros.
    // As a result, we can't define them as completely unrelated classes, because then
    // scalac will be confused should the metaprogrammer decided to return those trees in expansion.
    // Luckily, not only g.Tree is not sealed, but also g.Ident is not final.

    sealed trait TermName extends Name
    class TermNameImpl(val value: String) extends g.Ident(g.TermName(value).encode) with TermName {
      override def qualifier: g.Tree = super[TermName].qualifier
      override val name: g.Name = super[TermName].name
    }

    sealed trait TypeName extends Name
    class TypeNameImpl(val value: String) extends g.Ident(g.TypeName(value).encode) with TypeName {
      override def qualifier: g.Tree = super[TypeName].qualifier
      override val name: g.Name = super[TypeName].name
    }

    case class Init(mtpe: Type, mname: Name, argss: List[List[Term]]) extends g.RefTree {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = mname.name
    }

    case class Self(mname: Term.Name, decltpe: Option[Type]) extends g.DefTree {
      def name: g.Name = mname.name
    }

    case class Template(es: List[Stat], is: List[Init], self: Self, ss: List[Stat]) extends g.Tree

    sealed trait Mod extends g.Tree

    case class ModAnnot(init: Init) extends Mod

    case class ModPrivate(within: Ref) extends Mod

    case class ModProtected(within: Ref) extends Mod

    case class ModImplicit() extends Mod

    case class ModFinal() extends Mod

    case class ModSealed() extends Mod

    case class ModOverride() extends Mod

    case class ModCase() extends Mod

    case class ModAbstract() extends Mod

    case class ModCovariant() extends Mod

    case class ModContravariant() extends Mod

    case class ModLazy() extends Mod

    case class ModValParam() extends Mod

    case class ModVarParam() extends Mod

    case class ModInline() extends Mod

    sealed trait Enumerator extends g.Tree

    case class EnumeratorGenerator(pat: Pat, rhs: Term) extends Enumerator

    case class EnumeratorVal(pat: Pat, rhs: Term) extends Enumerator

    case class EnumeratorGuard(cond: Term) extends Enumerator

    case class Importer(ref: Term.Ref, importees: List[Importee]) extends g.Tree

    sealed trait Importee extends g.RefTree

    case class ImporteeWildcard() extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = g.nme.WILDCARD
    }

    case class ImporteeName(mname: Name) extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = mname.name
    }

    case class ImporteeRename(mname: Name, mrename: Name) extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = mrename.name
    }

    case class ImporteeUnimport(mname: Name) extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = g.nme.WILDCARD
    }
  }
}
