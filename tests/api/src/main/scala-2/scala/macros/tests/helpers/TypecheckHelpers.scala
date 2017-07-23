package scala.macros.tests
package helpers

import org.junit.Assert._
import scala.language.experimental.macros
import scala.reflect.macros.ParseException
import scala.reflect.macros.TypecheckException
import scala.reflect.macros.whitebox._
import scala.reflect.internal.util.Position

trait Options
trait LowPriorityOptions {
  implicit object WithPositions extends Options
}
object Options extends LowPriorityOptions {
  implicit object WithoutPositions extends Options
}

trait TypecheckHelpers {
  // Typechecks the enclosed code at compile time
  // and expands into a string literal that contains an error message.
  // Returns an empty string if there's no typecheck error.
  //
  // The options parameter determines whether to print just the error message
  // or also the original code with a familiar-looking caret pointing to the error.
  // Here's an example from the quasiquote suite:
  //
  //   assert(typecheckError("""
  //     import scala.macros._
  //     val q"type $name[$X] = $Y" = q"type List[+A] = List[A]"
  //   """) === """
  //     |<macro>:3: not found: value X
  //     |      val q"type $name[$X] = $Y" = q"type List[+A] = List[A]"
  //     |                        ^
  //   """.trim.stripMargin)
  def assertTypecheckError(expected: String, code: String)(implicit options: Options): Unit =
    macro TypecheckHelpers.impl
}

object TypecheckHelpers {
  def impl(c: Context)(expected: c.Expr[String], code: c.Expr[String])(
      options: c.Expr[Options]): c.Expr[Unit] = {
    import c.universe.{Position => _, _}
    val s_code = code match {
      case Expr(Literal(Constant(s_code: String))) => s_code
      case _ => c.abort(c.enclosingPosition, "this macro only works with literal code strings")
    }
    val tree = {
      try c.parse(s_code.replace("QQQ", "\"\"\""))
      catch { case ex: ParseException => c.abort(c.enclosingPosition, "this code fails to parse") }
    }
    def format(ex: TypecheckException): String = {
      val optionsSym = options.tree.tpe.typeSymbol
      if (optionsSym == typeOf[Options.WithoutPositions.type].typeSymbol) {
        ex.msg
      } else if (optionsSym == typeOf[Options.WithPositions.type].typeSymbol) {
        Position.formatMessage(ex.pos.asInstanceOf[Position], ex.msg, shortenFile = true)
      } else {
        c.abort(c.enclosingPosition, s"unsupported option: $optionsSym")
      }
    }
    try {
      c.typecheck(tree, silent = false)
      reify(assertEquals(expected.splice, ""))
    } catch {
      case ex: TypecheckException =>
        val message = c.Expr[String](Literal(Constant(format(ex))))
        reify(assertEquals(expected.splice, message.splice))
    }
  }
}
