package scala.macros.internal
package engines.scalac
package trees

import scala.tools.nsc.Global

// NOTE: Scalac ASTs aren't sufficiently strongly-typed to fully implement our API.
// As a result, we have to approximate with things like `type Term = g.Tree`.
// Additionally, we have to come up with new AST nodes to express things that
// aren't modeled as trees in Scalac. Luckily, Scalac's Tree class is not sealed.
trait Trees extends scala.macros.trees.Trees with Abstracts with Companions { self: Universe =>
  import treeCompanions._

  type Tree = g.Tree
  type Ref = g.RefTree
  type Stat = g.Tree
  type Name = g.Ident
  type Lit = g.Literal

  type Term = g.Tree
  override val Term = TermCompanion
  object TermCompanion extends TermCompanion {
    type Ref = g.RefTree
    type Name = g.Ident
    type Param = g.ValDef
  }

  type Type = g.Tree
  override val Type = TypeCompanion
  object TypeCompanion extends TypeCompanion {
    type Ref = g.RefTree
    type Name = g.Ident
    type Bounds = g.TypeBoundsTree
    type Var = g.Bind
    type Param = g.TypeDef
  }

  type Pat = g.Tree
  override val Pat = PatCompanion
  object PatCompanion extends PatCompanion {
    type Var = g.Bind
  }

  type Member = g.DefTree
  override val Member = MemberCompanion
  object MemberCompanion extends MemberCompanion {
    type Term = g.DefTree
    type Type = g.DefTree
  }

  type Decl = g.Tree
  override val Decl = DeclCompanion
  object DeclCompanion extends DeclCompanion {
    type Val = g.Tree
    type Var = g.Tree
    type Def = g.DefDef
    type Type = g.TypeDef
  }

  type Defn = g.Tree
  override val Defn = DefnCompanion
  object DefnCompanion extends DefnCompanion {
    type Val = g.Tree
    type Var = g.Tree
    type Def = g.DefDef
    type Macro = g.DefDef
    type Type = g.TypeDef
    type Class = g.ClassDef
    type Trait = g.ClassDef
    type Object = g.ModuleDef
  }

  type Pkg = g.PackageDef
  override val Pkg = PkgCompanion
  object PkgCompanion extends PkgCompanion {
    type Object = g.ModuleDef
  }

  type Ctor = g.DefDef
  override val Ctor = CtorCompanion
  object CtorCompanion extends CtorCompanion {
    type Primary = g.DefDef
    type Secondary = g.DefDef
  }

  case class Init(mtpe: Type, mname: Name, argss: List[List[Term]]) extends Ref {
    def qualifier: g.Tree = g.EmptyTree
    def name: g.Name = mname.name
  }
  override object Init extends InitCompanion

  case class Self(mname: Term.Name, decltpe: Option[Type]) extends Member {
    def name: g.Name = mname.name
  }
  override object Self extends SelfCompanion

  type Template = g.Template

  sealed trait Mod extends Tree
  override val Mod = ModCompanion
  object ModCompanion extends ModCompanion {
    case class Annot(init: Init) extends Mod
    override object Annot extends ModAnnotCompanion
    case class Private(within: Ref) extends Mod
    override object Private extends ModPrivateCompanion
    case class Protected(within: Ref) extends Mod
    override object Protected extends ModProtectedCompanion
    case class Implicit() extends Mod
    override object Implicit extends ModImplicitCompanion
    case class Final() extends Mod
    override object Final extends ModFinalCompanion
    case class Sealed() extends Mod
    override object Sealed extends ModSealedCompanion
    case class Override() extends Mod
    override object Override extends ModOverrideCompanion
    case class Case() extends Mod
    override object Case extends ModCaseCompanion
    case class Abstract() extends Mod
    override object Abstract extends ModAbstractCompanion
    case class Covariant() extends Mod
    override object Covariant extends ModCovariantCompanion
    case class Contravariant() extends Mod
    override object Contravariant extends ModContravariantCompanion
    case class Lazy() extends Mod
    override object Lazy extends ModLazyCompanion
    case class ValParam() extends Mod
    override object ValParam extends ModValParamCompanion
    case class VarParam() extends Mod
    override object VarParam extends ModVarParamCompanion
    case class Inline() extends Mod
    override object Inline extends ModInlineCompanion
  }

  sealed trait Enumerator extends Tree
  override val Enumerator = EnumeratorCompanion
  object EnumeratorCompanion extends EnumeratorCompanion {
    case class Generator(pat: Pat, rhs: Term) extends Enumerator
    override object Generator extends EnumeratorGeneratorCompanion
    case class Val(pat: Pat, rhs: Term) extends Enumerator
    override object Val extends EnumeratorValCompanion
    case class Guard(cond: Term) extends Enumerator
    override object Guard extends EnumeratorGuardCompanion
  }

  type Import = g.Import

  case class Importer(ref: Term.Ref, importees: List[Importee]) extends Tree
  override object Importer extends ImporterCompanion

  sealed trait Importee extends Ref
  override val Importee = ImporteeCompanion
  object ImporteeCompanion extends ImporteeCompanion {
    case class Wildcard() extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = g.nme.WILDCARD
    }
    override object Wildcard extends ImporteeWildcardCompanion
    case class Name(mname: self.Name) extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = mname.name
    }
    override object Name extends ImporteeNameCompanion
    case class Rename(mname: self.Name, mrename: self.Name) extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = mrename.name
    }
    override object Rename extends ImporteeRenameCompanion
    case class Unimport(mname: self.Name) extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = g.nme.WILDCARD
    }
    override object Unimport extends ImporteeUnimportCompanion
  }

  type Case = g.CaseDef

  type Source = g.PackageDef
}