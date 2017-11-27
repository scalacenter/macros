package scala.macros.internal.engines.scalac

import java.nio.file.Path
import scala.reflect.internal.util.SourceFile
import scala.reflect.internal.{Flags => gf}
import scala.reflect.macros.contexts.Context

case class ScalacUniverse(ctx: Context) extends macros.core.Universe with Flags {
  self =>
  import ctx.universe._

  // ========================
  // Trees
  // ========================
  override type Tree = ctx.universe.Tree
  override type Splice = ctx.universe.Tree
  override type Stat = ctx.universe.Tree
  override type Term = ctx.universe.Tree
  type Ref = ctx.universe.RefTree
  override type TermParam = ctx.universe.ValDef
  type Pat = ctx.universe.Tree

  override def treeStructure(tree: Tree): String = ctx.universe.showRaw(tree)
  override def treeSyntax(tree: Tree): String = ctx.universe.showCode(tree)
  override def treePosition(tree: Tree): Position = Position(tree.pos)

  def fresh(prefix: String): String =
    ctx.universe.freshTermName(prefix)(ctx.universe.globalFreshNameCreator).toString

  implicit class XtensionStats(stats: List[ctx.universe.Tree]) {
    // NOTE(xeno-by): The methods below are supposed to take care of statement-level desugaring/resugaring.
    // For more information, see this code from the early days of scalahost:
    // https://github.com/xeno-by/scalahost/blob/4ca12dfa3f204b91efe2eba3e5991dbb6ea1879d/interface/src/main/scala/scala/meta/internal/hosts/scalac/converters/ToMtree.scala#L131-L284.
    // For even more information, check out this code from even earlier days:
    // TreeInfo.untypecheckedTemplBody (from the Scala compiler).
    def toGStats: List[ctx.universe.Tree] = {
      // TODO: implement me
      stats
    }
    def toStats: List[ctx.universe.Tree] = {
      // TODO: implement me
      stats
    }
  }
  implicit class XtensionGTermName(gtree: ctx.universe.SymTree with ctx.universe.NameTree) {
    def toTermName: Term = TermName(gtree.name.decoded).copyAttrs(gtree)
  }

  override def Splice(term: typed.Tree): Splice = term //untpd.TypedSplice(term)

  override def TermName(value: String): Term =
    new c.TermName(value)

  override def TermSelect(qual: Term, name: String): Term =
    ctx.universe.Select(qual, name)

  override def TermApply(fun: Term, args: List[Term]): Term =
    ctx.universe.Apply(fun, args)

  override def TermApplyType(fun: Term, targs: List[TypeTree]): Term =
    ctx.universe.TypeApply(fun, targs)

  override def TermNew(init: Init): Term =
    ctx.universe.New(init.mtpe, init.argss).setPos(init.pos)

  override def TermIf(cond: Term, truep: Term, elsep: Term): Term =
    ctx.universe.If(cond, truep, elsep)

  override def LitNull: Lit = ctx.universe.Literal(ctx.universe.Constant(null))

  implicit class XtensionInit(tree: Init) {
    def toGParent: ctx.universe.Tree = {
      ctx.universe.build.SyntacticApplied(tree.mtpe, tree.argss).setPos(tree.pos)
    }
    def toGNew: ctx.universe.Tree = {
      ctx.universe.New(tree.mtpe, tree.argss).setPos(tree.pos)
    }
  }

  override def TermBlock(stats: List[Stat]): Term =
    ctx.universe.gen.mkBlock(stats.toGStats)

  override def TermParam(
      mods: List[Mod],
      name: String,
      decltpe: Option[TypeTree],
      default: Option[Term]
  ): TermParam = {
    val gname = ctx.universe.TermName(name)
    val gtpt = decltpe.getOrElse(ctx.universe.TypeTree())
    val gdefault = default.getOrElse(ctx.universe.EmptyTree)
    ctx.universe.ValDef(mods.toGModifiers | gf.PARAM, gname, gtpt, gdefault)
  }

  // ===========
  // Typed trees
  // ===========
  object typed extends typedApi {
    type Tree = ctx.universe.Tree
    type Term = ctx.universe.Tree
    type Def = ctx.universe.Tree

    def treePosition(tree: Tree): Position = Position(tree.pos)
    def treeSyntax(tree: Tree): String = self.treeSyntax(tree)
    def treeStructure(tree: Tree): String = self.treeStructure(tree)

    def symOf(tree: Def): Symbol = tree.symbol
    def typeOf(tree: Term): Type = tree.tpe
    def ref(sym: Symbol): Term = {
      // TODO(olafur) is .setType(sym.tpe) correct?
      new c.TermName(sym.name.decoded).setSymbol(sym).setType(sym.tpe)
    }
  }

  // =====
  // Lit
  // =====
  override type Lit = ctx.universe.Literal
  override def LitString(value: String): Lit = ctx.universe.Literal(ctx.universe.Constant(value))
  override def LitInt(value: Int): Lit = ctx.universe.Literal(ctx.universe.Constant(value))

  // =====
  // Defn
  // =====
  override type Defn = ctx.universe.Tree
  override def DefnVal(
      mods: List[Mod],
      name: String,
      decltpe: Option[TypeTree],
      rhs: Term
  ): Defn = {
    val gname = ctx.universe.TermName(name)
    ctx.universe.ValDef(mods.toGModifiers, gname, decltpe.getOrElse(ctx.universe.TypeTree()), rhs)
  }
  override def DefnDef(
      mods: List[Mod],
      name: String,
      tparams: List[TypeParam],
      paramss: List[List[TermParam]],
      decltpe: Option[TypeTree],
      body: Term
  ): Defn = {
    val gparamss = paramss // TODO: view bounds, context bounds
    val gtpt = decltpe.getOrElse(ctx.universe.TypeTree())
    val gname = ctx.universe.TermName(name)
    ctx.universe.DefDef(mods.toGModifiers, gname, tparams, gparamss, gtpt, body)
  }
  override def DefnObject(
      mods: List[Mod],
      name: String,
      templ: Template
  ): Defn = {
    val gname = ctx.universe.TermName(name)
    ctx.universe.ModuleDef(
      mods.toGModifiers,
      gname,
      templ.toGTemplate(ctx.universe.Modifiers(), Nil)
    )
  }

  // ========
  // Template
  // ========
  override type Template = c.Template
  implicit class XtensionTemplate(tree: Template) {
    def toGTemplate(
        gctorMods: ctx.universe.Modifiers,
        gctorParamss: List[List[ctx.universe.ValDef]]
    ): ctx.universe.Template = {
      val gearly = tree.early.toGStats
      val gparents = tree.inits.map(_.toGParent)
      val gself = tree.self.toGSelf
      val gstats = gearly ++ tree.stats.toGStats
      ctx.universe.gen.mkTemplate(gparents, gself, gctorMods, gctorParamss, gstats).setPos(tree.pos)
    }
  }
  override def Template(
      inits: List[Init],
      self: Self,
      stats: List[Stat]
  ): Template =
    c.Template(Nil, inits, self, stats)
  override type Init = c.Init
  override def Init(tpe: ctx.universe.Tree, argss: List[List[ctx.universe.Tree]]): c.Init =
    c.Init(tpe, c.NameAnonymous(), argss)
  override type Self = c.Self
  override def Self(name: String, decltpe: Option[TypeTree]): Self =
    c.Self(new c.TermName(name), decltpe)
  implicit class XtensionSelf(self: Self) {
    def toGSelf: ctx.universe.ValDef = {
      val gname = self.mname match {
        case name: c.TermName => name.name.toTermName
        case _ => ctx.universe.nme.WILDCARD
      }
      val gmods = ctx.universe.Modifiers(gf.PRIVATE)
      val gtpt = self.decltpe.getOrElse(ctx.universe.TypeTree())
      ctx.universe.ValDef(gmods, gname, gtpt, ctx.universe.EmptyTree).setPos(self.pos)
    }
  }

  // =====
  // Types
  // =====
  override type Type = ctx.universe.Type
  override type WeakTypeTag[T] = ctx.universe.WeakTypeTag[T]
  override type TypeTree = ctx.universe.Tree
  override type TypeParam = ctx.universe.TypeDef

  override def weakTypeTagType[T](tt: ctx.universe.WeakTypeTag[T]): Type =
    tt.tpe
  override def TypeName(value: String): TypeTree =
    new c.TypeName(value)
  override def TypeSelect(qual: Term, name: String): TypeTree =
    ctx.universe.Select(qual, ctx.universe.TypeName(name))
  override def TypeApply(tpe: TypeTree, targs: List[TypeTree]): TypeTree =
    ctx.universe.AppliedTypeTree(tpe, targs)
  override def TypeParam(
      mods: List[Mod],
      name: String,
      tparams: List[TypeParam],
      tbounds: TypeBounds,
      vbounds: List[TypeTree],
      cbounds: List[TypeTree]
  ): TypeParam = ???

  override def caseFields(tpe: Type): List[Denotation] =
    tpe.typeSymbol.caseFieldAccessors.map(sym => Denotation(tpe, sym))
  override def typeRef(path: String): Type = {
    // TODO(olafur) this will crash when path is not a class.
    val sym = ctx.mirror.staticClass(path)
    ctx.universe.TypeRef(ctx.universe.NoPrefix, sym, Nil)
  }
  override def appliedType(tp: Type, args: List[Type]): Type = {
    ctx.universe.TypeRef(NoPrefix, tp.typeSymbol, args)
  }
  override def typeTreeOf(tp: Type): TypeTree =
    ctx.universe.TypeTree(tp)

  override def denotSym(denot: Denotation): Symbol = denot.sym
  override def denotInfo(denot: Denotation): Type = {
    val result = denot.pre.memberInfo(denot.sym)
    result match {
      // HACK(olafur): info for case fields return their nullary method type,
      // here we unwrap the result type of the nullary method type to hide
      // away this detail from users.
      case NullaryMethodType(tpe) if denot.sym.isCaseAccessor => tpe
      case _ => result
    }
  }

  def typeMembers(tpe: Type, f0: Symbol => Boolean): List[Denotation] = {
    val f1: Symbol => Boolean = sym =>
      f0(sym) && !sym.name.endsWith(ctx.universe.nme.LOCAL_SUFFIX_STRING)
    tpe.members.sorted.withFilter(f1).map(sym => Denotation(tpe, sym))
  }

  def typeMembers(tpe: Type, name: String, f: Symbol => Boolean): List[Denotation] = {
    // TODO. Leveraging tpe.members(Name) may be more efficient.
    typeMembers(tpe, sym => f(sym) && sym.name.decoded == name)
  }

  // ====
  // Mods
  // ====
  type Mod = ctx.universe.Tree
  implicit class XtensionMods(mods: List[Mod]) {
    def toGModifiers: ctx.universe.Modifiers = {
      // TODO: implement me
      ctx.universe.Modifiers()
    }
  }

  // =========
  // Expansion
  // =========
  case class Expansion(c: Context)
  case class Input(underlying: SourceFile) extends macros.core.Input {
    def path: Path = underlying.file.file.toPath
  }
  case class Position(underlying: ctx.universe.Position) extends macros.core.Position {
    override def input: Input = Input(underlying.source)
    override def line: Int = underlying.line
  }
  override def enclosingPosition: Position = Position(ctx.enclosingPosition)
  override def enclosingOwner: ctx.universe.Symbol =
    ctx.internal.enclosingOwner
  case class Mirror(c: Context)
  // ========================
  // Semantic
  // ========================
  override type Symbol = ctx.universe.Symbol
  override def root: Symbol = ctx.universe.rootMirror.RootClass
  override def symOwner(sym: Symbol): Option[Symbol] = {
    val owner = sym.owner
    if (owner == NoSymbol) None
    else Some(owner)
  }
  override def symName(sym: Symbol): String =
    sym.name.decoded
  private def symFlags(sym0: Symbol): Long = {
    val sym = {
      if (sym0.isModuleClass) sym0.asClass.module
      else if (sym0.isTypeSkolem) sym0.deSkolemize
      else sym0.setterIn(sym0.owner).orElse(sym0.getterIn(sym0.owner).orElse(sym0))
    }

    def has(flag: Long): Boolean = sym.hasFlag(flag)
    val isObject = sym.isModule && !has(gf.PACKAGE) && sym.name != ctx.universe.nme.PACKAGE
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
      if (sym.isModule && sym.name == ctx.universe.nme.PACKAGE) flags |= PACKAGEOBJECT
      if (sym.isClass && !has(gf.TRAIT)) flags |= CLASS
      if (sym.isClass && has(gf.TRAIT)) flags |= TRAIT
      if (maybeValOrVar && (has(gf.MUTABLE) || ctx.universe.nme.isSetterName(sym.name)))
        flags |= VAR
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
        if (sym.hasAccessBoundary && gpriv != ctx.universe.NoSymbol && !has(gf.PROTECTED))
          flags |= PRIVATE
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
  case class Denotation(pre: ctx.universe.Type, sym: ctx.universe.Symbol) {
    final override def toString = s"$sym in $pre"
  }

  // ========================
  // Custom trees
  // ========================
  val c: customTrees.type = customTrees
  object customTrees {
    type TypedSplice
    sealed trait Name extends ctx.universe.RefTree {
      def value: String
      def qualifier: ctx.universe.Tree = ctx.universe.EmptyTree
      def name: ctx.universe.Name = {
        if (this.isInstanceOf[TypeName]) ctx.universe.TypeName(value).encode
        else ctx.universe.TermName(value).encode
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
    // Luckily, not only ctx.universe.Tree is not sealed, but also ctx.universe.Ident is not final.

    class TermName(val value: String)
        extends ctx.universe.Ident(ctx.universe.TermName(value).encode)
        with Name {
      override def qualifier: ctx.universe.Tree = super[Name].qualifier
      override val name: ctx.universe.Name = super[Name].name
    }

    class TypeName(val value: String)
        extends ctx.universe.Ident(ctx.universe.TypeName(value).encode)
        with Name {
      override def qualifier: ctx.universe.Tree = super[Name].qualifier
      override val name: ctx.universe.Name = super[Name].name
    }

    case class Init(mtpe: TypeTree, mname: Name, argss: List[List[Term]])
        extends ctx.universe.RefTree {
      def qualifier: ctx.universe.Tree = ctx.universe.EmptyTree
      def name: ctx.universe.Name = mname.name
    }

    case class Self(mname: Name, decltpe: Option[TypeTree]) extends ctx.universe.DefTree {
      def name: ctx.universe.Name = mname.name
    }

    case class Template(early: List[Stat], inits: List[Init], self: Self, stats: List[Stat])
        extends ctx.universe.Tree

    sealed trait Mod extends ctx.universe.Tree

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

    sealed trait Enumerator extends ctx.universe.Tree

    case class EnumeratorGenerator(pat: Pat, rhs: Term) extends Enumerator

    case class EnumeratorVal(pat: Pat, rhs: Term) extends Enumerator

    case class EnumeratorGuard(cond: Term) extends Enumerator

    case class Importer(ref: ctx.universe.Tree, importees: List[Importee]) extends ctx.universe.Tree

    sealed trait Importee extends ctx.universe.RefTree

    case class ImporteeWildcard() extends Importee {
      def qualifier: ctx.universe.Tree = ctx.universe.EmptyTree
      def name: ctx.universe.Name = ctx.universe.nme.WILDCARD
    }

    case class ImporteeName(mname: Name) extends Importee {
      def qualifier: ctx.universe.Tree = ctx.universe.EmptyTree
      def name: ctx.universe.Name = mname.name
    }

    case class ImporteeRename(mname: Name, mrename: Name) extends Importee {
      def qualifier: ctx.universe.Tree = ctx.universe.EmptyTree
      def name: ctx.universe.Name = mrename.name
    }

    case class ImporteeUnimport(mname: Name) extends Importee {
      def qualifier: ctx.universe.Tree = ctx.universe.EmptyTree
      def name: ctx.universe.Name = ctx.universe.nme.WILDCARD
    }
  }
}
