package scala.meta.tests
package prettyprinters

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.io._
import scala.meta.inputs._

@RunWith(classOf[JUnit4])
class PositionSuite {
  @Test
  def range: Unit = {
    val input = Input.File(AbsolutePath("/foo.scala").get)
    val range = Position.Range(input, 40, 2)
    assertEquals("""/foo.scala@40..2""", range.syntax)
    assertEquals("""Position.Range(Input.File(Paths.get("/foo.scala")), 40, 2)""", range.structure)
  }
}