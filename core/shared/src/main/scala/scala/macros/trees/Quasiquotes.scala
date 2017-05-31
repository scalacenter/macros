package scala.macros
package trees

import scala.macros.internal.annotation.compileTimeOnly
import scala.macros.internal.trees.Errors

private[macros] trait Quasiquotes { self: Universe =>
  // NOTE: These are just stubs that must be taken care of by engines.
  implicit class XtensionQuasiquotes(val sc: StringContext) {
    trait InterpolatorSignature {
      @compileTimeOnly(Errors.QuasiquotesRequireCompilerSupport)
      def apply[T >: Any](args: T*): Any = ???
      @compileTimeOnly(Errors.QuasiquotesRequireCompilerSupport)
      def unapply(scrutinee: Any): Any = ???
    }
    object q extends InterpolatorSignature
    object param extends InterpolatorSignature
    object t extends InterpolatorSignature
    object tparam extends InterpolatorSignature
    object p extends InterpolatorSignature
    object init extends InterpolatorSignature
    object self extends InterpolatorSignature
    object template extends InterpolatorSignature
    object mod extends InterpolatorSignature
    object enumerator extends InterpolatorSignature
    object importer extends InterpolatorSignature
    object importee extends InterpolatorSignature
    object source extends InterpolatorSignature
  }
}
