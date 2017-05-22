package scala.meta.tests
package classifiers

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.classifiers._
import scala.meta.tests.helpers._

trait Unclassifiable
class Derived extends Unclassifiable

trait MyToken
object MyToken {
  implicit def tokenIsClassifiable[T <: MyToken]: Classifiable[T] = null
}
class MyIdent extends MyToken

trait Manual
object Manual {
  def unapply(x: MyToken): Boolean = x.isInstanceOf[MyIdent]
  implicit def classifier[T <: MyToken]: Classifier[T, Manual] = {
    new Classifier[T, Manual] {
      def apply(x: T): Boolean = Manual.unapply(x)
    }
  }
}

@RunWith(classOf[JUnit4])
class ClassifierSuite extends TypecheckHelpers {
  @Test
  def unclassifiableInheritance: Unit = {
    assertTypecheckError(
      "don't know how to classify scala.meta.tests.classifiers.Unclassifiable",
      "(??? : Unclassifiable).is[Derived]")
  }

  @Test
  def unclassifiableTypeclass: Unit = {
    assertTypecheckError(
      "don't know how to classify scala.meta.tests.classifiers.Unclassifiable",
      "(??? : Unclassifiable).is[Manual]")
  }

  @Test
  def classifiableInheritance: Unit = {
    assertTypecheckError(
      "don't know how to check whether scala.meta.tests.classifiers.MyToken is scala.meta.tests.classifiers.MyIdent",
      "(??? : MyToken).is[MyIdent]")

    assertTypecheckError(
      "don't know how to check whether scala.meta.tests.classifiers.MyIdent is scala.meta.tests.classifiers.MyIdent",
      "(??? : MyIdent).is[MyIdent]")
  }

  @Test
  def classifiableTypeclass: Unit = {
    val ident1: MyToken = new MyIdent
    assertTrue(ident1.is[Manual])

    val ident2: MyIdent = new MyIdent
    assertTrue(ident2.is[Manual])
  }
}
