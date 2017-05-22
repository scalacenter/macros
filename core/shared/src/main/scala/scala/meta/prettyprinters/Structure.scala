package scala.meta
package prettyprinters

import scala.meta.internal.prettyprinters._

@scala.annotation.implicitNotFound("don't know how to prettyprint structure of ${T}")
trait Structure[T] {
  def render(p: Prettyprinter, x: T): Unit
}

object Structure {
  def apply[T](fn: (Prettyprinter, T) => Unit): Structure[T] = {
    new Structure[T] { override def render(p: Prettyprinter, x: T): Unit = fn(p, x) }
  }

  implicit def structureUnit[T <: Unit]: Structure[T] = Structure { (p, x) =>
    p.raw(x.toString)
  }

  implicit def structureBoolean[T <: Boolean]: Structure[T] = Structure { (p, x) =>
    p.raw(x.toString)
  }

  implicit def structureByte[T <: Byte]: Structure[T] = Structure { (p, x) =>
    p.raw(x.toString).raw(".toByte")
  }

  implicit def structureShort[T <: Short]: Structure[T] = Structure { (p, x) =>
    p.raw(x.toString).raw(".toShort")
  }

  implicit def structureChar[T <: Char]: Structure[T] = Structure { (p, x) =>
    p.raw(enquote(x.toString, SingleQuotes))
  }

  implicit def structureInt[T <: Int]: Structure[T] = Structure { (p, x) =>
    p.raw(x.toString)
  }

  implicit def structureFloat[T <: Float]: Structure[T] = Structure { (p, x) =>
    x match {
      case x if x.isNaN => p.raw("Float.NaN")
      case Float.PositiveInfinity => p.raw("Float.PositiveInfinity")
      case Float.NegativeInfinity => p.raw("Float.NegativeInfinity")
      case _ => p.raw(x + "f")
    }
  }

  implicit def structureLong[T <: Long]: Structure[T] = Structure { (p, x) =>
    p.raw(x.toString).raw("L")
  }

  implicit def structureDouble[T <: Double]: Structure[T] = Structure { (p, x) =>
    x match {
      case x if x.isNaN => p.raw("Double.NaN")
      case Double.PositiveInfinity => p.raw("Double.PositiveInfinity")
      case Double.NegativeInfinity => p.raw("Double.NegativeInfinity")
      case _ => p.raw(x + "d")
    }
  }

  implicit def structureString[T <: String]: Structure[T] = Structure { (p, x) =>
    if (x == null) p.raw("null")
    else p.raw(enquote(x, DoubleQuotes))
  }

  implicit def structureSymbol[T <: Symbol]: Structure[T] = Structure { (p, x) =>
    p.raw(x.toString)
  }

  implicit def structureNull: Structure[Null] = Structure { (p, x) =>
    p.raw("null")
  }

  // TODO: This would be a perfect place for generic programming.
  // Unfortunately, we can't use Shapeless here, because bootstrapping.
  private val tupleNames = 1.to(22).map(i => "Tuple" + i).toSet
  implicit def structureProduct[T <: Product]: Structure[T] = Structure { (p, x) =>
    import p._
    def renderPrefix(): Unit = {
      raw(x.productPrefix)
    }
    def renderComponents(xs: List[_]): Unit = {
      def loop(x: Any): Unit = x match {
        case x: Unit => str(x)
        case x: Boolean => str(x)
        case x: Byte => str(x)
        case x: Short => str(x)
        case x: Char => str(x)
        case x: Int => str(x)
        case x: Float => str(x)
        case x: Long => str(x)
        case x: Double => str(x)
        case x: String => str(x)
        case x: Symbol => str(x)
        case null => str(null)
        case x: Prettyprinted => str(x)
        case x: Product => str(x)
        case other => sys.error("don't know how to prettyprint ${x.getClass}")
      }
      raw("(")
      rep(xs, ", ")(loop)
      raw(")")
    }
    x match {
      case Nil => raw("Nil")
      case List(List()) => raw("List(List())")
      case other: List[_] => raw("List"); renderComponents(other)
      case _ =>
        val nonTrivial = !tupleNames(x.productPrefix)
        val nonEmpty = x.productArity > 0 || !x.getClass.getName.endsWith("$")
        if (nonTrivial) renderPrefix()
        if (nonEmpty) renderComponents(x.productIterator.toList)
    }
  }

  implicit def structurePrettyprinted[T <: Prettyprinted]: Structure[T] =
    Structure { (p, x) =>
      Prettyprinted.structure(x, p)
    }

  implicit def structurePretty[T <: Pretty]: Structure[T] =
    structurePrettyprinted[T]
}
