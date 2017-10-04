package scala.macros
package trees

import scala.reflect.ClassTag

private[macros] trait Abstracts { self: Universe =>
  import treeCompanions._

  private[macros] def abstracts: TreeAbstracts
  private[macros] trait TreeAbstracts {
    def treePos(tree: Tree): Position
    def nameValue(name: Name): String
    def nameApply(value: String): Name
    def nameUnapply(tree: Any): Option[String]
    def litValue(lit: Lit): Any
    def litUnapply(tree: Any): Option[Any]
    def memberName(member: Member): Name
    def NameAnonymous: NameAnonymousCompanion
    def NameIndeterminate: NameIndeterminateCompanion
    def LitUnit: LitUnitCompanion
    def LitBoolean: LitBooleanCompanion
    def LitByte: LitByteCompanion
    def LitShort: LitShortCompanion
    def LitChar: LitCharCompanion
    def LitInt: LitIntCompanion
    def LitFloat: LitFloatCompanion
    def LitLong: LitLongCompanion
    def LitDouble: LitDoubleCompanion
    def LitString: LitStringCompanion
    def LitSymbol: LitSymbolCompanion
    def LitNull: LitNullCompanion
    def TermThis: TermThisCompanion
    def TermSuper: TermSuperCompanion
    def TermName: TermNameCompanion
    def TermSelect: TermSelectCompanion
    def TermInterpolate: TermInterpolateCompanion
    def TermXml: TermXmlCompanion
    def TermApply: TermApplyCompanion
    def TermApplyType: TermApplyTypeCompanion
    def TermApplyInfix: TermApplyInfixCompanion
    def TermApplyUnary: TermApplyUnaryCompanion
    def TermAssign: TermAssignCompanion
    def TermReturn: TermReturnCompanion
    def TermThrow: TermThrowCompanion
    def TermAscribe: TermAscribeCompanion
    def TermAnnotate: TermAnnotateCompanion
    def TermTuple: TermTupleCompanion
    def TermBlock: TermBlockCompanion
    def TermIf: TermIfCompanion
    def TermMatch: TermMatchCompanion
    def TermTry: TermTryCompanion
    def TermTryWithHandler: TermTryWithHandlerCompanion
    def TermFunction: TermFunctionCompanion
    def TermPartialFunction: TermPartialFunctionCompanion
    def TermWhile: TermWhileCompanion
    def TermDo: TermDoCompanion
    def TermFor: TermForCompanion
    def TermForYield: TermForYieldCompanion
    def TermNew: TermNewCompanion
    def TermNewAnonymous: TermNewAnonymousCompanion
    def TermPlaceholder: TermPlaceholderCompanion
    def TermEta: TermEtaCompanion
    def TermRepeated: TermRepeatedCompanion
    def TermParam: TermParamCompanion
    def TypeName: TypeNameCompanion
    def TypeSelect: TypeSelectCompanion
    def TypeProject: TypeProjectCompanion
    def TypeSingleton: TypeSingletonCompanion
    def TypeApply: TypeApplyCompanion
    def TypeApplyInfix: TypeApplyInfixCompanion
    def TypeFunction: TypeFunctionCompanion
    def TypeTuple: TypeTupleCompanion
    def TypeWith: TypeWithCompanion
    def TypeAnd: TypeAndCompanion
    def TypeOr: TypeOrCompanion
    def TypeRefine: TypeRefineCompanion
    def TypeExistential: TypeExistentialCompanion
    def TypeAnnotate: TypeAnnotateCompanion
    def TypePlaceholder: TypePlaceholderCompanion
    def TypeBounds: TypeBoundsCompanion
    def TypeByName: TypeByNameCompanion
    def TypeRepeated: TypeRepeatedCompanion
    def TypeVar: TypeVarCompanion
    def TypeMethod: TypeMethodCompanion
    def TypeLambda: TypeLambdaCompanion
    def TypeParam: TypeParamCompanion
    def PatVar: PatVarCompanion
    def PatWildcard: PatWildcardCompanion
    def PatSeqWildcard: PatSeqWildcardCompanion
    def PatBind: PatBindCompanion
    def PatAlternative: PatAlternativeCompanion
    def PatTuple: PatTupleCompanion
    def PatExtract: PatExtractCompanion
    def PatExtractInfix: PatExtractInfixCompanion
    def PatInterpolate: PatInterpolateCompanion
    def PatXml: PatXmlCompanion
    def PatTyped: PatTypedCompanion
    def DeclVal: DeclValCompanion
    def DeclVar: DeclVarCompanion
    def DeclDef: DeclDefCompanion
    def DeclType: DeclTypeCompanion
    def DefnVal: DefnValCompanion
    def DefnVar: DefnVarCompanion
    def DefnDef: DefnDefCompanion
    def DefnMacro: DefnMacroCompanion
    def DefnType: DefnTypeCompanion
    def DefnClass: DefnClassCompanion
    def DefnTrait: DefnTraitCompanion
    def DefnObject: DefnObjectCompanion
    def PkgProper: PkgProperCompanion
    def PkgObject: PkgObjectCompanion
    def CtorPrimary: CtorPrimaryCompanion
    def CtorSecondary: CtorSecondaryCompanion
    def Init: InitCompanion
    def Self: SelfCompanion
    def Template: TemplateCompanion
    def ModAnnot: ModAnnotCompanion
    def ModPrivate: ModPrivateCompanion
    def ModProtected: ModProtectedCompanion
    def ModImplicit: ModImplicitCompanion
    def ModFinal: ModFinalCompanion
    def ModSealed: ModSealedCompanion
    def ModOverride: ModOverrideCompanion
    def ModCase: ModCaseCompanion
    def ModAbstract: ModAbstractCompanion
    def ModCovariant: ModCovariantCompanion
    def ModContravariant: ModContravariantCompanion
    def ModLazy: ModLazyCompanion
    def ModValParam: ModValParamCompanion
    def ModVarParam: ModVarParamCompanion
    def ModMacro: ModMacroCompanion
    def EnumeratorGenerator: EnumeratorGeneratorCompanion
    def EnumeratorVal: EnumeratorValCompanion
    def EnumeratorGuard: EnumeratorGuardCompanion
    def Import: ImportCompanion
    def Importer: ImporterCompanion
    def ImporteeWildcard: ImporteeWildcardCompanion
    def ImporteeName: ImporteeNameCompanion
    def ImporteeRename: ImporteeRenameCompanion
    def ImporteeUnimport: ImporteeUnimportCompanion
    def Case: CaseCompanion
    def Source: SourceCompanion
  }
}
