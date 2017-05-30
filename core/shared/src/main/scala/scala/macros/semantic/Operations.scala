package scala.macros
package semantic

private[scala] trait Operations { self: Universe =>
  private[scala] trait SymbolBasedOps extends FlagBasedOps {
    protected implicit def m: Mirror
    protected def sym: Symbol
    protected def flags: Long = abstracts.symFlags(sym)
    def name: Name = abstracts.symName(sym)
    // NOTE: isXXX operations inherited from FlagBasedOps
    def annots: List[Init] = abstracts.symAnnots(sym)
    def within: Symbol = abstracts.symWithin(sym)
    def denot: Denotation = abstracts.symDenot(sym)
    def denot(pre: Type): Denotation = abstracts.symDenot(sym, pre)
  }

  private[scala] trait FlagBasedOps {
    protected def flags: Long
    private def hasFlags(flags: Long) = (this.flags & flags) == flags
    def isVal: Boolean = hasFlags(VAL)
    def isVar: Boolean = hasFlags(VAR)
    def isDef: Boolean = hasFlags(DEF)
    def isPrimaryCtor: Boolean = hasFlags(PRIMARYCTOR)
    def isSecondaryCtor: Boolean = hasFlags(SECONDARYCTOR)
    def isMacro: Boolean = hasFlags(MACRO)
    def isType: Boolean = hasFlags(TYPE)
    def isParam: Boolean = hasFlags(PARAM)
    def isTypeParam: Boolean = hasFlags(TYPEPARAM)
    def isObject: Boolean = hasFlags(OBJECT)
    def isPackage: Boolean = hasFlags(PACKAGE)
    def isPackageObject: Boolean = hasFlags(PACKAGEOBJECT)
    def isClass: Boolean = hasFlags(CLASS)
    def isTrait: Boolean = hasFlags(TRAIT)
    def isPrivate: Boolean = hasFlags(PRIVATE)
    def isProtected: Boolean = hasFlags(PROTECTED)
    def isAbstract: Boolean = hasFlags(ABSTRACT)
    def isFinal: Boolean = hasFlags(FINAL)
    def isSealed: Boolean = hasFlags(SEALED)
    def isImplicit: Boolean = hasFlags(IMPLICIT)
    def isLazy: Boolean = hasFlags(LAZY)
    def isCase: Boolean = hasFlags(CASE)
    def isCovariant: Boolean = hasFlags(COVARIANT)
    def isContravariant: Boolean = hasFlags(CONTRAVARIANT)
    def isInline: Boolean = hasFlags(INLINE)
  }

  private[scala] type SymFilter = Symbol => Boolean
  private[scala] trait MemberBasedOps[M] {
    protected implicit def m: Mirror
    protected def members(f: SymFilter): List[M]
    protected def members(name: String, f: SymFilter): List[M]
    def members: List[M] = members(sym => true)
    def members(name: String): List[M] = members(name, sym => true)
    def vals: List[M] = members(sym => sym.isVal)
    def vals(name: String): List[M] = members(name, sym => sym.isVal)
    def vars: List[M] = members(sym => sym.isVar)
    def vars(name: String): List[M] = members(name, sym => sym.isVar)
    def defs: List[M] = members(sym => sym.isDef || sym.isMacro)
    def defs(name: String): List[M] = members(name, sym => sym.isDef || sym.isMacro)
    def ctors: List[M] = members(sym => sym.isPrimaryCtor || sym.isSecondaryCtor)
    def types: List[M] = members(sym => sym.isType)
    def types(name: String): List[M] = members(name, sym => sym.isType)
    def params: List[M] = members(sym => sym.isParam)
    def params(name: String): List[M] = members(name, sym => sym.isParam)
    def tparams: List[M] = members(sym => sym.isTypeParam)
    def tparams(name: String): List[M] = members(name, sym => sym.isTypeParam)
    def objects: List[M] = members(sym => sym.isObject || sym.isPackageObject)
    def objects(name: String): List[M] = members(name, sym => sym.isObject || sym.isPackageObject)
    def classes: List[M] = members(sym => sym.isClass)
    def classes(name: String): List[M] = members(name, sym => sym.isClass)
    def traits: List[M] = members(sym => sym.isTrait)
    def traits(name: String): List[M] = members(name, sym => sym.isTrait)
    def packages: List[M] = members(sym => sym.isPackage)
    def packages(name: String): List[M] = members(name, sym => sym.isPackage)
  }
}
