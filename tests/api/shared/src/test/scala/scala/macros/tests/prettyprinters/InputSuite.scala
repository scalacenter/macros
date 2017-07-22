package scala.macros.tests
package prettyprinters

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.macros.io._
import scala.macros.inputs._

@RunWith(classOf[JUnit4])
class InputSuite {
  @Test
  def none: Unit = {
    val input = Input.None
    assertEquals("""<none>""", input.syntax)
    assertEquals("""Input.None""", input.structure)
  }

  @Test
  def file: Unit = {
    val input = Input.File(AbsolutePath("/foo.scala").get)
    assertEquals("""/foo.scala""", input.syntax)
    assertEquals("""Input.File(Paths.get("/foo.scala"))""", input.structure)
  }

  @Test
  def virtualFile: Unit = {
    val input = Input.VirtualFile("label", "42")
    assertEquals("""label""", input.syntax)
    assertEquals("""Input.VirtualFile("label", "42")""", input.structure)
  }
}