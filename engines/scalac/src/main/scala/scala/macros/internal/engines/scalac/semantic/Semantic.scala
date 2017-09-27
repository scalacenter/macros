package scala.macros.internal
package engines.scalac
package semantic

trait Semantic extends scala.macros.semantic.Semantic with Abstracts { self: Universe =>
  type Symbol = g.Symbol

  case class Denotation(pre: g.Type, sym: g.Symbol) {
    final override def toString = s"$sym in $pre"
  }
}
