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
}