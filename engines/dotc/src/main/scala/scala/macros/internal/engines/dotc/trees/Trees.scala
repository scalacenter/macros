package scala.macros.internal
package engines.dotc
package trees

// NOTE: Scalac ASTs aren't sufficiently strongly-typed to fully implement our API.
// As a result, we have to approximate with things like `type Term = g.Tree`.
// Additionally, we have to come up with new AST nodes to express things that
// aren't modeled as trees in Scalac. Luckily, Scalac's Tree class is not sealed.
trait Trees extends scala.macros.trees.Trees with Abstracts { self: Universe =>
  type Tree = g.Tree

  type Ref = g.RefTree
  type Stat = g.Tree
  type Name = c.Name
  type Lit = g.Literal

  type Term = g.Tree
  override val Term = TermCompanion
  object TermCompanion extends TermCompanion {
    type Ref = g.RefTree
    type Name = c.TermName
    type Param = g.ValDef
  }

  type Type = g.Tree
  override val Type = TypeCompanion
  object TypeCompanion extends TypeCompanion {
    type Ref = g.RefTree
    type Name = c.TypeName
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

  type Init = c.Init
  type Self = c.Self
  type Template = c.Template
  type Mod = c.Mod
  type Enumerator = c.Enumerator
  type Import = g.Import
  type Importer = c.Importer
  type Importee = c.Importee
  type Case = g.CaseDef
  type Source = g.PackageDef
}
