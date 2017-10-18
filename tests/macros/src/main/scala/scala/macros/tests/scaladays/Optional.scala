package scala.macros.tests.scaladays

import scala.macros._

// NOTE(olafur): the `extends AnyVal` will cause Optional.map tocrash in scalac
// [error]   last tree to typer: TypeTree
//[error]        tree position: line 18 of /tests/macros/src/test/scala/scala/macros/tests/scaladays/OptionalSuite.scala
//[error]             tree tpe: ErasedValueType(class Optional, String)
//[error]               symbol: <none>
//[error]    symbol definition: <none> (a NoSymbol)
//[error]       symbol package: <none>
//[error]        symbol owners:
//[error]            call site: method $anonfun$t1 in package scaladays
//[error]
final case class Optional[+A >: Null](value: A) {
  def get: A = value
  def isEmpty: Boolean = value == null
  def getOrElse(alt: => A): A = macro {
    val tempValDef = tpd.ValDef(this)
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
    val tempValDef = tpd.ValDef(this)
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
