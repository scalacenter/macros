package scala.macros.tests.scaladays

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(classOf[JUnit4])
class SyntaxSuite {
  @Test def syntaxChar: Unit = assertEquals("'2'", TestMacros.syntax('2'))
  @Test def syntaxDouble: Unit = assertEquals("2.0d", TestMacros.syntax(2d))
  @Test def syntaxFloat: Unit = assertEquals("2.0f", TestMacros.syntax(2f))
  @Test def syntaxInt: Unit = assertEquals("2", TestMacros.syntax(2))
  @Test def syntaxLong: Unit = assertEquals("2L", TestMacros.syntax(2L))
  @Test def syntaxNull: Unit = assertEquals("null", TestMacros.syntax(null))
  @Test def syntaxString: Unit = assertEquals("\"2\"", TestMacros.syntax("2"))
  @Test def syntaxSymbol: Unit = assertEquals("'S", TestMacros.syntax('S))

  // special cases
  @Test def syntaxDoubleNaN: Unit =
    assertEquals("Double.NaN", TestMacros.syntax(Double.NaN))
  @Test def syntaxDoublePosInf: Unit =
    assertEquals("Double.PositiveInfinity", TestMacros.syntax(Double.PositiveInfinity))
  @Test def syntaxDoubleNegInf: Unit =
    assertEquals("Double.NegativeInfinity", TestMacros.syntax(Double.NegativeInfinity))
  @Test def syntaxFloatNaN: Unit =
    assertEquals("Float.NaN", TestMacros.syntax(Float.NaN))
  @Test def syntaxFloatPosInf: Unit =
    assertEquals("Float.PositiveInfinity", TestMacros.syntax(Float.PositiveInfinity))
  @Test def syntaxFloatNegInf: Unit =
    assertEquals("Float.NegativeInfinity", TestMacros.syntax(Float.NegativeInfinity))
}
