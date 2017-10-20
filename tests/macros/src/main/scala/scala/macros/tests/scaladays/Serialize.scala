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

  def materialize[T]: Serialize[T] = macro {
    val instance = Term.fresh("instance")
    val param = Term.fresh("x")
    val buf = Term.fresh("buf")

    val fieldSerialization: List[Stat] = {
      val serializerss = T.caseFields.map { f =>
        val namePart = Lit.String("\"" + f.name.value + "\": ")
        val appendName = Term.Apply(Term.Select(buf, Term.Name("++=")), namePart :: Nil)
        val valueRef = Term.Select(param, Term.Name(f.sym))
        val valuePart =
          Term
            .Name("_root_")
            .select("scala")
            .select("Predef")
            .select("implicitly")
            .applyType(Type.Apply(Type.Name("Serialize"), List(f.info)) :: Nil)
            .select("apply")
            .apply(valueRef :: Nil)
        val appendValue = buf.select("++=").apply(valuePart :: Nil)
        List(appendName, appendValue)
      }
      val separators = serializerss.map(
        _ => Term.Apply(Term.Select(buf, Term.Name("++=")), Lit.String(", ") :: Nil)
      )
      serializerss.zip(separators).map({ case (ss, s) => ss :+ s }).flatten.dropRight(1)
    }
    var stats = List.newBuilder[Stat]
    stats += Defn.Val(
      Nil,
      buf,
      None,
      Term.New(
        Init(
          Type.Select(
            Term.Name("_root_").select("scala"),
            Type.Name("StringBuilder")
          ),
          Nil
        )
      )
    )
    stats += Term.Apply(Term.Select(buf, Term.Name("++=")), List(Lit.String("{ ")))
    stats ++= fieldSerialization
    stats += Term.Apply(Term.Select(buf, Term.Name("++=")), List(Lit.String(" }")))
    stats += Term.Select(buf, Term.Name("toString"))
    val defnObject: Stat = Defn.Object(
      List(),
      instance,
      List(Init(Type.Apply(Type.Name("Serialize"), List(T)),  Nil)),
      Self(Name(""), None),
      List(
        Defn.Def(
          Nil,
          Term.Name("apply"),
          Nil,
          List(List(Term.Param.apply(Nil, param, Some(T), None))),
          Some(
            Type.Select(
              Term.Name("_root_").select("java").select("lang"),
              Type.Name("String")
            )
          ),
          Term.Block(stats.result())
        )
      )
    )
    Term.Block(
      defnObject ::
        instance ::
        Nil
    )
  }
}
