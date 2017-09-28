package scala.macros.tests
package scaladays

import scala.macros._
import scala.language.experimental.macros

class main extends MacroAnnotation {
  def apply(defn: Any): Any = macro {
    val q"..$mods object $name extends ..$inits { $self => ..$stats }" = defn
    val main = q"def main(args: Array[String]): Unit = { ..$stats }"
    q"..$mods object $name extends ..$inits { $self => $main }"
  }
}
