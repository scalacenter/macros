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
  override object Term extends TermCompanion {
    type Ref = g.RefTree
    type Name = g.Ident
    type Param = g.ValDef
  }

  type Type = g.Tree
  override object Type extends TypeCompanion {
    type Ref = g.RefTree
    type Name = g.Ident
    type Bounds = g.TypeBoundsTree
    type Var = g.Bind
    type Param = g.TypeDef
  }

  type Pat = g.Tree
  override object Pat extends PatCompanion {
    type Var = g.Bind
  }

  type Member = g.DefTree
  override object Member extends MemberCompanion {
    type Term = g.DefTree
    type Type = g.DefTree
  }

  type Decl = g.Tree
  override object Decl extends DeclCompanion {
    type Val = g.Tree
    type Var = g.Tree
    type Def = g.DefDef
    type Type = g.TypeDef
  }

  type Defn = g.Tree
  override object Defn extends DefnCompanion {
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
  override object Pkg extends PkgCompanion {
    type Object = g.ModuleDef
  }

  type Ctor = g.DefDef
  override object Ctor extends CtorCompanion {
    type Primary = g.DefDef
    type Secondary = g.DefDef
  }

  final case class Init(mtpe: Type, mname: Name, argss: List[List[Term]]) extends Ref {
    def qualifier: g.Tree = g.EmptyTree
    def name: g.Name = mname.name
  }
  override object Init extends InitCompanion

  final case class Self(mname: Term.Name, decltpe: Option[Type]) extends Member {
    def name: g.Name = mname.name
  }
  override object Self extends SelfCompanion

  type Template = g.Template

  sealed trait Mod extends Tree
  override object Mod extends ModCompanion {
    final case class Annot(init: Init) extends Mod
    override object Annot extends ModAnnotCompanion
    final case class Private(within: Ref) extends Mod
    override object Private extends ModPrivateCompanion
    final case class Protected(within: Ref) extends Mod
    override object Protected extends ModProtectedCompanion
    final case class Implicit() extends Mod
    override object Implicit extends ModImplicitCompanion
    final case class Final() extends Mod
    override object Final extends ModFinalCompanion
    final case class Sealed() extends Mod
    override object Sealed extends ModSealedCompanion
    final case class Override() extends Mod
    override object Override extends ModOverrideCompanion
    final case class Case() extends Mod
    override object Case extends ModCaseCompanion
    final case class Abstract() extends Mod
    override object Abstract extends ModAbstractCompanion
    final case class Covariant() extends Mod
    override object Covariant extends ModCovariantCompanion
    final case class Contravariant() extends Mod
    override object Contravariant extends ModContravariantCompanion
    final case class Lazy() extends Mod
    override object Lazy extends ModLazyCompanion
    final case class ValParam() extends Mod
    override object ValParam extends ModValParamCompanion
    final case class VarParam() extends Mod
    override object VarParam extends ModVarParamCompanion
    final case class Inline() extends Mod
    override object Inline extends ModInlineCompanion
  }

  sealed trait Enumerator extends Tree
  override object Enumerator extends EnumeratorCompanion {
    final case class Generator(pat: Pat, rhs: Term) extends Enumerator
    override object Generator extends EnumeratorGeneratorCompanion
    final case class Val(pat: Pat, rhs: Term) extends Enumerator
    override object Val extends EnumeratorValCompanion
    final case class Guard(cond: Term) extends Enumerator
    override object Guard extends EnumeratorGuardCompanion
  }

  type Import = g.Import

  final case class Importer(ref: Term.Ref, importees: List[Importee]) extends Tree
  override object Importer extends ImporterCompanion

  sealed trait Importee extends Ref
  override object Importee extends ImporteeCompanion {
    final case class Wildcard() extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = g.nme.WILDCARD
    }
    override object Wildcard extends ImporteeWildcardCompanion
    final case class Name(mname: self.Name) extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = mname.name
    }
    override object Name extends ImporteeNameCompanion
    final case class Rename(mname: self.Name, mrename: self.Name) extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = mrename.name
    }
    override object Rename extends ImporteeRenameCompanion
    final case class Unimport(mname: self.Name) extends Importee {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = g.nme.WILDCARD
    }
    override object Unimport extends ImporteeUnimportCompanion
  }

  type Case = g.CaseDef

  type Source = g.PackageDef
}