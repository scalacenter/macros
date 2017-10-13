package scala.macros.internal.engines.scalac

import scala.macros.semantic.Flags
import scala.reflect.internal.{Flags => gf}
import scala.tools.nsc.Global
import scala.reflect.macros.contexts.Context

case class ScalacUniverse(g: Global) extends macros.core.Universe with Flags {
  case class Expansion(c: Context)
  case class Mirror(c: Context)

  // ========================
  // Semantic
  // ========================
  override type Symbol = g.Symbol
  override def symName(sym: Symbol)(implicit m: Mirror): Name =
    if (sym.isTerm) TermNameSymbol(sym)
    else TypeNameSymbol(sym)
  private def symFlags(sym0: Symbol): Long = {
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
  private def hasFlags(sym: Symbol, flags: Long) = {
    val symFlag = symFlags(sym)
    (symFlag & flags) == flags
  }
  case class Denotation(pre: g.Type, sym: g.Symbol) {
    final override def toString = s"$sym in $pre"
  }

  // ========================
  // Trees
  // ========================

  override type Tree = g.Tree
  override def treeStructure(tree: Tree): String = g.showRaw(tree)
  override def treeSyntax(tree: Tree): String = g.showCode(tree)

  override type Stat = g.Tree
  implicit class XtensionStats(stats: List[g.Tree]) {
    // NOTE(xeno-by): The methods below are supposed to take care of statement-level desugaring/resugaring.
    // For more information, see this code from the early days of scalahost:
    // https://github.com/xeno-by/scalahost/blob/4ca12dfa3f204b91efe2eba3e5991dbb6ea1879d/interface/src/main/scala/scala/meta/internal/hosts/scalac/converters/ToMtree.scala#L131-L284.
    // For even more information, check out this code from even earlier days:
    // TreeInfo.untypecheckedTemplBody (from the Scala compiler).
    def toGStats: List[g.Tree] = {
      // TODO: implement me
      stats
    }
    def toStats: List[g.Tree] = {
      // TODO: implement me
      stats
    }
  }
  override type Term = g.Tree
  type Ref = g.RefTree
  override type Name = c.Name
  implicit class XtensionGTermName(gtree: g.SymTree with g.NameTree) {
    def toTermName: TermName = TermName(gtree.name.decoded).copyAttrs(gtree)
  }
  implicit class XtensionTermName(tree: TermName) {
    // TODO(olafur) attribute name with same type as parent select.
    def toGTermName: g.TermName = tree.name.toTermName
  }
  implicit class XtensionTypeName(tree: TypeName) {
    def toGTypeName: g.TypeName = tree.name.toTypeName
  }
  override def nameValue(name: Name): String = name.value
  override def Name(value: String): Name =
    if (value.isEmpty) c.NameAnonymous()
    else c.NameIndeterminate(value)
  type TermRef = g.Tree

  override type TermName = c.TermName
  override def TermName(value: String): TermName =
    new c.TermName(value)
  override def TermNameSymbol(sym: Symbol)(implicit m: Mirror): TermName =
    TermName(sym.name.decoded).setSymbol(sym)
  override def TermNameUnapply(arg: Any): Option[String] = arg match {
    case t: c.TermName => Some(t.value)
    case _ => None
  }

  override def TermSelect(qual: Term, name: TermName): Term =
    g.Select(qual, name.toGTermName)
  override def TermSelectUnapply(arg: Any): Option[(TermRef, TermName)] = arg match {
    case g.Select(qual, name) => Some(qual -> new c.TermName(name.decoded))
    case _ => None
  }

  override def TermApply(fun: Term, args: List[Term]): Term =
    g.Apply(fun, args)
  override def TermApplyUnapply(arg: Any): Option[(Term, List[Term])] = arg match {
    case g.Apply(fun, args) => Some(fun -> args)
    case _ => None
  }

  override def TermApplyType(fun: Term, targs: List[Type]): Term =
    g.TypeApply(fun, targs)

  override def TermNew(init: Init): Term =
    g.New(init.mtpe, init.argss).setPos(init.pos)
  implicit class XtensionInit(tree: Init) {
    def toGParent: g.Tree = {
      g.build.SyntacticApplied(tree.mtpe, tree.argss).setPos(tree.pos)
    }
    def toGNew: g.Tree = {
      g.New(tree.mtpe, tree.argss).setPos(tree.pos)
    }
  }

  override def TermBlock(stats: List[Stat]): Term =
    g.gen.mkBlock(stats.toGStats)

  override type TermParam = g.ValDef
  override def TermParam(
      mods: List[Mod],
      name: Name,
      decltpe: Option[Type],
      default: Option[Term]
  ): TermParam = {
    val gname = name match {
      case name: c.TermName => name.toGTermName
      case _ => g.nme.WILDCARD
    }
    val gtpt = decltpe.getOrElse(g.TypeTree())
    val gdefault = default.getOrElse(g.EmptyTree)
    g.ValDef(mods.toGModifiers | gf.PARAM, gname, gtpt, gdefault)
  }

  // =====
  // Types
  // =====
  override type TypeName = c.TypeName
  override def TypeName(value: String): TypeName =
    new c.TypeName(value)
  override def TypeNameSymbol(sym: Symbol)(implicit m: Mirror): TypeName =
    TypeName(sym.name.decoded).setSymbol(sym)

  override def TypeSelect(qual: TermRef, name: TypeName): Type =
    g.Select(qual, name.toGTypeName)
  override def TypeApply(tpe: Type, targs: List[Type]): Type =
    g.AppliedTypeTree(tpe, targs)

  type Pat = g.Tree
  type PatVar = g.Bind
  override def PatVar(name: c.TermName): PatVar =
    g.Bind(name.toGTermName, g.Ident(g.nme.WILDCARD))

  // =====
  // Lit
  // =====
  override type Lit = g.Literal
  override def LitString(value: String): Lit = g.Literal(g.Constant(value))

  // =====
  // Defn
  // =====
  override type Defn = g.Tree
  override def DefnVal(
      mods: List[Mod],
      pats: List[Pat],
      decltpe: Option[Type],
      rhs: Term
  ): Defn = {
    pats match {
      case List(name @ g.Ident(_: g.TermName)) =>
        val cname: TermName = name.toTermName
        DefnVal(mods, List(PatVar(cname).setPos(name.pos)), decltpe, rhs)
      case List(bind: g.Bind) =>
        val name = bind.toTermName
        g.ValDef(mods.toGModifiers, name.toGTermName, decltpe.getOrElse(g.TypeTree()), rhs)
      case _ =>
        ???
    }
  }
  override def DefnDef(
      mods: List[Mod],
      name: TermName,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[Type],
      body: Term
  ): Defn = {
    val gparamss = paramss // TODO: view bounds, context bounds
    val gtpt = decltpe.getOrElse(g.TypeTree())
    g.DefDef(mods.toGModifiers, name.toGTermName, tparams, gparamss, gtpt, body)
  }
  override def DefnObject(
      mods: List[Mod],
      name: TermName,
      templ: Template
  ): Defn =
    g.ModuleDef(mods.toGModifiers, name.toGTermName, templ.toGTemplate(g.Modifiers(), Nil))

  // ========
  // Template
  // ========
  override type Template = c.Template
  implicit class XtensionTemplate(tree: Template) {
    def toGTemplate(gctorMods: g.Modifiers, gctorParamss: List[List[g.ValDef]]): g.Template = {
      val gearly = tree.early.toGStats
      val gparents = tree.inits.map(_.toGParent)
      val gself = tree.self.toGSelf
      val gstats = gearly ++ tree.stats.toGStats
      g.gen.mkTemplate(gparents, gself, gctorMods, gctorParamss, gstats).setPos(tree.pos)
    }
  }
  override def Template(
      inits: List[Init],
      self: Self,
      stats: List[Stat]
  ): Template =
    c.Template(Nil, inits, self, stats)
  override type Init = c.Init
  override def Init(tpe: g.Tree, name: c.Name, argss: List[List[g.Tree]]): c.Init =
    c.Init(tpe, name, argss)
  override type Self = c.Self
  override def Self(name: Name, decltpe: Option[Type]): Self =
    c.Self(name, decltpe)
  implicit class XtensionSelf(self: Self) {
    def toGSelf: g.ValDef = {
      val gname = self.mname match {
        case name: c.TermName => name.toGTermName
        case _ => g.nme.WILDCARD
      }
      val gmods = g.Modifiers(gf.PRIVATE)
      val gtpt = self.decltpe.getOrElse(g.TypeTree())
      g.ValDef(gmods, gname, gtpt, g.EmptyTree).setPos(self.pos)
    }
  }

  // =====
  // Types
  // =====
  override type TypeParam = g.TypeDef
  def TypeParam(
      mods: List[Mod],
      name: Name,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[Type],
      cbounds: List[Type]
  ): TypeParam = ???
  override type Type = g.Tree
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

  override def caseFields(tpe: Type)(implicit m: Mirror): List[Denotation] =
    typeMembers(tpe, sym => hasFlags(sym, CASE | VAL))

  override def denotName(denot: Denotation)(implicit m: Mirror): Name = symName(denot.sym)
  override def denotSym(denot: Denotation)(implicit m: Mirror): Symbol = denot.sym
  override def denotInfo(denot: Denotation)(implicit m: Mirror): Type =
    denot.pre.memberInfo(denot.sym).toType

  def typeMembers(tpe: Type, f0: Symbol => Boolean)(implicit m: Mirror): List[Denotation] = {
    val f1: Symbol => Boolean = sym => f0(sym) && !sym.name.endsWith(g.nme.LOCAL_SUFFIX_STRING)
    tpe.toGType.members.sorted.withFilter(f1).map(sym => Denotation(tpe.toGType, sym))
  }

  def typeMembers(tpe: Type, name: String, f: Symbol => Boolean)(
      implicit m: Mirror
  ): List[Denotation] = {
    // TODO. Leveraging tpe.members(Name) may be more efficient.
    typeMembers(tpe, sym => f(sym) && sym.name.decoded == name)
  }

  type Mod = g.Tree
  implicit class XtensionMods(mods: List[Mod]) {
    def toGModifiers: g.Modifiers = {
      // TODO: implement me
      g.Modifiers()
    }
  }

  // ==========
  // Extensions
  // ==========

  // ========================
  // Custom trees
  // ========================
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

    case class NameAnonymous() extends Name {
      def value: String = ""
    }

    case class NameIndeterminate(value: String) extends Name

    // TODO: TermName and TypeName are special in the sense that the are only custom trees
    // that are stats, i.e. they can be returned from macros.
    // As a result, we can't define them as completely unrelated classes, because then
    // scalac will be confused should the metaprogrammer decided to return those trees in expansion.
    // Luckily, not only g.Tree is not sealed, but also g.Ident is not final.

    class TermName(val value: String) extends g.Ident(g.TermName(value).encode) with Name {
      override def qualifier: g.Tree = super[Name].qualifier
      override val name: g.Name = super[Name].name
    }

    class TypeName(val value: String) extends g.Ident(g.TypeName(value).encode) with Name {
      override def qualifier: g.Tree = super[Name].qualifier
      override val name: g.Name = super[Name].name
    }

    case class Init(mtpe: Type, mname: Name, argss: List[List[Term]]) extends g.RefTree {
      def qualifier: g.Tree = g.EmptyTree
      def name: g.Name = mname.name
    }

    case class Self(mname: Name, decltpe: Option[Type]) extends g.DefTree {
      def name: g.Name = mname.name
    }

    case class Template(early: List[Stat], inits: List[Init], self: Self, stats: List[Stat])
        extends g.Tree

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

    case class ModMacro() extends Mod

    sealed trait Enumerator extends g.Tree

    case class EnumeratorGenerator(pat: Pat, rhs: Term) extends Enumerator

    case class EnumeratorVal(pat: Pat, rhs: Term) extends Enumerator

    case class EnumeratorGuard(cond: Term) extends Enumerator

    case class Importer(ref: TermRef, importees: List[Importee]) extends g.Tree

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
