package scala.meta
package trees

private[scala] trait Companions { self: Universe =>
  private[scala] val treeCompanions: TreeCompanions = new TreeCompanions {}
  private[scala] trait TreeCompanions {
    trait NameAnonymousCompanion {
      def apply(): Term.Name with Type.Name
      def unapply(tree: Any): Boolean
    }

    trait NameIndeterminateCompanion {
      def apply(value: String): Name
      def unapply(tree: Any): Option[String]
    }

    trait LitUnitCompanion {
      def apply(): Lit
      def unapply(tree: Any): Boolean
    }

    trait LitBooleanCompanion {
      def apply(value: Boolean): Lit
      def unapply(tree: Any): Option[Boolean]
    }

    trait LitByteCompanion {
      def apply(value: Byte): Lit
      def unapply(tree: Any): Option[Byte]
    }

    trait LitShortCompanion {
      def apply(value: Short): Lit
      def unapply(tree: Any): Option[Short]
    }

    trait LitCharCompanion {
      def apply(value: Char): Lit
      def unapply(tree: Any): Option[Char]
    }

    trait LitIntCompanion {
      def apply(value: Int): Lit
      def unapply(tree: Any): Option[Int]
    }

    trait LitFloatCompanion {
      def apply(value: Float): Lit
      def unapply(tree: Any): Option[Float]
    }

    trait LitLongCompanion {
      def apply(value: Long): Lit
      def unapply(tree: Any): Option[Long]
    }

    trait LitDoubleCompanion {
      def apply(value: Double): Lit
      def unapply(tree: Any): Option[Double]
    }

    trait LitStringCompanion {
      def apply(value: String): Lit
      def unapply(tree: Any): Option[String]
    }

    trait LitSymbolCompanion {
      def apply(value: scala.Symbol): Lit
      def unapply(tree: Any): Option[scala.Symbol]
    }

    trait LitNullCompanion {
      def apply(): Lit
      def unapply(tree: Any): Boolean
    }

    trait TermThisCompanion {
      def apply(qual: Name): Term.Ref
      def unapply(tree: Any): Option[Name]
    }

    trait TermSuperCompanion {
      def apply(thisp: Name, superp: Name): Term.Ref
      def unapply(tree: Any): Option[(Name, Name)]
    }

    trait TermNameCompanion {
      def apply(value: String): Term.Name
      def apply(sym: Symbol): Term.Name
      def unapply(tree: Any): Option[String]
    }

    trait TermSelectCompanion {
      def apply(qual: Term, name: Term.Name): Term.Ref
      def unapply(tree: Any): Option[(Term, Term.Name)]
    }

    trait TermInterpolateCompanion {
      def apply(prefix: Term.Name, parts: List[Lit], args: List[Term]): Term
      def unapply(tree: Any): Option[(Term.Name, List[Lit], List[Term])]
    }

    trait TermXmlCompanion {
      def apply(parts: List[Lit], args: List[Term]): Term
      def unapply(tree: Any): Option[(List[Lit], List[Term])]
    }

    trait TermApplyCompanion {
      def apply(fun: Term, args: List[Term]): Term
      def unapply(tree: Any): Option[(Term, List[Term])]
    }

    trait TermApplyTypeCompanion {
      def apply(fun: Term, targs: List[Type]): Term
      def unapply(tree: Any): Option[(Term, List[Type])]
    }

    trait TermApplyInfixCompanion {
      def apply(lhs: Term, op: Name, targs: List[Type], args: List[Term]): Term
      def unapply(tree: Any): Option[(Term, Name, List[Type], List[Term])]
    }

    trait TermApplyUnaryCompanion {
      def apply(op: Name, arg: Term): Term.Ref
      def unapply(tree: Any): Option[(Name, Term)]
    }

    trait TermAssignCompanion {
      def apply(lhs: Term, rhs: Term): Term
      def unapply(tree: Any): Option[(Term, Term)]
    }

    trait TermReturnCompanion {
      def apply(expr: Term): Term
      def unapply(tree: Any): Option[Term]
    }

    trait TermThrowCompanion {
      def apply(expr: Term): Term
      def unapply(tree: Any): Option[Term]
    }

    trait TermAscribeCompanion {
      def apply(expr: Term, tpe: Type): Term
      def unapply(tree: Any): Option[(Term, Type)]
    }

    trait TermAnnotateCompanion {
      def apply(expr: Term, annots: List[Mod]): Term
      def unapply(tree: Any): Option[(Term, List[Mod])]
    }

    trait TermTupleCompanion {
      def apply(args: List[Term]): Term
      def unapply(tree: Any): Option[List[Term]]
    }

    trait TermBlockCompanion {
      def apply(stats: List[Stat]): Term
      def unapply(tree: Any): Option[List[Stat]]
    }

    trait TermIfCompanion {
      def apply(cond: Term, thenp: Term, elsep: Term): Term
      def unapply(tree: Any): Option[(Term, Term, Term)]
    }

    trait TermMatchCompanion {
      def apply(expr: Term, cases: List[Case]): Term
      def unapply(tree: Any): Option[(Term, List[Case])]
    }

    trait TermTryCompanion {
      def apply(expr: Term, catchp: List[Case], finallyp: Option[Term]): Term
      def unapply(tree: Any): Option[(Term, List[Case], Option[Term])]
    }

    trait TermTryWithHandlerCompanion {
      def apply(expr: Term, catchp: Term, finallyp: Option[Term]): Term
      def unapply(tree: Any): Option[(Term, Term, Option[Term])]
    }

    trait TermFunctionCompanion {
      def apply(params: List[Term.Param], body: Term): Term
      def unapply(tree: Any): Option[(List[Term.Param], Term)]
    }

    trait TermPartialFunctionCompanion {
      def apply(cases: List[Case]): Term
      def unapply(tree: Any): Option[List[Case]]
    }

    trait TermWhileCompanion {
      def apply(expr: Term, body: Term): Term
      def unapply(tree: Any): Option[(Term, Term)]
    }

    trait TermDoCompanion {
      def apply(body: Term, expr: Term): Term
      def unapply(tree: Any): Option[(Term, Term)]
    }

    trait TermForCompanion {
      def apply(enums: List[Enumerator], body: Term): Term
      def unapply(tree: Any): Option[(List[Enumerator], Term)]
    }

    trait TermForYieldCompanion {
      def apply(enums: List[Enumerator], body: Term): Term
      def unapply(tree: Any): Option[(List[Enumerator], Term)]
    }

    trait TermNewCompanion {
      def apply(init: Init): Term
      def unapply(tree: Any): Option[Init]
    }

    trait TermNewAnonymousCompanion {
      def apply(templ: Template): Term
      def unapply(tree: Any): Option[Template]
    }

    trait TermPlaceholderCompanion {
      def apply(): Term
      def unapply(tree: Any): Boolean
    }

    trait TermEtaCompanion {
      def apply(expr: Term): Term
      def unapply(tree: Any): Option[Term]
    }

    trait TermRepeatedCompanion {
      def apply(expr: Term): Term
      def unapply(tree: Any): Option[Term]
    }

    trait TermParamCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          decltpe: Option[Type],
          default: Option[Term]): Term.Param
      def unapply(tree: Any): Option[(List[Mod], Term.Name, Option[Type], Option[Term])]
    }

    trait TypeNameCompanion {
      def apply(value: String): Type.Name
      def apply(sym: Symbol): Type.Name
      def unapply(tree: Any): Option[String]
    }

    trait TypeSelectCompanion {
      def apply(qual: Term.Ref, name: Type.Name): Type.Ref
      def unapply(tree: Any): Option[(Term.Ref, Type.Name)]
    }

    trait TypeProjectCompanion {
      def apply(qual: Type, name: Type.Name): Type.Ref
      def unapply(tree: Any): Option[(Type, Type.Name)]
    }

    trait TypeSingletonCompanion {
      def apply(ref: Term.Ref): Type.Ref
      def unapply(tree: Any): Option[Term.Ref]
    }

    trait TypeApplyCompanion {
      def apply(tpe: Type, args: List[Type]): Type
      def unapply(tree: Any): Option[(Type, List[Type])]
    }

    trait TypeApplyInfixCompanion {
      def apply(lhs: Type, op: Name, rhs: Type): Type
      def unapply(tree: Any): Option[(Type, Name, Type)]
    }

    trait TypeFunctionCompanion {
      def apply(params: List[Type], res: Type): Type
      def unapply(tree: Any): Option[(List[Type], Type)]
    }

    trait TypeTupleCompanion {
      def apply(args: List[Type]): Type
      def unapply(tree: Any): Option[List[Type]]
    }

    trait TypeWithCompanion {
      def apply(lhs: Type, rhs: Type): Type
      def unapply(tree: Any): Option[(Type, Type)]
    }

    trait TypeAndCompanion {
      def apply(lhs: Type, rhs: Type): Type
      def unapply(tree: Any): Option[(Type, Type)]
    }

    trait TypeOrCompanion {
      def apply(lhs: Type, rhs: Type): Type
      def unapply(tree: Any): Option[(Type, Type)]
    }

    trait TypeRefineCompanion {
      def apply(tpe: Option[Type], stats: List[Stat]): Type
      def unapply(tree: Any): Option[(Option[Type], List[Stat])]
    }

    trait TypeExistentialCompanion {
      def apply(tpe: Type, stats: List[Stat]): Type
      def unapply(tree: Any): Option[(Type, List[Stat])]
    }

    trait TypeAnnotateCompanion {
      def apply(tpe: Type, annots: List[Mod]): Type
      def unapply(tree: Any): Option[(Type, List[Mod])]
    }

    trait TypePlaceholderCompanion {
      def apply(bounds: Type.Bounds): Type
      def unapply(tree: Any): Option[Type.Bounds]
    }

    trait TypeBoundsCompanion {
      def apply(lo: Option[Type], hi: Option[Type]): Type.Bounds
      def unapply(tree: Any): Option[(Option[Type], Option[Type])]
    }

    trait TypeByNameCompanion {
      def apply(tpe: Type): Type
      def unapply(tree: Any): Option[Type]
    }

    trait TypeRepeatedCompanion {
      def apply(tpe: Type): Type
      def unapply(tree: Any): Option[Type]
    }

    trait TypeVarCompanion {
      def apply(name: Type.Name): Type.Var
      def unapply(tree: Any): Option[Type.Name]
    }

    trait TypeMethodCompanion {
      def apply(paramss: List[List[Term.Param]], tpe: Type): Type
      def unapply(tree: Any): Option[(List[List[Term.Param]], Type)]
    }

    trait TypeLambdaCompanion {
      def apply(tparams: List[Type.Param], tpe: Type): Type
      def unapply(tree: Any): Option[(List[Type.Param], Type)]
    }

    trait TypeParamCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          tbounds: Type.Bounds,
          vbounds: List[Type],
          cbounds: List[Type]): Type.Param
      def unapply(tree: Any)
        : Option[(List[Mod], Type.Name, List[Type.Param], Type.Bounds, List[Type], List[Type])]
    }

    trait PatVarCompanion {
      def apply(name: Term.Name): Pat.Var
      def unapply(tree: Any): Option[Term.Name]
    }

    trait PatWildcardCompanion {
      def apply(): Pat
      def unapply(tree: Any): Boolean
    }

    trait PatSeqWildcardCompanion {
      def apply(): Pat
      def unapply(tree: Any): Boolean
    }

    trait PatBindCompanion {
      def apply(lhs: Pat, rhs: Pat): Pat
      def unapply(tree: Any): Option[(Pat, Pat)]
    }

    trait PatAlternativeCompanion {
      def apply(lhs: Pat, rhs: Pat): Pat
      def unapply(tree: Any): Option[(Pat, Pat)]
    }

    trait PatTupleCompanion {
      def apply(args: List[Pat]): Pat
      def unapply(tree: Any): Option[List[Pat]]
    }

    trait PatExtractCompanion {
      def apply(fun: Term, args: List[Pat]): Pat
      def unapply(tree: Any): Option[(Term, List[Pat])]
    }

    trait PatExtractInfixCompanion {
      def apply(lhs: Pat, op: Term.Name, rhs: List[Pat]): Pat
      def unapply(tree: Any): Option[(Pat, Term.Name, List[Pat])]
    }

    trait PatInterpolateCompanion {
      def apply(prefix: Term.Name, parts: List[Lit], args: List[Pat]): Pat
      def unapply(tree: Any): Option[(Term.Name, List[Lit], List[Pat])]
    }

    trait PatXmlCompanion {
      def apply(parts: List[Lit], args: List[Pat]): Pat
      def unapply(tree: Any): Option[(List[Lit], List[Pat])]
    }

    trait PatTypedCompanion {
      def apply(lhs: Pat, rhs: Type): Pat
      def unapply(tree: Any): Option[(Pat, Type)]
    }

    trait DeclValCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Type): Decl.Val
      def unapply(tree: Any): Option[(List[Mod], List[Pat], Type)]
    }

    trait DeclVarCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Type): Decl.Var
      def unapply(tree: Any): Option[(List[Mod], List[Pat], Type)]
    }

    trait DeclDefCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Type): Decl.Def
      def unapply(
          tree: Any): Option[(List[Mod], Term.Name, List[Type.Param], List[List[Term.Param]], Type)]
    }

    trait DeclTypeCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          bounds: Type.Bounds): Decl.Type
      def unapply(tree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Type.Bounds)]
    }

    trait DefnValCompanion {
      def apply(mods: List[Mod], pats: List[Pat], decltpe: Option[Type], rhs: Term): Defn.Val
      def unapply(tree: Any): Option[(List[Mod], List[Pat], Option[Type], Term)]
    }

    trait DefnVarCompanion {
      def apply(
          mods: List[Mod],
          pats: List[Pat],
          decltpe: Option[Type],
          rhs: Option[Term]): Defn.Var
      def unapply(tree: Any): Option[(List[Mod], List[Pat], Option[Type], Option[Term])]
    }

    trait DefnDefCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Option[Type],
          body: Term): Defn.Def
      def unapply(tree: Any): Option[
        (List[Mod], Term.Name, List[Type.Param], List[List[Term.Param]], Option[Type], Term)]
    }

    trait DefnMacroCompanion {
      def apply(
          mods: List[Mod],
          name: Term.Name,
          tparams: List[Type.Param],
          paramss: List[List[Term.Param]],
          decltpe: Option[Type],
          body: Term): Defn.Macro
      def unapply(tree: Any): Option[
        (List[Mod], Term.Name, List[Type.Param], List[List[Term.Param]], Option[Type], Term)]
    }

    trait DefnTypeCompanion {
      def apply(mods: List[Mod], name: Type.Name, tparams: List[Type.Param], body: Type): Defn.Type
      def unapply(tree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Type)]
    }

    trait DefnClassCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          ctor: Ctor.Primary,
          templ: Template): Defn.Class
      def unapply(
          tree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Ctor.Primary, Template)]
    }

    trait DefnTraitCompanion {
      def apply(
          mods: List[Mod],
          name: Type.Name,
          tparams: List[Type.Param],
          ctor: Ctor.Primary,
          templ: Template): Defn.Trait
      def unapply(
          tree: Any): Option[(List[Mod], Type.Name, List[Type.Param], Ctor.Primary, Template)]
    }

    trait DefnObjectCompanion {
      def apply(mods: List[Mod], name: Term.Name, templ: Template): Defn.Object
      def unapply(tree: Any): Option[(List[Mod], Term.Name, Template)]
    }

    trait PkgProperCompanion {
      def apply(ref: Term.Ref, stats: List[Stat]): Pkg
      def unapply(tree: Any): Option[(Term.Ref, List[Stat])]
    }

    trait PkgObjectCompanion {
      def apply(mods: List[Mod], name: Term.Name, templ: Template): Pkg.Object
      def unapply(tree: Any): Option[(List[Mod], Term.Name, Template)]
    }

    trait CtorPrimaryCompanion {
      def apply(mods: List[Mod], name: Name, paramss: List[List[Term.Param]]): Ctor.Primary
      def unapply(tree: Any): Option[(List[Mod], Name, List[List[Term.Param]])]
    }

    trait CtorSecondaryCompanion {
      def apply(
          mods: List[Mod],
          name: Name,
          paramss: List[List[Term.Param]],
          init: Init,
          stats: List[Stat]): Ctor.Secondary
      def unapply(tree: Any): Option[(List[Mod], Name, List[List[Term.Param]], Init, List[Stat])]
    }

    trait InitCompanion {
      def apply(tpe: Type, name: Name, argss: List[List[Term]]): Init
      def unapply(tree: Any): Option[(Type, Name, List[List[Term]])]
    }

    trait SelfCompanion {
      def apply(name: Term.Name, decltpe: Option[Type]): Self
      def unapply(tree: Any): Option[(Term.Name, Option[Type])]
    }

    trait TemplateCompanion {
      def apply(early: List[Stat], inits: List[Init], self: Self, stats: List[Stat]): Template
      def unapply(tree: Any): Option[(List[Stat], List[Init], Self, List[Stat])]
    }

    trait ModAnnotCompanion {
      def apply(init: Init): Mod
      def unapply(tree: Any): Option[Init]
    }

    trait ModPrivateCompanion {
      def apply(within: Ref): Mod
      def unapply(tree: Any): Option[Ref]
    }

    trait ModProtectedCompanion {
      def apply(within: Ref): Mod
      def unapply(tree: Any): Option[Ref]
    }

    trait ModImplicitCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModFinalCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModSealedCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModOverrideCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModCaseCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModAbstractCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModCovariantCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModContravariantCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModLazyCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModValParamCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModVarParamCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait ModInlineCompanion {
      def apply(): Mod
      def unapply(tree: Any): Boolean
    }

    trait EnumeratorGeneratorCompanion {
      def apply(pat: Pat, rhs: Term): Enumerator
      def unapply(tree: Any): Option[(Pat, Term)]
    }

    trait EnumeratorValCompanion {
      def apply(pat: Pat, rhs: Term): Enumerator
      def unapply(tree: Any): Option[(Pat, Term)]
    }

    trait EnumeratorGuardCompanion {
      def apply(cond: Term): Enumerator
      def unapply(tree: Any): Option[Term]
    }

    trait ImportCompanion {
      def apply(importers: List[Importer]): Import
      def unapply(tree: Any): Option[List[Importer]]
    }

    trait ImporterCompanion {
      def apply(ref: Term.Ref, importees: List[Importee]): Importer
      def unapply(tree: Any): Option[(Term.Ref, List[Importee])]
    }

    trait ImporteeWildcardCompanion {
      def apply(): Importee
      def unapply(tree: Any): Boolean
    }

    trait ImporteeNameCompanion {
      def apply(name: Name): Importee
      def unapply(tree: Any): Option[Name]
    }

    trait ImporteeRenameCompanion {
      def apply(name: Name, rename: Name): Importee
      def unapply(tree: Any): Option[(Name, Name)]
    }

    trait ImporteeUnimportCompanion {
      def apply(name: Name): Importee
      def unapply(tree: Any): Option[Name]
    }

    trait CaseCompanion {
      def apply(pat: Pat, cond: Option[Term], body: Term): Case
      def unapply(tree: Any): Option[(Pat, Option[Term], Term)]
    }

    trait SourceCompanion {
      def apply(stats: List[Stat]): Source
      def unapply(tree: Any): Option[List[Stat]]
    }
  }
}
