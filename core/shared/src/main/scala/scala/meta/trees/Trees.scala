package scala.meta
package trees

private[scala] trait Trees extends Abstracts with Companions with Extensions { root: Universe =>
  type Tree >: Null <: AnyRef // Tree.pos, Tree.syntax, Tree.structure

  type Ref >: Null <: Tree
  type Stat >: Null <: Tree

  type Name >: Null <: Ref // Name.value
  private[scala] trait NameHack {
    type Anonymous >: Null <: Term.Name with Type.Name
  }
  object Name extends NameHack {
    def unapply(tree: Tree) = abstracts.nameUnapply(tree)
    def Anonymous = abstracts.NameAnonymous
    def Indeterminate = abstracts.NameIndeterminate
  }

  type Lit >: Null <: Term with Type with Pat // Lit.value
  object Lit {
    def unapply(tree: Tree) = abstracts.litUnapply(tree)
    def Unit = abstracts.LitUnit
    def Boolean = abstracts.LitBoolean
    def Byte = abstracts.LitByte
    def Short = abstracts.LitShort
    def Char = abstracts.LitChar
    def Int = abstracts.LitInt
    def Float = abstracts.LitFloat
    def Long = abstracts.LitLong
    def Double = abstracts.LitDouble
    def String = abstracts.LitString
    def Symbol = abstracts.LitSymbol
    def Null = abstracts.LitNull
  }

  type Term >: Null <: Stat
  private[scala] trait TermHack {
    type Ref >: Null <: Term with root.Ref
    type Name >: Null <: root.Name with Term.Ref with Pat
    type Param >: Null <: Member.Term
  }
  object Term extends TermHack { // Term.fresh
    def This = abstracts.TermThis
    def Super = abstracts.TermSuper
    def Name = abstracts.TermName
    def Select = abstracts.TermSelect
    def Interpolate = abstracts.TermInterpolate
    def Xml = abstracts.TermXml
    def Apply = abstracts.TermApply
    def ApplyType = abstracts.TermApplyType
    def ApplyInfix = abstracts.TermApplyInfix
    def ApplyUnary = abstracts.TermApplyUnary
    def Assign = abstracts.TermAssign
    def Return = abstracts.TermReturn
    def Throw = abstracts.TermThrow
    def Ascribe = abstracts.TermAscribe
    def Annotate = abstracts.TermAnnotate
    def Tuple = abstracts.TermTuple
    def Block = abstracts.TermBlock
    def If = abstracts.TermIf
    def Match = abstracts.TermMatch
    def Try = abstracts.TermTry
    def TryWithHandler = abstracts.TermTryWithHandler
    def Function = abstracts.TermFunction
    def PartialFunction = abstracts.TermPartialFunction
    def While = abstracts.TermWhile
    def Do = abstracts.TermDo
    def For = abstracts.TermFor
    def ForYield = abstracts.TermForYield
    def New = abstracts.TermNew
    def NewAnonymous = abstracts.TermNewAnonymous
    def Placeholder = abstracts.TermPlaceholder
    def Eta = abstracts.TermEta
    def Repeated = abstracts.TermRepeated
    def Param = abstracts.TermParam
    def fresh(prefix: String = "fresh") = abstracts.termFresh(prefix)
  }

  type Type >: Null <: Tree
  private[scala] trait TypeHack {
    type Ref >: Null <: Type with root.Ref
    type Name >: Null <: root.Name with Type.Ref
    type Bounds >: Null <: Tree
    type Var >: Null <: Type with Member.Type
    type Param >: Null <: Member.Type
  }
  object Type extends TypeHack { // Type.fresh
    def Name = abstracts.TypeName
    def Select = abstracts.TypeSelect
    def Project = abstracts.TypeProject
    def Singleton = abstracts.TypeSingleton
    def Apply = abstracts.TypeApply
    def ApplyInfix = abstracts.TypeApplyInfix
    def Function = abstracts.TypeFunction
    def Tuple = abstracts.TypeTuple
    def With = abstracts.TypeWith
    def And = abstracts.TypeAnd
    def Or = abstracts.TypeOr
    def Refine = abstracts.TypeRefine
    def Existential = abstracts.TypeExistential
    def Annotate = abstracts.TypeAnnotate
    def Placeholder = abstracts.TypePlaceholder
    def Bounds = abstracts.TypeBounds
    def ByName = abstracts.TypeByName
    def Repeated = abstracts.TypeRepeated
    def Var = abstracts.TypeVar
    def Param = abstracts.TypeParam
    def fresh(prefix: String = "fresh") = abstracts.typeFresh(prefix)
  }

  type Pat >: Null <: Tree
  private[scala] trait PatHack {
    type Var >: Null <: Pat with Member.Term
  }
  object Pat extends PatHack { // Pat.fresh
    def Var = abstracts.PatVar
    def Wildcard = abstracts.PatWildcard
    def SeqWildcard = abstracts.PatSeqWildcard
    def Bind = abstracts.PatBind
    def Alternative = abstracts.PatAlternative
    def Tuple = abstracts.PatTuple
    def Extract = abstracts.PatExtract
    def ExtractInfix = abstracts.PatExtractInfix
    def Interpolate = abstracts.PatInterpolate
    def Xml = abstracts.PatXml
    def Typed = abstracts.PatTyped
    def fresh(prefix: String = "fresh") = abstracts.patFresh(prefix)
  }

  type Member >: Null <: Tree // Member.name
  private[scala] trait MemberHack {
    type Term >: Null <: Member // Member.Term.name
    type Type >: Null <: Member // Member.Type.name
  }
  object Member extends MemberHack

  type Decl >: Null <: Stat
  private[scala] trait DeclHack {
    type Val >: Null <: Decl
    type Var >: Null <: Decl
    type Def >: Null <: Decl with Member.Term
    type Type >: Null <: Decl with Member.Type
  }
  object Decl extends DeclHack {
    def Val = abstracts.DeclVal
    def Var = abstracts.DeclVar
    def Def = abstracts.DeclDef
    def Type = abstracts.DeclType
  }

  type Defn >: Null <: Stat
  private[scala] trait DefnHack {
    type Val >: Null <: Defn
    type Var >: Null <: Defn
    type Def >: Null <: Defn with Member.Term
    type Macro >: Null <: Defn with Member.Term
    type Type >: Null <: Defn with Member.Type
    type Class >: Null <: Defn with Member.Type
    type Trait >: Null <: Defn with Member.Type
    type Object >: Null <: Defn with Member.Term
  }
  object Defn extends DefnHack {
    def Val = abstracts.DefnVal
    def Var = abstracts.DefnVar
    def Def = abstracts.DefnDef
    def Macro = abstracts.DefnMacro
    def Type = abstracts.DefnType
    def Class = abstracts.DefnClass
    def Trait = abstracts.DefnTrait
    def Object = abstracts.DefnObject
  }

  type Pkg >: Null <: Member.Term with Stat // Pkg.name
  private[scala] trait PkgHack {
    type Object >: Null <: Member.Term with Stat
  }
  object Pkg extends PkgHack {
    def apply(ref: Term.Ref, stats: List[Stat]) = abstracts.Pkg.apply(ref, stats)
    def unapply(tree: Tree): Option[(Term.Ref, List[Stat])] = abstracts.Pkg.unapply(tree)
    def Object = abstracts.PkgObject
  }

  type Ctor >: Null <: Member
  private[scala] trait CtorHack {
    type Primary >: Null <: Ctor
    type Secondary >: Null <: Ctor with Stat
  }
  object Ctor extends CtorHack {
    def Primary = abstracts.CtorPrimary
    def Secondary = abstracts.CtorSecondary
  }

  type Init >: Null <: Ref
  def Init = abstracts.Init

  type Self >: Null <: Member
  def Self = abstracts.Self

  type Template >: Null <: Tree
  def Template = abstracts.Template

  type Mod >: Null <: Tree
  object Mod {
    def Annot = abstracts.ModAnnot
    def Private = abstracts.ModPrivate
    def Protected = abstracts.ModProtected
    def Implicit = abstracts.ModImplicit
    def Final = abstracts.ModFinal
    def Sealed = abstracts.ModSealed
    def Override = abstracts.ModOverride
    def Case = abstracts.ModCase
    def Abstract = abstracts.ModAbstract
    def Covariant = abstracts.ModCovariant
    def Contravariant = abstracts.ModContravariant
    def Lazy = abstracts.ModLazy
    def ValParam = abstracts.ModValParam
    def VarParam = abstracts.ModVarParam
    def Inline = abstracts.ModInline
  }

  type Enumerator >: Null <: Tree
  object Enumerator {
    def Generator = abstracts.EnumeratorGenerator
    def Val = abstracts.EnumeratorVal
    def Guard = abstracts.EnumeratorGuard
  }

  type Import >: Null <: Stat
  def Import = abstracts.Import

  type Importer >: Null <: Tree
  def Importer = abstracts.Importer

  type Importee >: Null <: Ref
  object Importee {
    def Wildcard = abstracts.ImporteeWildcard
    def Name = abstracts.ImporteeName
    def Rename = abstracts.ImporteeRename
    def Unimport = abstracts.ImporteeUnimport
  }

  type Case >: Null <: Tree
  def Case = abstracts.Case

  type Source >: Null <: Tree
  def Source = abstracts.Source
}
