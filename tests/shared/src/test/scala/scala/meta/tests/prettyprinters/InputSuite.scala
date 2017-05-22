package scala.meta.tests
package prettyprinters

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.io._
import scala.meta.inputs._

@RunWith(classOf[JUnit4])
class InputSuite {
  @Test
  def none: Unit = {
    val input = Input.None
    assertEquals("""<none>""", input.syntax)
    assertEquals("""Input.None""", input.structure)
  }

  @Test
  def string: Unit = {
    val input = Input.String("42")
    assertEquals("""<string>""", input.syntax)
    assertEquals("""Input.String("42")""", input.structure)
  }

  @Test
  def stream: Unit = {
    val input = Input.Stream(null)
    assertEquals("""<stream>""", input.syntax)
    assertEquals("""Input.Stream(<stream>)""", input.structure)
  }

  @Test
  def labeledString: Unit = {
    val input = Input.LabeledString("label", "42")
    assertEquals("""label""", input.syntax)
    assertEquals("""Input.LabeledString("label", "42")""", input.structure)
  }

  @Test
  def file: Unit = {
    val input = Input.File(AbsolutePath("/foo.scala").get)
    assertEquals("""/foo.scala""", input.syntax)
    assertEquals("""Input.File(Paths.get("/foo.scala"))""", input.structure)
  }

  @Test
  def slice: Unit = {
    val input = Input.Slice(Input.String("42"), 1, 1)
    assertEquals("""<slice>""", input.syntax)
    assertEquals("""Input.Slice(Input.String("42"), 1, 1)""", input.structure)
  }
}