package scala.macros.internal
package engines.scalac
package trees

import scala.macros.inputs._
import scala.macros.internal.engines.scalac.inputs._

trait Abstracts extends scala.macros.trees.Abstracts with Positions { self: Universe =>
  import treeCompanions._

  val abstracts: TreeAbstracts
  trait TreeAbstracts extends super.TreeAbstracts {
    def treePos(tree: Tree): Position = tree.pos

    def nameValue(name: Name): String = nameUnapply(name).get

    def nameUnapply(tree: Any): Option[String] = tree match {
      case tree: g.Ident => Some(tree.name.decoded)
      case _ => None
    }

    def litValue(lit: Lit): Any = litUnapply(lit).get

    def litUnapply(tree: Any): Option[Any] = tree match {
      case tree: g.Literal => Some(tree.value.value)
      case _ => None
    }

    def memberName(member: Member): Name = g.Ident(member.name)

    object NameAnonymous extends NameAnonymousCompanion {
      def apply(): Term.Name with Type.Name = ???
      def unapply(tree: Any): Boolean = ???
    }

    object NameIndeterminate extends NameIndeterminateCompanion {
      def apply(value: String): Name = ???
      def unapply(tree: Any): Option[String] = ???
    }

    object LitUnit extends LitUnitCompanion {
      def apply(value: Unit): Lit = ???
      def unapply(tree: Any): Option[Unit] = ???
    }

    object LitBoolean extends LitBooleanCompanion {
      def apply(value: Boolean): Lit = ???
      def unapply(tree: Any): Option[Boolean] = ???
    }

    object LitByte extends LitByteCompanion {
      def apply(value: Byte): Lit = ???
      def unapply(tree: Any): Option[Byte] = ???
    }

    object LitShort extends LitShortCompanion {
      def apply(value: Short): Lit = ???
      def unapply(tree: Any): Option[Short] = ???
    }

    object LitChar extends LitCharCompanion {
      def apply(value: Char): Lit = ???
      def unapply(tree: Any): Option[Char] = ???
    }

    object LitInt extends LitIntCompanion {
      def apply(value: Int): Lit = ???
      def unapply(tree: Any): Option[Int] = ???
    }

    object LitFloat extends LitFloatCompanion {
      def apply(value: Float): Lit = ???
      def unapply(tree: Any): Option[Float] = ???
    }

    object LitLong extends LitLongCompanion {
      def apply(value: Long): Lit = ???
      def unapply(tree: Any): Option[Long] = ???
    }

    object LitDouble extends LitDoubleCompanion {
      def apply(value: Double): Lit = ???
      def unapply(tree: Any): Option[Double] = ???
    }

    object LitString extends LitStringCompanion {
      def apply(value: String): Lit = ???
      def unapply(tree: Any): Option[String] = ???
    }

    object LitSymbol extends LitSymbolCompanion {
      def apply(value: Symbol): Lit = ???
      def unapply(tree: Any): Option[Symbol] = ???
    }

    object LitNull extends LitNullCompanion {
      def apply(value: Any): Lit = ???
      def unapply(tree: Any): Option[Any] = ???
    }

    object TermThis extends TermThisCompanion {
      def apply(qual: Name): Term.Ref = ???
      def unapply(tree: Any): Option[Name] = ???
    }

    object TermSuper extends TermSuperCompanion {
      def apply(thisp: Name, superp: Name): Term.Ref = ???
      def unapply(tree: Any): Option[(Name, Name)] = ???
    }

    object TermName extends TermNameCompanion {
      def apply(value: String): Term.Name = ???
      def unapply(tree: Any): Option[String] = ???
    }

    object TermSelect extends TermSelectCompanion {
      def apply(qual: Term, name: Term.Name): Term.Ref = ???
      def unapply(tree: Any): Option[(Term, Term.Name)] = ???
    }

    object TermInterpolate extends TermInterpolateCompanion {
      def apply(prefix: Term.Name, parts: List[Lit], args: List[Term]): Term = ???
      def unapply(tree: Any): Option[(Term.Name, List[Lit], List[Term])] = ???
    }

    object TermXml extends TermXmlCompanion {
      def apply(parts: List[Lit], args: List[Term]): Term = ???
      def unapply(tree: Any): Option[(List[Lit], List[Term])] = ???
    }

    object TermApply extends TermApplyCompanion {
      def apply(fun: Term, args: List[Term]): Term = ???
      def unapply(tree: Any): Option[(Term, List[Term])] = ???
    }

    object TermApplyType extends TermApplyTypeCompanion {
      def apply(fun: Term, targs: List[Type]): Term = ???
      def unapply(tree: Any): Option[(Term, List[Type])] = ???
    }

    object TermApplyInfix extends TermApplyInfixCompanion {
      def apply(lhs: Term, op: Name, targs: List[Type], args: List[Term]): Term = ???
      def unapply(tree: Any): Option[(Term, Name, List[Type], List[Term])] = ???
    }

    object TermApplyUnary extends TermApplyUnaryCompanion {
      def apply(op: Name, arg: Term): Term.Ref = ???
      def unapply(tree: Any): Option[(Name, Term)] = ???
    }

    object TermAssign extends TermAssignCompanion {
      def apply(lhs: Term, rhs: Term): Term = ???
      def unapply(tree: Any): Option[(Term, Term)] = ???
    }

    object TermReturn extends TermReturnCompanion {
      def apply(expr: Term): Term = ???
      def unapply(tree: Any): Option[Term] = ???
    }

    object TermThrow extends TermThrowCompanion {
      def apply(expr: Term): Term = ???
      def unapply(tree: Any): Option[Term] = ???
    }

    object TermAscribe extends TermAscribeCompanion {
      def apply(expr: Term, tpe: Type): Term = ???
      def unapply(tree: Any): Option[(Term, Type)] = ???
    }

    object TermAnnotate extends TermAnnotateCompanion {
      def apply(expr: Term, annots: List[Mod]): Term = ???
      def unapply(tree: Any): Option[(Term, List[Mod])] = ???
    }

    object TermTuple extends TermTupleCompanion {
      def apply(args: List[Term]): Term = ???
      def unapply(tree: Any): Option[List[Term]] = ???
    }

    object TermBlock extends TermBlockCompanion {
      def apply(stats: List[Stat]): Term = ???
      def unapply(tree: Any): Option[List[Stat]] = ???
    }

    object TermIf extends TermIfCompanion {
      def apply(cond: Term, thenp: Term, elsep: Term): Term = ???
      def unapply(tree: Any): Option[(Term, Term, Term)] = ???
    }

    object TermMatch extends TermMatchCompanion {
      def apply(expr: Term, cases: List[Case]): Term = ???
      def unapply(tree: Any): Option[(Term, List[Case])] = ???
    }

    object TermTry extends TermTryCompanion {
      def apply(expr: Term, catchp: List[Case], finallyp: Option[Term]): Term = ???
      def unapply(tree: Any): Option[(Term, List[Case], Option[Term])] = ???
    }

    object TermTryWithHandler extends TermTryWithHandlerCompanion {
      def apply(expr: Term, catchp: Term, finallyp: Option[Term]): Term = ???
      def unapply(tree: Any): Option[(Term, Term, Option[Term])] = ???
    }

    object TermFunction extends TermFunctionCompanion {
      def apply(params: List[Term.Param], body: Term): Term = ???
      def unapply(tree: Any): Option[(List[Term.Param], Term)] = ???
    }

    object TermPartialFunction extends TermPartialFunctionCompanion {
      def apply(cases: List[Case]): Term = ???
      def unapply(tree: Any): Option[List[Case]] = ???
    }

    object TermWhile extends TermWhileCompanion {
      def apply(expr: Term, body: Term): Term = ???
      def unapply(tree: Any): Option[(Term, Term)] = ???
    }

    object TermDo extends TermDoCompanion {
      def apply(body: Term, expr: Term): Term = ???
      def unapply(tree: Any): Option[(Term, Term)] = ???
    }

    object TermFor extends TermForCompanion {
      def apply(enums: List[Enumerator], body: Term): Term = ???
      def unapply(tree: Any): Option[(List[Enumerator], Term)] = ???
    }

    object TermForYield extends TermForYieldCompanion {
      def apply(enums: List[Enumerator], body: Term): Term = ???
      def unapply(tree: Any): Option[(List[Enumerator], Term)] = ???
    }

    object TermNew extends TermNewCompanion {
      def apply(init: Init): Term = ???
      def unapply(tree: Any): Option[Init] = ???
    }

    object TermNewAnonymous extends TermNewAnonymousCompanion {
      def apply(templ: Template): Term = ???
      def unapply(tree: Any): Option[Template] = ???
    }

    object TermPlaceholder extends TermPlaceholderCompanion {
      def apply(): Term = ???
      def unapply(tree: Any): Boolean = ???
    }

    object TermEta extends TermEtaCompanion {
      def apply(expr: Term): Term = ???
      def unapply(tree: Any): Option[Term] = ???
    }

    object TermRepeated extends TermRepeatedCompanion {
      def apply(expr: Term): Term = ???
      def unapply(tree: Any): Option[Term] = ???
    }

    object TermParam extends TermParamCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          decltpe: Option[Type],
          default: Option[Term]): Term.Param = ???
      def unapply(tree: Any): Option[(List[Mod], Term.Name, Option[Type], Option[Term])] = ???
    }

    object TypeName extends TypeNameCompanion {
      def apply(value: String): Type.Name = ???
      def unapply(tree: Any): Option[String] = ???
    }

    object TypeSelect extends TypeSelectCompanion {
      def apply(qual: Term.Ref, name: Type.Name): Type.Ref = ???
      def unapply(tree: Any): Option[(Term.Ref, Type.Name)] = ???
    }

    object TypeProject extends TypeProjectCompanion {
      def apply(qual: Type, name: Type.Name): Type.Ref = ???
      def unapply(tree: Any): Option[(Type, Type.Name)] = ???
    }

    object TypeSingleton extends TypeSingletonCompanion {
      def apply(ref: Term.Ref): Type.Ref = ???
      def unapply(tree: Any): Option[Term.Ref] = ???
    }

    object TypeApply extends TypeApplyCompanion {
      def apply(tpe: Type, args: List[Type]): Type = ???
      def unapply(tree: Any): Option[(Type, List[Type])] = ???
    }

    object TypeApplyInfix extends TypeApplyInfixCompanion {
      def apply(lhs: Type, op: Name, rhs: Type): Type = ???
      def unapply(tree: Any): Option[(Type, Name, Type)] = ???
    }

    object TypeFunction extends TypeFunctionCompanion {
      def apply(params: List[Type], res: Type): Type = ???
      def unapply(tree: Any): Option[(List[Type], Type)] = ???
    }

    object TypeTuple extends TypeTupleCompanion {
      def apply(args: List[Type]): Type = ???
      def unapply(tree: Any): Option[List[Type]] = ???
    }

    object TypeWith extends TypeWithCompanion {
      def apply(lhs: Type, rhs: Type): Type = ???
      def unapply(tree: Any): Option[(Type, Type)] = ???
    }

    object TypeAnd extends TypeAndCompanion {
      def apply(lhs: Type, rhs: Type): Type = ???
      def unapply(tree: Any): Option[(Type, Type)] = ???
    }

    object TypeOr extends TypeOrCompanion {
      def apply(lhs: Type, rhs: Type): Type = ???
      def unapply(tree: Any): Option[(Type, Type)] = ???
    }

    object TypeRefine extends TypeRefineCompanion {
      def apply(tpe: Option[Type], stats: List[Stat]): Type = ???
      def unapply(tree: Any): Option[(Option[Type], List[Stat])] = ???
    }

    object TypeExistential extends TypeExistentialCompanion {
      def apply(tpe: Type, stats: List[Stat]): Type = ???
      def unapply(tree: Any): Option[(Type, List[Stat])] = ???
    }

    object TypeAnnotate extends TypeAnnotateCompanion {
      def apply(tpe: Type, annots: List[Mod]): Type = ???
      def unapply(tree: Any): Option[(Type, List[Mod])] = ???
    }

    object TypePlaceholder extends TypePlaceholderCompanion {
      def apply(bounds: Type.Bounds): Type = ???
      def unapply(tree: Any): Option[Type.Bounds] = ???
    }

    object TypeBounds extends TypeBoundsCompanion {
      def apply(lo: Option[Type], hi: Option[Type]): Type.Bounds = ???
      def unapply(tree: Any): Option[(Option[Type], Option[Type])] = ???
    }

    object TypeByName extends TypeByNameCompanion {
      def apply(tpe: Type): Type = ???
      def unapply(tree: Any): Option[Type] = ???
    }

    object TypeRepeated extends TypeRepeatedCompanion {
      def apply(tpe: Type): Type = ???
      def unapply(tree: Any): Option[Type] = ???
    }

    object TypeVar extends TypeVarCompanion {
      def apply(name: Type.Name): Type.Var = ???
      def unapply(tree: Any): Option[Type.Name] = ???
    }

    object TypeParam extends TypeParamCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          tbounds: Type.Bounds,
          vbounds: List[Type],
          cbounds: List[Type]): Type.Param = ???
      def unapply(tree: Any)
        : Option[(List[Mod], Type.Name, List[Type.Param], Type.Bounds, List[Type], List[Type])] =
        ???
    }

    object PatVar extends PatVarCompanion {
      def apply(name: Term.Name): Pat.Var = ???
      def unapply(tree: Any): Option[Term.Name] = ???
    }

    object PatWildcard extends PatWildcardCompanion {
      def apply(): Pat = ???
      def unapply(tree: Any): Boolean = ???
    }

    object PatSeqWildcard extends PatSeqWildcardCompanion {
      def apply(): Pat = ???
      def unapply(tree: Any): Boolean = ???
    }

    object PatBind extends PatBindCompanion {
      def apply(lhs: Pat, rhs: Pat): Pat = ???
      def unapply(tree: Any): Option[(Pat, Pat)] = ???
    }

    object PatAlternative extends PatAlternativeCompanion {
      def apply(lhs: Pat, rhs: Pat): Pat = ???
      def unapply(tree: Any): Option[(Pat, Pat)] = ???
    }

    object PatTuple extends PatTupleCompanion {
      def apply(args: List[Pat]): Pat = ???
      def unapply(tree: Any): Option[List[Pat]] = ???
    }

    object PatExtract extends PatExtractCompanion {
      def apply(fun: Term, args: List[Pat]): Pat = ???
      def unapply(tree: Any): Option[(Term, List[Pat])] = ???
    }

    object PatExtractInfix extends PatExtractInfixCompanion {
      def apply(lhs: Pat, op: Term.Name, rhs: List[Pat]): Pat = ???
      def unapply(tree: Any): Option[(Pat, Term.Name, List[Pat])] = ???
    }

    object PatInterpolate extends PatInterpolateCompanion {
      def apply(prefix: Term.Name, parts: List[Lit], args: List[Pat]): Pat = ???
      def unapply(tree: Any): Option[(Term.Name, List[Lit], List[Pat])] = ???
    }

    object PatXml extends PatXmlCompanion {
      def apply(parts: List[Lit], args: List[Pat]): Pat = ???
      def unapply(tree: Any): Option[(List[Lit], List[Pat])] = ???
    }

    object PatTyped extends PatTypedCompanion {
      def apply(lhs: Pat, rhs: Type): Pat = ???
      def unapply(tree: Any): Option[(Pat, Type)] = ???
    }

    object DeclVal extends DeclValCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Type): Decl.Val = ???
      def unapply(tree: Any): Option[(List[Mod], List[Pat], Type)] = ???
    }

    object DeclVar extends DeclVarCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Type): Decl.Var = ???
      def unapply(tree: Any): Option[(List[Mod], List[Pat], Type)] = ???
    }

    object DeclDef extends DeclDefCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Type): Decl.Def = ???
      def unapply(tree: Any)
        : Option[(List[Mod], Term.Name, List[Type.Param], List[List[Term.Param]], Type)] = ???
    }

    object DeclType extends DeclTypeCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          bounds: Type.Bounds): Decl.Type = ???
      def unapply(tree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Type.Bounds)] = ???
    }

    object DefnVal extends DefnValCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Option[Type], rhs: Term): Defn.Val = ???
      def unapply(tree: Any): Option[(List[Mod], List[Pat], Option[Type], Term)] = ???
    }

    object DefnVar extends DefnVarCompanion {
      def apply(
          mods: List[Mod],
          pats: List[Pat],
          decltpe: Option[Type],
          rhs: Option[Term]): Defn.Var = ???
      def unapply(tree: Any): Option[(List[Mod], List[Pat], Option[Type], Option[Term])] = ???
    }

    object DefnDef extends DefnDefCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Option[Type],
          body: Term): Defn.Def = ???
      def unapply(tree: Any): Option[
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
      def unapply(tree: Any): Option[
        (List[Mod], Term.Name, List[Type.Param], List[List[Term.Param]], Option[Type], Term)] = ???
    }

    object DefnType extends DefnTypeCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          body: Type): Defn.Type = ???
      def unapply(tree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Type)] = ???
    }

    object DefnClass extends DefnClassCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          ctor: Ctor.Primary,
          templ: Template): Defn.Class = ???
      def unapply(
          tree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Ctor.Primary, Template)] =
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
          tree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Ctor.Primary, Template)] =
        ???
    }

    object DefnObject extends DefnObjectCompanion {
      def apply(mods: List[Mod], name: Term.Name, templ: Template): Defn.Object = ???
      def unapply(tree: Any): Option[(List[Mod], Term.Name, Template)] = ???
    }

    object PkgProper extends PkgProperCompanion {
      def apply(ref: Term.Ref, stats: List[Stat]): Pkg = ???
      def unapply(tree: Any): Option[(Term.Ref, List[Stat])] = ???
    }

    object PkgObject extends PkgObjectCompanion {
      def apply(mods: List[Mod], name: Term.Name, templ: Template): Pkg.Object = ???
      def unapply(tree: Any): Option[(List[Mod], Term.Name, Template)] = ???
    }

    object CtorPrimary extends CtorPrimaryCompanion {
      def apply(mods: List[Mod], name: Name, paramss: List[List[Term.Param]]): Ctor.Primary = ???
      def unapply(tree: Any): Option[(List[Mod], Name, List[List[Term.Param]])] = ???
    }

    object CtorSecondary extends CtorSecondaryCompanion {
      def apply(
          mods: List[Mod],
          name: Name,
          paramss: List[List[Term.Param]],
          init: Init,
          stats: List[Stat]): Ctor.Secondary = ???
      def unapply(tree: Any): Option[(List[Mod], Name, List[List[Term.Param]], Init, List[Stat])] =
        ???
    }

    lazy val Init = self.Init

    lazy val Self = self.Self

    object Template extends TemplateCompanion {
      def apply(early: List[Stat], inits: List[Init], self: Self, stats: List[Stat]): Template =
        ???
      def unapply(tree: Any): Option[(List[Stat], List[Init], Self, List[Stat])] = ???
    }

    lazy val ModAnnot = self.Mod.Annot

    lazy val ModPrivate = self.Mod.Private

    lazy val ModProtected = self.Mod.Protected

    lazy val ModImplicit = self.Mod.Implicit

    lazy val ModFinal = self.Mod.Final

    lazy val ModSealed = self.Mod.Sealed

    lazy val ModOverride = self.Mod.Override

    lazy val ModCase = self.Mod.Case

    lazy val ModAbstract = self.Mod.Abstract

    lazy val ModCovariant = self.Mod.Covariant

    lazy val ModContravariant = self.Mod.Contravariant

    lazy val ModLazy = self.Mod.Lazy

    lazy val ModValParam = self.Mod.ValParam

    lazy val ModVarParam = self.Mod.VarParam

    lazy val ModInline = self.Mod.Inline

    lazy val EnumeratorGenerator = self.Enumerator.Generator

    lazy val EnumeratorVal = self.Enumerator.Val

    lazy val EnumeratorGuard = self.Enumerator.Guard

    object Import extends ImportCompanion {
      def apply(importers: List[Importer]): Import = ???
      def unapply(tree: Any): Option[List[Importer]] = ???
    }

    lazy val Importer = self.Importer

    lazy val ImporteeWildcard = self.Importee.Wildcard

    lazy val ImporteeName = self.Importee.Name

    lazy val ImporteeRename = self.Importee.Rename

    lazy val ImporteeUnimport = self.Importee.Unimport

    object Case extends CaseCompanion {
      def apply(pat: Pat, cond: Option[Term], body: Term): Case = ???
      def unapply(tree: Any): Option[(Pat, Option[Term], Term)] = ???
    }

    object Source extends SourceCompanion {
      def apply(stats: List[Stat]): Source = ???
      def unapply(tree: Any): Option[List[Stat]] = ???
    }
  }
}
