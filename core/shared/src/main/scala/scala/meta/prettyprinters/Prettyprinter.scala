package scala.meta
package prettyprinters

// NOTE: This class is an evolution of Show from 1.x
// and is heavily inspired by ShowBuilder from scala-native/scala-native.

final class Prettyprinter {
  private val buf = new java.lang.StringBuilder

  def raw(value: String): Prettyprinter = {
    buf.append(value)
    this
  }

  def stx[T: Syntax](value: T): Prettyprinter = {
    implicitly[Syntax[T]].render(this, value)
    this
  }

  def str[T: Structure](value: T): Prettyprinter = {
    implicitly[Structure[T]].render(this, value)
    this
  }

  def rep[T](xs: List[T], sep: String = "")(fn: T => Unit): Prettyprinter = {
    if (xs.nonEmpty) {
      xs.init.foreach { x =>
        fn(x)
        raw(sep)
      }
      fn(xs.last)
    }
    this
  }

  override def toString: String = buf.toString
}
