package scala.macros.internal
package engines.scalac
package semantic

import scala.reflect.macros.contexts.Context
import scala.reflect.internal.{Flags => gf}
import scala.macros.internal.prettyprinters._

trait Abstracts extends scala.macros.semantic.Mirrors { self: Universe =>
  case class Mirror(c: Context)

  trait MirrorAbstracts extends super.MirrorAbstracts {
    def refDenot(ref: Ref)(implicit m: Mirror): Denotation = {
      require(ref.tpe != null, "can't call semantic APIs on unattributed trees")
      require(ref.qualifier.tpe != null, "can't call semantic APIs on unattributed trees")
      Denotation(ref.qualifier.tpe, ref.symbol)
    }

    def termTpe(term: Term)(implicit m: Mirror): Type = {
      require(term.tpe != null, "can't call semantic APIs on unattributed trees")
      term.tpe.toType
    }

    def sym(id: String)(implicit m: Mirror): Symbol = {
      ???
    }

    def symSyntax(p: Prettyprinter, sym: Symbol)(implicit m: Mirror): Unit = {
      p.raw(sym.toString)
    }

    def symStructure(p: Prettyprinter, sym: Symbol)(implicit m: Mirror): Unit = {
      ???
    }

    def symName(sym: Symbol)(implicit m: Mirror): Name = {
      if (sym.isTerm) Term.Name(sym) else Type.Name(sym)
    }

    def symFlags(sym0: Symbol)(implicit m: Mirror): Long = {
      val sym = {
        if (sym0.isModuleClass) sym0.asClass.module
        else if (sym0.isTypeSkolem) sym0.deSkolemize
        else sym0.setterIn(sym0.owner).orElse(sym0.getterIn(sym0.owner).orElse(sym0))
      }

      def has(flag: Long): Boolean = sym.hasFlag(flag)
      val isObject = sym.isModule && !has(gf.PACKAGE) && sym.name != g.nme.PACKAGE
      val isAccessor = has(gf.ACCESSOR) || has(gf.PARAMACCESSOR)

      val definitionFlags = {
        var flags = 0L
        def maybeValOrVar = (sym.isTerm && flags == 0L) || (has(gf.PARAMACCESSOR) && flags == PARAM)
        if (sym.isMethod && !sym.isConstructor && !has(gf.MACRO) && !isAccessor) flags |= DEF
        if (sym.isPrimaryConstructor) flags |= PRIMARYCTOR
        if (sym.isConstructor && !sym.isPrimaryConstructor) flags |= SECONDARYCTOR
        if (has(gf.MACRO)) flags |= MACRO
        if (sym.isType && !sym.isClass && !has(gf.PARAM)) flags |= TYPE
        if (sym.isTerm && (has(gf.PARAM) || has(gf.PARAMACCESSOR))) flags |= PARAM
        if (sym.isType && has(gf.PARAM)) flags |= TYPEPARAM
        if (isObject) flags |= OBJECT
        if (has(gf.PACKAGE)) flags |= PACKAGE
        if (sym.isModule && sym.name == g.nme.PACKAGE) flags |= PACKAGEOBJECT
        if (sym.isClass && !has(gf.TRAIT)) flags |= CLASS
        if (sym.isClass && has(gf.TRAIT)) flags |= TRAIT
        if (maybeValOrVar && (has(gf.MUTABLE) || g.nme.isSetterName(sym.name))) flags |= VAR
        if (maybeValOrVar && !(has(gf.LOCAL) && has(gf.PARAMACCESSOR))) flags |= VAL
        flags
      }

      val accessQualifierFlags = {
        var flags = 0L
        val gpriv = sym.privateWithin.orElse(sym.owner)
        if (has(gf.SYNTHETIC) && has(gf.ARTIFACT)) {
          // NOTE: Artifact vals produced by mkPatDef can be private to method.
          // I've no idea what this means, so here we just ignore such vals.
        } else {
          if (has(gf.PROTECTED)) flags |= PROTECTED
          if (has(gf.PRIVATE) && !has(gf.PARAMACCESSOR)) flags |= PRIVATE
          // TODO: `private[pkg] class C` doesn't have PRIVATE in its flags,
          // so we need to account for that!
          if (sym.hasAccessBoundary && gpriv != g.NoSymbol && !has(gf.PROTECTED)) flags |= PRIVATE
        }
        flags
      }

      val otherFlags = {
        var flags = 0L
        val isDeclaredDeferred = has(gf.DEFERRED) && !has(gf.PARAM)
        val isDeclaredAbstract = (has(gf.ABSTRACT) && !has(gf.TRAIT)) || has(gf.ABSOVERRIDE)
        if (isDeclaredDeferred || isDeclaredAbstract) flags |= ABSTRACT
        if ((has(gf.FINAL) && !has(gf.PACKAGE)) || isObject) flags |= FINAL
        if (has(gf.SEALED)) flags |= SEALED
        if (has(gf.IMPLICIT)) flags |= IMPLICIT
        if (has(gf.LAZY)) flags |= LAZY
        if (has(gf.CASE) || has(gf.CASEACCESSOR)) flags |= CASE
        if (sym.isType && has(gf.CONTRAVARIANT)) flags |= CONTRAVARIANT
        if (sym.isType && has(gf.COVARIANT)) flags |= COVARIANT
        // TODO: MACRO
        flags
      }

      definitionFlags | accessQualifierFlags | otherFlags
    }

    def symAnnots(sym: Symbol)(implicit m: Mirror): List[Init] = {
      ???
    }

    def symWithin(sym: Symbol)(implicit m: Mirror): Symbol = {
      ???
    }

    def symDenot(sym: Symbol)(implicit m: Mirror): Denotation = {
      symDenot(sym, sym.owner.thisType.toType)
    }

    def symDenot(sym: Symbol, pre: Type)(implicit m: Mirror): Denotation = {
      Denotation(pre.toGType, sym)
    }

    def symMembers(sym: Symbol, f: SymFilter)(implicit m: Mirror): List[Symbol] = {
      typeMembers(sym.info.toType, f).map(_.sym)
    }

    def symMembers(sym: Symbol, name: String, f: SymFilter)(implicit m: Mirror): List[Symbol] = {
      typeMembers(sym.info.toType, name, f).map(_.sym)
    }

    def denotSyntax(p: Prettyprinter, denot: Denotation)(implicit m: Mirror): Unit = {
      // TODO: Change .raw(pre.toString) to .stx(pre)
      p.stx(denot.sym).raw(" in ").raw(denot.pre.toString)
    }

    def denotStructure(p: Prettyprinter, denot: Denotation)(implicit m: Mirror): Unit = {
      ???
    }

    def denotSym(denot: Denotation)(implicit m: Mirror): Symbol = {
      denot.sym
    }

    def denotInfo(denot: Denotation)(implicit m: Mirror): Type = {
      denot.pre.memberInfo(denot.sym).toType
    }

    def denotMembers(denot: Denotation, f: SymFilter)(implicit m: Mirror): List[Denotation] = {
      typeMembers(denotInfo(denot), f)
    }

    def denotMembers(denot: Denotation, name: String, f: SymFilter)(
        implicit m: Mirror
    ): List[Denotation] = {
      typeMembers(denotInfo(denot), name, f)
    }

    implicit class XtensionToType(gtpe: g.Type) {
      def toType: Type = {
        gtpe match {
          case g.NullaryMethodType(gtpe) => gtpe.toType
          case tpe => g.TypeTree(gtpe)
        }
      }
    }

    implicit class XtensionToGType(tpe: Type) {
      def toGType: g.Type = tpe match {
        case gtpt: g.TypeTree => gtpt.tpe
        case _ => ???
      }
    }

    def typeEqual(tpe1: Type, tpe2: Type)(implicit m: Mirror): Boolean = {
      tpe1.toGType =:= tpe2.toGType
    }

    def typeSubtype(tpe1: Type, tpe2: Type)(implicit m: Mirror): Boolean = {
      tpe1.toGType <:< tpe2.toGType
    }

    def typeWiden(tpe: Type)(implicit m: Mirror): Type = {
      tpe.toGType.widen.toType
    }

    def typeNarrow(tpe: Type)(implicit m: Mirror): Type = {
      tpe.toGType.narrow.toType
    }

    def caseFields(tpe: Type)(implicit m: Mirror): List[Denotation] =
      // NOTE(olafur) backend should not use api extension methods.
      tpe.vals.filter(_.isCase)

    def typeMembers(tpe: Type, f0: SymFilter)(implicit m: Mirror): List[Denotation] = {
      val f1: SymFilter = sym => f0(sym) && !sym.name.endsWith(g.nme.LOCAL_SUFFIX_STRING)
      tpe.toGType.members.sorted.filter(f1).map(sym => Denotation(tpe.toGType, sym)).toList
    }

    def typeMembers(tpe: Type, name: String, f: SymFilter)(implicit m: Mirror): List[Denotation] = {
      // TODO. Leveraging tpe.members(Name) may be more efficient.
      typeMembers(tpe, sym => f(sym) && sym.name.decoded == name)
    }

    def typeLub(tpes: List[Type])(implicit m: Mirror): Type = {
      g.lub(tpes.map(_.toGType)).toType
    }

    def typeGlb(tpes: List[Type])(implicit m: Mirror): Type = {
      g.glb(tpes.map(_.toGType)).toType
    }
  }
}
