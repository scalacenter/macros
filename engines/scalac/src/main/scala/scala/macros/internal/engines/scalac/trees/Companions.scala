package scala.macros.internal
package engines.scalac
package trees

trait Companions extends scala.macros.trees.Companions { self: Universe =>
  override object treeCompanions extends super.TreeCompanions {
    trait InitCompanion extends super.InitCompanion {
      def unapply(tree: Init): Option[(Type, Name, List[List[Term]])]

      def unapply(tree: Any): Option[(Type, Name, List[List[Term]])] = tree match {
        case tree: Init => unapply(tree)
        case _ => None
      }
    }

    trait SelfCompanion extends super.SelfCompanion {
      def unapply(tree: Self): Option[(Term.Name, Option[Type])]

      def unapply(tree: Any): Option[(Term.Name, Option[Type])] = tree match {
        case tree: Self => unapply(tree)
        case _ => None
      }
    }

    trait ModAnnotCompanion extends super.ModAnnotCompanion {
      def unapply(tree: Mod.Annot): Option[Init]

      def unapply(tree: Any): Option[Init] = tree match {
        case tree: Mod.Annot => unapply(tree)
        case _ => None
      }
    }

    trait ModPrivateCompanion extends super.ModPrivateCompanion {
      def unapply(tree: Mod.Private): Option[Ref]

      def unapply(tree: Any): Option[Ref] = tree match {
        case tree: Mod.Private => unapply(tree)
        case _ => None
      }
    }

    trait ModProtectedCompanion extends super.ModProtectedCompanion {
      def unapply(tree: Mod.Protected): Option[Ref]

      def unapply(tree: Any): Option[Ref] = tree match {
        case tree: Mod.Protected => unapply(tree)
        case _ => None
      }
    }

    trait ModImplicitCompanion extends super.ModImplicitCompanion {
      def unapply(tree: Mod.Implicit): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Implicit => unapply(tree)
        case _ => false
      }
    }

    trait ModFinalCompanion extends super.ModFinalCompanion {
      def unapply(tree: Mod.Final): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Final => unapply(tree)
        case _ => false
      }
    }

    trait ModSealedCompanion extends super.ModSealedCompanion {
      def unapply(tree: Mod.Sealed): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Sealed => unapply(tree)
        case _ => false
      }
    }

    trait ModOverrideCompanion extends super.ModOverrideCompanion {
      def unapply(tree: Mod.Override): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Override => unapply(tree)
        case _ => false
      }
    }

    trait ModCaseCompanion extends super.ModCaseCompanion {
      def unapply(tree: Mod.Case): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Case => unapply(tree)
        case _ => false
      }
    }

    trait ModAbstractCompanion extends super.ModAbstractCompanion {
      def unapply(tree: Mod.Abstract): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Abstract => unapply(tree)
        case _ => false
      }
    }

    trait ModCovariantCompanion extends super.ModCovariantCompanion {
      def unapply(tree: Mod.Covariant): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Covariant => unapply(tree)
        case _ => false
      }
    }

    trait ModContravariantCompanion extends super.ModContravariantCompanion {
      def unapply(tree: Mod.Contravariant): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Contravariant => unapply(tree)
        case _ => false
      }
    }

    trait ModLazyCompanion extends super.ModLazyCompanion {
      def unapply(tree: Mod.Lazy): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Lazy => unapply(tree)
        case _ => false
      }
    }

    trait ModValParamCompanion extends super.ModValParamCompanion {
      def unapply(tree: Mod.ValParam): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.ValParam => unapply(tree)
        case _ => false
      }
    }

    trait ModVarParamCompanion extends super.ModVarParamCompanion {
      def unapply(tree: Mod.VarParam): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.VarParam => unapply(tree)
        case _ => false
      }
    }

    trait ModInlineCompanion extends super.ModInlineCompanion {
      def unapply(tree: Mod.Inline): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Mod.Inline => unapply(tree)
        case _ => false
      }
    }

    trait EnumeratorGeneratorCompanion extends super.EnumeratorGeneratorCompanion {
      def unapply(tree: Enumerator.Generator): Option[(Pat, Term)]

      def unapply(tree: Any): Option[(Pat, Term)] = tree match {
        case tree: Enumerator.Generator => unapply(tree)
        case _ => None
      }
    }

    trait EnumeratorValCompanion extends super.EnumeratorValCompanion {
      def unapply(tree: Enumerator.Val): Option[(Pat, Term)]

      def unapply(tree: Any): Option[(Pat, Term)] = tree match {
        case tree: Enumerator.Val => unapply(tree)
        case _ => None
      }
    }

    trait EnumeratorGuardCompanion extends super.EnumeratorGuardCompanion {
      def unapply(tree: Enumerator.Guard): Option[Term]

      def unapply(tree: Any): Option[Term] = tree match {
        case tree: Enumerator.Guard => unapply(tree)
        case _ => None
      }
    }

    trait ImporterCompanion extends super.ImporterCompanion {
      def unapply(tree: Importer): Option[(Term.Ref, List[Importee])]

      def unapply(tree: Any): Option[(Term.Ref, List[Importee])] = tree match {
        case tree: Importer => unapply(tree)
        case _ => None
      }
    }

    trait ImporteeWildcardCompanion extends super.ImporteeWildcardCompanion {
      def unapply(tree: Importee.Wildcard): Boolean

      def unapply(tree: Any): Boolean = tree match {
        case tree: Importee.Wildcard => unapply(tree)
        case _ => false
      }
    }

    trait ImporteeNameCompanion extends super.ImporteeNameCompanion {
      def unapply(tree: Importee.Name): Option[Name]

      def unapply(tree: Any): Option[Name] = tree match {
        case tree: Importee.Name => unapply(tree)
        case _ => None
      }
    }

    trait ImporteeRenameCompanion extends super.ImporteeRenameCompanion {
      def unapply(tree: Importee.Rename): Option[(Name, Name)]

      def unapply(tree: Any): Option[(Name, Name)] = tree match {
        case tree: Importee.Rename => unapply(tree)
        case _ => None
      }
    }

    trait ImporteeUnimportCompanion extends super.ImporteeUnimportCompanion {
      def unapply(tree: Importee.Unimport): Option[Name]

      def unapply(tree: Any): Option[Name] = tree match {
        case tree: Importee.Unimport => unapply(tree)
        case _ => None
      }
    }
  }
}