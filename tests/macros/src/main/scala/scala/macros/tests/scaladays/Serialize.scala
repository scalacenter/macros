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
    val serializeTpe = Type.typeRef("scala.macros.tests.scaladays.Serialize")

    val fieldSerialization: List[Stat] = {
      val serializerss = T.caseFields.map { f =>
        val namePart = Lit.String("\"" + f.name + "\": ")
        val appendName = Term.Apply(Term.Select(Term.Name(buf), "++="), namePart :: Nil)
        val valueRef = Term.Select(Term.Name(param), f.name)
        val valuePart =
          Term
            .Name("_root_")
            .select("scala")
            .select("Predef")
            .select("implicitly")
            .applyType(serializeTpe.appliedTo(f.info :: Nil).toTypeTree :: Nil)
            .select("apply")
            .apply(valueRef :: Nil)
        val appendValue = Term.Name(buf).select("++=").apply(valuePart :: Nil)
        List(appendName, appendValue)
      }
      val separators = serializerss.map(
        _ => Term.Apply(Term.Select(Term.Name(buf), "++="), Lit.String(", ") :: Nil)
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
          Type.typeRef("scala.StringBuilder").toTypeTree,
          Nil
        )
      )
    )
    stats += Term.Apply(Term.Select(Term.Name(buf), "++="), List(Lit.String("{ ")))
    stats ++= fieldSerialization
    stats += Term.Apply(Term.Select(Term.Name(buf), "++="), List(Lit.String(" }")))
    stats += Term.Select(Term.Name(buf), "toString")
    val defnObject: Stat = Defn.Object(
      List(),
      instance,
      Template(
        List(Init(serializeTpe.appliedTo(T :: Nil).toTypeTree, Nil)),
        Self("", None),
        List(
          Defn.Def(
            Nil,
            "apply",
            Nil,
            List(List(Term.Param.apply(Nil, param, Some(T.toTypeTree), None))),
            Some(TypeTree.Name("String")),
            Term.Block(stats.result())
          )
        )
      )
    )
    Term.Block(
      defnObject ::
        Term.Name(instance) ::
        Nil
    )
  }
}
