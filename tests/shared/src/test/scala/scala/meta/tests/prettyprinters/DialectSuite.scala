package scala.meta.tests
package prettyprinters

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.dialects._

@RunWith(classOf[JUnit4])
class DialectSuite {
  @Test
  def standard: Unit = {
    assertEquals("""Scala211""", Scala211.syntax)
    assertEquals("""Scala211""", Scala211.structure)
  }

  @Test
  def nonStandard: Unit = {
    val d = Scala211.copy(allowTrailingCommas = true)
    assertTrue(d.syntax.matches("""^Dialect\(.*\)$"""))
    assertTrue(d.structure.matches("""^Dialect\(.*\)$"""))
  }
}