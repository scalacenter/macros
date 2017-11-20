package scala.macros.core

import java.nio.file.Path

trait Universe extends UntypedTrees with TypedTrees {

  def fresh(prefix: String): String

  type Symbol
  def root: Symbol // _root_ package symbol
  def symName(sym: Symbol): String
  def symOwner(sym: Symbol): Option[Symbol]

  type Denotation
  def denotInfo(denot: Denotation): Type
  def denotSym(denot: Denotation): Symbol

  type Type
  def caseFields(tpe: Type): List[Denotation]
  def typeRef(path: String): Type
  def appliedType(tp: Type, args: List[Type]): Type
  def typeTreeOf(tp: Type): TypeTree

  def enclosingPosition: Position
  def enclosingOwner: Symbol

  // For compilers
  type Mirror
  type Expansion
}

trait Input {
  def path: Path
}

trait Position {
  def line: Int
  def input: Input
}
