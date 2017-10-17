package scala.macros.tests.scaladays

import scala.macros._

object Assert {
  def assert(cond: Boolean): Unit = macro {
    val root = Term.Name("_root_")
      .select("org")
      .select("junit")
      .select("Assert")

    cond match {
      case tpd.Apply(tpd.Select(qual, sym), arg :: Nil) if sym.name == "==" =>
        root.select("assertEquals").apply(qual.splice :: arg.splice :: Nil)
      case _ =>
        root.select("assertTrue").apply(cond.splice :: Nil)
    }
  }
}
