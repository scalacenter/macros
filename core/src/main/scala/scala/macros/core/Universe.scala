package scala.macros.core

import java.nio.file.Path

trait Universe extends UntypedTrees with TypedTrees {

  def fresh(prefix: String): String

 // =========
  // Semantic
  // =========
  type Mirror

  type Symbol
  def root: Symbol                     // root package symbol
  def symName(sym: Symbol): String
  def symOwner(sym: Symbol): Option[Symbol]

  type Denotation
  def denotInfo(denot: Denotation): Type
  def denotSym(denot: Denotation): Symbol

  type Type
  def caseFields(tpe: Type): List[Denotation]
  def typeRef(path: String): Type
  def appliedType(tp: Type, args: List[Type]): Type

  // =========
  // Expansion
  // =========
  type Expansion
  def enclosingPosition: Position
  def enclosingOwner: Symbol
}

trait Input extends Any {
  def path: Path
}

trait Position extends Any {
  def line: Int
  def input: Input
}
