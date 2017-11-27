package scala.macros.tests

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

  @macros.socrates
  def materialize[T]: Serialize[T] = macro impl
  def impl(c: Expansion)(T: Type): Term = {
    val instance = Term.fresh("instance")
    val param = Term.fresh("x")
    val buf = Term.fresh("buf")
    val root =
      Term
        .Name("_root_")
        .select("scala" :: "macros" :: "tests" :: Nil)
    val serializeTpe = Type.typeRef("scala.macros.tests.Serialize")
    val stringBuilderTpe = Type.typeRef("scala.collection.mutable.StringBuilder").toTypeTree
    TypeTree.Select(Term.Name("_root_").select("scala"), "StringBuilder")
    val append = "append"

    val fieldSerialization: List[Stat] = {
      val serializerss = T.caseFields.map { f =>
        val namePart = Lit.String("\"" + f.name + "\": ")
        val appendName = Term.Apply(Term.Select(Term.Name(buf), append), namePart :: Nil)
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
        val appendValue = Term.Name(buf).select(append).apply(valuePart :: Nil)
        List(appendName, appendValue)
      }
      val separators = serializerss.map(
        _ => Term.Apply(Term.Select(Term.Name(buf), append), Lit.String(", ") :: Nil)
      )
      serializerss.zip(separators).flatMap { case (ss, s) => ss :+ s }.dropRight(1)
    }
    var stats = List.newBuilder[Stat]
    stats += Defn.Val(
      Nil,
      buf,
      None,
      Term.New(
        Init(
          stringBuilderTpe,
          Nil
        )
      )
    )
    val x = new StringBuilder
    x ++= "a"
    stats += Term.Apply(Term.Select(Term.Name(buf), append), List(Lit.String("{ ")))
    stats ++= fieldSerialization
    stats += Term.Apply(Term.Select(Term.Name(buf), append), List(Lit.String(" }")))
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
    val result = Term.Block(
      defnObject ::
        Term.Name(instance) ::
        Nil
    )
    result
  }
}
