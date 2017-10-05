package scala.macros.tests.scaladays

import scala.macros._

object Assert {
  def assert(cond: Boolean): Unit = macro {
    val root = Term
      .Name("_root_")
      .select("org")
      .select("junit")
      .select("Assert")

    cond match {
      // NOTE(olafur) we should not do stringly based pattern matching here.
      // Ideally, we should be using the semantic API for this.
      case Term.Apply(Term.Select(qual, Term.Name("==")), arg :: Nil) =>
        root.select("assertEquals").apply(qual :: arg :: Nil)
      case _ =>
        root.select("assertTrue").apply(cond :: Nil)
    }
  }
}
