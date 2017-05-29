package scala.macros.internal
package engines.scalac
package semantic

trait Semantic extends scala.macros.semantic.Semantic with Abstracts { self: Universe =>
  type Symbol = g.Symbol
  case class Denotation(pre: Type, symbol: Symbol)
}