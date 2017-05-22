package scala.meta.tests
package prettyprinters

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.prettyprinters._
import scala.meta.tests.helpers._

@RunWith(classOf[JUnit4])
class StandardSuite extends TypecheckHelpers {
  @Test
  def prettyUnit: Unit = {
    assertEquals("()", ().syntax)
    assertEquals("()", ().structure)
  }

  @Test
  def prettyBoolean: Unit = {
    assertEquals("true", true.syntax)
    assertEquals("true", true.structure)
    assertEquals("false", false.syntax)
    assertEquals("false", false.structure)
  }

  @Test
  def prettyByte: Unit = {
    assertEquals("42", 42.toByte.syntax)
    assertEquals("42.toByte", 42.toByte.structure)
  }

  @Test
  def prettyShort: Unit = {
    assertEquals("42", 42.toShort.syntax)
    assertEquals("42.toShort", 42.toShort.structure)
  }

  @Test
  def prettyChar: Unit = {
    assertEquals("c", 'c'.syntax)
    assertEquals("'c'", 'c'.structure)
    assertEquals("\n", '\n'.syntax)
    assertEquals("'\\n'", '\n'.structure)
  }

  @Test
  def prettyInt: Unit = {
    assertEquals("42", 42.syntax)
    assertEquals("42", 42.structure)
  }

  @Test
  def prettyFloat: Unit = {
    assertEquals("42.0", 42f.syntax)
    assertEquals("42.0f", 42f.structure)
  }

  @Test
  def prettyLong: Unit = {
    assertEquals("42", 42L.syntax)
    assertEquals("42L", 42L.structure)
  }

  @Test
  def prettyDouble: Unit = {
    assertEquals("42.0", 42d.syntax)
    assertEquals("42.0d", 42d.structure)
  }

  @Test
  def prettyString: Unit = {
    assertEquals("s", "s".syntax)
    assertEquals("\"s\"", "s".structure)
    assertEquals("\n", "\n".syntax)
    assertEquals("\"\\n\"", "\n".structure)
  }

  @Test
  def prettySymbol: Unit = {
    assertEquals("'x", 'x.syntax)
    assertEquals("'x", 'x.structure)
  }

  @Test
  def prettyNull: Unit = {
    // TODO: It would seem you can't add this kind of extension methods to Null.
    // It's not critical for me that this works, so I'm moving on.
    assertTypecheckError("value syntax is not a member of Null", "null.syntax")
    assertTypecheckError("value structure is not a member of Null", "null.structure")
  }

  @Test
  def prettyOption: Unit = {
    assertTypecheckError("don't know how to prettyprint syntax of None.type", "None.syntax")
    assertEquals("None", None.structure)
    assertTypecheckError("don't know how to prettyprint syntax of Some[Int]", "Some(1).syntax")
    assertEquals("Some(1)", Some(1).structure)
  }

  @Test
  def prettyList: Unit = {
    assertTypecheckError(
      "don't know how to prettyprint syntax of scala.collection.immutable.Nil.type",
      "Nil.syntax")
    assertEquals("Nil", Nil.structure)
    assertTypecheckError("don't know how to prettyprint syntax of List[Int]", "List(1).syntax")
    assertEquals("List(1)", List(1).structure)
    assertTypecheckError("don't know how to prettyprint syntax of List[Int]", "List(1, 2).syntax")
    assertEquals("List(1, 2)", List(1, 2).structure)
  }

  @Test
  def prettyTuple: Unit = {
    assertTypecheckError("don't know how to prettyprint syntax of (Int, Int)", "(1, 2).syntax")
    assertEquals("(1, 2)", (1, 2).structure)
  }
}
