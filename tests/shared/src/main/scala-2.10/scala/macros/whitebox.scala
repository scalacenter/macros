package scala.reflect
package macros

object whitebox {
  type Context = scala.reflect.macros.Context

  implicit class XtensionContext(val c: Context) {
    import c.universe._
    def typecheck(tree0: Any,
                  pt0: Any = null,
                  silent: Boolean = false,
                  withImplicitViewsDisabled: Boolean = false,
                  withMacrosDisabled: Boolean = false): Any = {
      val tree = tree0.asInstanceOf[Tree]
      val pt = if (pt0 != null) pt0.asInstanceOf[Type] else WildcardType
      c.typeCheck(tree, pt, silent, withImplicitViewsDisabled, withMacrosDisabled)
    }
  }
}
