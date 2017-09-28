package scala.macros.tests.scaladays

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(classOf[JUnit4])
class StructureSuite {
  @Test def structureChar: Unit = assertEquals("Lit.Char('2')", TestMacros.structure('2'))
  @Test def structureDouble: Unit = assertEquals("Lit.Double(2.0d)", TestMacros.structure(2d))
  @Test def structureFloat: Unit = assertEquals("Lit.Float(2.0f)", TestMacros.structure(2f))
  @Test def structureInt: Unit = assertEquals("Lit.Int(2)", TestMacros.structure(2))
  @Test def structureLong: Unit = assertEquals("Lit.Long(2L)", TestMacros.structure(2L))
  @Test def structureNull: Unit = assertEquals("Lit.Null()", TestMacros.structure(null))
  @Test def structureString: Unit = assertEquals("Lit.String(\"2\")", TestMacros.structure("2"))
  @Test def structureSymbol: Unit = assertEquals("Lit.Symbol('S)", TestMacros.structure('S))
}
