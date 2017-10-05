package scala.macros.tests.scaladays

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

// NOTE(olafur): these are temporarily commented out because
// we want to get the SerializeSuite tests merged first.
/*
@RunWith(classOf[JUnit4])
class StructureSuite {
  import TestMacros.structure

  @Test def char(): Unit = assertEquals("Lit.Char('2')", structure('2'))
  @Test def double(): Unit = assertEquals("Lit.Double(2.0d)", structure(2d))
  @Test def float(): Unit = assertEquals("Lit.Float(2.0f)", structure(2f))
  @Test def int(): Unit = assertEquals("Lit.Int(2)", structure(2))
  @Test def long(): Unit = assertEquals("Lit.Long(2L)", structure(2L))
  @Test def `null`(): Unit = assertEquals("Lit.Null()", structure(null))
  @Test def string(): Unit = assertEquals("Lit.String(\"2\")", structure("2"))
  @Test def symbol(): Unit = assertEquals("Lit.Symbol('S)", structure('S))

  val x = "a"
  @Test def name(): Unit =
    assertEquals(
      """Term.Select(Term.This(Name("StructureSuite")), Term.Name("x"))""",
      structure(x)
    )

  @Test def apply0(): Unit =
    assertEquals(
      """Term.Apply(Term.Select(Lit.String("a"), Term.Name("trim")), Nil)""",
      structure("a".trim)
    )
  @Test def apply1(): Unit =
    assertEquals(
      """Term.Apply(Term.Select(Lit.String("a"), Term.Name("charAt")), List(Lit.Int(0)))""",
      structure("a".charAt(0))
    )
  @Test def apply2(): Unit =
    assertEquals(
      "Term.Apply(Term.Select(Lit.String(\"a\"), Term.Name(\"substring\")), List(Lit.Int(0), Lit.Int(1)))",
      structure("a".substring(0, 1))
    )

}
 */
