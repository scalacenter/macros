package scala.meta
package prettyprinters

@scala.annotation.implicitNotFound("don't know how to prettyprint syntax of ${T}")
trait Syntax[T] {
  def render(p: Prettyprinter, x: T): Unit
}

object Syntax {
  def apply[T](fn: (Prettyprinter, T) => Unit): Syntax[T] = {
    new Syntax[T] { override def render(p: Prettyprinter, x: T): Unit = fn(p, x) }
  }

  implicit def syntaxUnit[T <: Unit]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxBoolean[T <: Boolean]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxByte[T <: Byte]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxShort[T <: Short]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxChar[T <: Char]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxInt[T <: Int]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxFloat[T <: Float]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxLong[T <: Long]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxDouble[T <: Double]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxString[T <: String]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  implicit def syntaxSymbol[T <: scala.Symbol]: Syntax[T] = Syntax{ (p, x) => p.raw(x.toString) }

  // NOTE: We intentionally don't provide an instance for Null.
}