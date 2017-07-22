package scala.macros

private[macros] trait Expansions { self: Universe =>
  type Expansion >: Null <: AnyRef

  // NOTE: Can't move this stuff to Abstracts.scala because then scalac produces invalid bytecode:
  // error while loading Abstracts, class file
  // '.../profiles/macros/jvm/target/scala-2.12/classes/scala/macros/Abstracts.class'
  // has location not matching its contents: contains class scala.macros.
  private[macros] def abstracts: ExpansionAbstracts
  private[macros] trait ExpansionAbstracts {
    def expandee(implicit e: Expansion): Term
    def abort(pos: Position, msg: String)(implicit e: Expansion): Nothing
    def error(pos: Position, msg: String)(implicit e: Expansion): Unit
    def warning(pos: Position, msg: String)(implicit e: Expansion): Unit
  }
}
