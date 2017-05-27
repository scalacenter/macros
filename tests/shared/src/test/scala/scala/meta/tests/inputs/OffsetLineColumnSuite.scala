package scala.meta.tests
package inputs

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.inputs._
import scala.meta.internal.prettyprinters._

@RunWith(classOf[JUnit4])
class OffsetLineColumnSuite {
  private def test(s: String)(expected: String): Unit = {
    val content = Input.String(s)
    val points = 0.to(content.chars.length).map(i => Position.Range(content, i, i))
    val actual = points.map(p => s"${p.start} ${p.startLine} ${p.startColumn}").mkString(EOL)
    assertEquals(expected, actual)
  }

  @Test
  def empty: Unit = {
    test("")("""
      |0 0 0
    """.trim.stripMargin)
  }

  @Test
  def newline: Unit = {
    test("\n")("""
      |0 0 0
      |1 1 0
    """.trim.stripMargin)
  }

  @Test
  def foo: Unit = {
    test("foo")("""
      |0 0 0
      |1 0 1
      |2 0 2
      |3 0 3
    """.trim.stripMargin)
  }

  @Test
  def fooNewline: Unit = {
    test("foo\n")("""
      |0 0 0
      |1 0 1
      |2 0 2
      |3 0 3
      |4 1 0
    """.trim.stripMargin)
  }

  @Test
  def fooNewlineBar: Unit = {
    test("foo\nbar")("""
      |0 0 0
      |1 0 1
      |2 0 2
      |3 0 3
      |4 1 0
      |5 1 1
      |6 1 2
      |7 1 3
    """.trim.stripMargin)
  }
}