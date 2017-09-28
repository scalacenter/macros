package scala.macros.tests
package scaladays

import scala.macros._

trait Serialize[T] {
  def apply(x: T): String
}

object Serialize {
  def apply[T](fn: T => String) = new Serialize[T] { def apply(x: T) = fn(x) }
  implicit def int: Serialize[Int] = Serialize { x =>
    x.toString
  }
  implicit def string: Serialize[String] = Serialize { x =>
    "\"" + x + "\""
  }

  implicit def materialize[T]: Serialize[T] = macro {
    val instance = Term.fresh("instance")
    val param = Term.fresh("x")
    val buf = Term.fresh("buf")
    val fieldSerialization = {
      val serializerss = T.vals.filter(_.isCase).map { f =>
        val namePart = Lit.String("\"" + f.name.value + "\": ")
        val appendName = q"$buf ++= $namePart"
        val valueRef = q"$param.${Term.Name(f.sym)}"
        val valuePart = q"_root_.scala.Predef.implicitly[Serialize[${f.info}]].apply($valueRef)"
        val appendValue = q"$buf ++= $valuePart"
        List(appendName, appendValue)
      }
      val separators = serializerss.map(_ => q"""$buf ++= ", """")
      serializerss.zip(separators).map({ case (ss, s) => ss :+ s }).flatten.dropRight(1)
    }
    q"""
      implicit object $instance extends Serialize[$T] {
        def apply($param: $T): _root_.java.lang.String = {
          val $buf = new _root_.scala.StringBuilder
          $buf ++= "{ "
          ..$fieldSerialization
          $buf ++= " }"
          $buf.toString
        }
      }
      $instance
    """
  }
}
