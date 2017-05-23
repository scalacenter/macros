// NOTE: This is a fragment of a manually desugared version of the macro commented out below.
// `plugins/dotc` will figure out the rest and will do this transformation automatically.
//
// import scala.macros._
// class main extends MacroAnnotation {
//   inline def apply(defn: Any): Any = meta {
//     val q"..$mods object $name { ..$stats }" = defn
//     val main = q"def main(arg: Array[String]): Unit = ..$stats"
//     q"..$mods object $name { $main }"
//   }
// }

package scala.macros.tests
package main
package desugared

import scala.macros._

object main$inline {
  def meta(prefix$1: _root_.scala.macros.Stat, defn: _root_.scala.macros.Stat)
          (implicit dialect$1: _root_.scala.macros.Dialect, expansion$1: _root_.scala.macros.Expansion): _root_.scala.macros.Stat = {
    val Defn.Object(mods, name, Template(Nil, Nil, Self(Name.Anonymous(), None), stats)) = defn
    val main = Defn.Def(
      Nil, Term.Name("main"), Nil,
      List(List(Term.Param(Nil, Term.Name("args"), Some(Type.Apply(Type.Name("Array"), List(Type.Name("String")))), None))),
      Some(Type.Name("Unit")),
      Term.Block(stats))
    Defn.Object(mods, name, Template(Nil, Nil, Self(Name.Anonymous(), None), List(main)))
  }
}