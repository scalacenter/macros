package scala.macros
package trees

import scala.annotation.compileTimeOnly
import scala.macros.internal.trees.Errors

private[scala] trait Quasiquotes { self: Universe =>
  // NOTE: These are just stubs that must be taken care of by engines.
  implicit class XtensionQuasiquotes(val sc: StringContext) {
    trait QuasiquoteSignature {
      @compileTimeOnly(Errors.QuasiquotesRequireCompilerSupport)
      def apply(args: Any*): Tree = ???
      @compileTimeOnly(Errors.QuasiquotesRequireCompilerSupport)
      def unapply(tree: Tree): Any = ???
    }
    object q extends QuasiquoteSignature
    object param extends QuasiquoteSignature
    object t extends QuasiquoteSignature
    object tparam extends QuasiquoteSignature
    object p extends QuasiquoteSignature
    object init extends QuasiquoteSignature
    object self extends QuasiquoteSignature
    object template extends QuasiquoteSignature
    object mod extends QuasiquoteSignature
    object enumerator extends QuasiquoteSignature
    object importer extends QuasiquoteSignature
    object importee extends QuasiquoteSignature
    object source extends QuasiquoteSignature
  }
}
