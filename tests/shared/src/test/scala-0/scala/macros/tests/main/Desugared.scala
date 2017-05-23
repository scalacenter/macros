package scala.macros.tests
package main
package desugared

import scala.macros._

object main$inline {
  def meta(this$n: Stat, defn: Stat)(implicit dialect: Dialect, expansion: Expansion): Stat = {
    val Defn.Object(mods, name, Template(Nil, Nil, Self(Name.Anonymous(), None), stats)) = defn
    val main = Defn.Def(
      Nil, Term.Name("main"), Nil,
      List(List(Term.Param(Nil, Term.Name("args"), Some(Type.Apply(Type.Name("Array"), List(Type.Name("String")))), None))),
      Some(Type.Name("Unit")),
      Term.Block(stats))
    Defn.Object(mods, name, Template(Nil, Nil, Self(Name.Anonymous(), None), List(main)))
  }
}