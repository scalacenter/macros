package scala.macros.internal
package trees

import scala.macros.internal.prettyprinters._
import scala.macros.Universe

trait TreeStructure { self: Universe =>

  private[macros] implicit def treeStructure[T <: Tree]: Structure[T] = new Structure[T] {
    // TODO: implement this
    override def render(p: Prettyprinter, x: T): Unit = x match {
      case Term.This(qual) =>
        p.raw("Term.This(")
        render(p, qual.asInstanceOf[T])
        p.raw(")")
      case Term.Apply(fun, args) =>
        p.raw("Term.Apply(")
        render(p, fun.asInstanceOf[T])
        p.raw(", ")
        args match {
          case Nil =>
            p.raw("Nil")
          case head :: tail =>
            p.raw("List(")
            render(p, head.asInstanceOf[T])
            tail.foreach { arg =>
              p.raw(", ")
              render(p, arg.asInstanceOf[T])
            }
            p.raw(")")
        }
        p.raw(")")
      case Term.Select(qual, name) =>
        p.raw("Term.Select(")
        render(p, qual.asInstanceOf[T])
        p.raw(", ")
        render(p, name.asInstanceOf[T])
        p.raw(")")
      case Name.Indeterminate(value) =>
        p.raw("Name(\"")
        p.raw(value)
        p.raw("\")")
      case Term.Name(value) =>
        p.raw("Term.Name(\"")
        p.raw(value)
        p.raw("\")")
      case Type.Name(value) =>
        p.raw("Type.Name(\"")
        p.raw(value)
        p.raw("\")")
      case Lit.Char(_) =>
        p.raw("Lit.Char(")
        p.raw(x.syntax)
        p.raw(")")
      case Lit.Double(_) =>
        p.raw("Lit.Double(")
        p.raw(x.syntax)
        p.raw(")")
      case Lit.Float(_) =>
        p.raw("Lit.Float(")
        p.raw(x.syntax)
        p.raw(")")
      case Lit.Int(_) =>
        p.raw("Lit.Int(")
        p.raw(x.syntax)
        p.raw(")")
      case Lit.Long(_) =>
        p.raw("Lit.Long(")
        p.raw(x.syntax)
        p.raw(")")
      case Lit.Null() => p.raw("Lit.Null()")
      case Lit.String(_) =>
        p.raw("Lit.String(")
        p.raw(x.syntax)
        p.raw(")")
      case Lit.Symbol(_) =>
        p.raw("Lit.Symbol(")
        p.raw(x.syntax)
        p.raw(")")
      case _ =>
        pprint.log(x)
        ???
    }
  }
}
