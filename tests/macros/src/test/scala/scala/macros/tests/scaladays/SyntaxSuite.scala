package scala.macros.tests.scaladays

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

// NOTE(olafur): these are temporarily commented out because
// we want to get the SerializeSuite tests merged first.
/*
@RunWith(classOf[JUnit4])
class SyntaxSuite {
  import TestMacros.syntax

  @Test def char(): Unit = assertEquals("'2'", syntax('2'))
  @Test def double(): Unit = assertEquals("2.0d", syntax(2d))
  @Test def float(): Unit = assertEquals("2.0f", syntax(2f))
  @Test def int(): Unit = assertEquals("2", syntax(2))
  @Test def long(): Unit = assertEquals("2L", syntax(2L))
  @Test def `null`(): Unit = assertEquals("null", syntax(null))
  @Test def string(): Unit = assertEquals("\"2\"", syntax("2"))
  @Test def symbol(): Unit = assertEquals("'S", syntax('S))

  // special cases
  @Test def doubleNaN(): Unit =
    assertEquals("Double.NaN", syntax(Double.NaN))
  @Test def doublePosInf(): Unit =
    assertEquals("Double.PositiveInfinity", syntax(Double.PositiveInfinity))
  @Test def doubleNegInf(): Unit =
    assertEquals("Double.NegativeInfinity", syntax(Double.NegativeInfinity))
  @Test def floatNaN(): Unit =
    assertEquals("Float.NaN", syntax(Float.NaN))
  @Test def floatPosInf(): Unit =
    assertEquals("Float.PositiveInfinity", syntax(Float.PositiveInfinity))
  @Test def floatNegInf(): Unit =
    assertEquals("Float.NegativeInfinity", syntax(Float.NegativeInfinity))

  val x = "a"
  @Test def name(): Unit = assertEquals("SyntaxSuite.this.x", syntax(x))

  @Test def apply0(): Unit =
    assertEquals("\"a\".trim()", syntax("a".trim))
  @Test def apply1(): Unit =
    assertEquals("\"a\".charAt(0)", syntax("a".charAt(0)))
  @Test def apply2(): Unit =
    assertEquals("\"a\".substring(0, 1)", syntax("a".substring(0, 1)))
  @Test def applySymbol(): Unit =
    assertEquals("scala.Symbol.apply(\"S\")", syntax(scala.Symbol.apply("S")))

  def add(a: Int)(b: Int): Int = a + b
  @Test def applyCurry(): Unit =
    assertEquals("SyntaxSuite.this.add(1)(2)", syntax(add(1)(2)))

  def tpe[T](e: T) = e
  @Test def typeApply(): Unit =
    assertEquals("SyntaxSuite.this.tpe[Int](2)", syntax(tpe(2)))
  val lst = List(1)
  @Test def typeApplyHK(): Unit = {
    assertEquals("SyntaxSuite.this.tpe[List[Int]](SyntaxSuite.this.lst)", syntax(tpe(lst)))
  }

}
 */
