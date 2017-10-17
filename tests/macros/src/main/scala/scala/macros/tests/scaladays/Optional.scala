package scala.macros.tests.scaladays

import scala.macros._

final class Optional[+A >: Null](val value: A) extends AnyVal {
  def get: A = value
  def isEmpty = value == null

  def getOrElse[B >: A](alt: => B): B = macro {
    val tempValDef = tpd.ValDef(prefix)
    val tempIdent = tpd.ref(tempValDef.symbol)
    val ifTerm = tpd.If(
      tempIdent.select("isEmpty"),
      alt,
      tempIdent.select("value")
    )

    Term.Block(tempValDef.splice :: ifTerm.splice :: Nil)
  }

  def map[B >: Null](f: A => B): Optional[B] = macro {
    val tpd.Function(param :: Nil, body) = f
    val tempValDef = tpd.ValDef(prefix)
    val tempIdent = tpd.ref(tempValDef.symbol)

    val newBody = body.transform {
      case tpd.Name(denot) if denot.sym eq param =>
        tempIdent.select("value")
    }

    val ifTerm = Term.If(
      tempIdent.select("isEmpty").splice,
      Term.New(Init(TypeTree.Name("Optional"), (Lit.Null :: Nil) :: Nil)),
      Term.New(Init(TypeTree.Name("Optional"), (newBody.splice :: Nil) :: Nil))
    )

    Term.Block(tempValDef.splice :: ifTerm :: Nil)
  }

  override def toString = if (isEmpty) "<empty>" else s"$value"
}
