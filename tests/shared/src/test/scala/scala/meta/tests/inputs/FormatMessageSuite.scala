package scala.meta.tests
package inputs

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.meta.internal.inputs._
import scala.meta.inputs._
import scala.meta.internal.prettyprinters._

@RunWith(classOf[JUnit4])
class FormatMessageSuite {
  private def test(s: String)(expected: String): Unit = {
    val content = Input.String(s)
    val points = 0.to(content.chars.length).map(i => Position.Range(content, i, i))
    val actual = points.map(p => s"${p.formatMessage("error", "foo")}").mkString(EOL)
    assertEquals(expected, actual)
  }

  @Test
  def empty: Unit = {
    test("")("""
      |<string>:1: error: foo
      |
      |^
    """.trim.stripMargin)
  }

  @Test
  def newline: Unit = {
    test("\n")("""
      |<string>:1: error: foo
      |
      |^
      |<string>:2: error: foo
      |
      |^
    """.trim.stripMargin)
  }

  @Test
  def foo: Unit = {
    test("foo")("""
      |<string>:1: error: foo
      |foo
      |^
      |<string>:1: error: foo
      |foo
      | ^
      |<string>:1: error: foo
      |foo
      |  ^
      |<string>:1: error: foo
      |foo
      |   ^
    """.trim.stripMargin)
  }

  @Test
  def fooNewline: Unit = {
    test("foo\n")("""
      |<string>:1: error: foo
      |foo
      |^
      |<string>:1: error: foo
      |foo
      | ^
      |<string>:1: error: foo
      |foo
      |  ^
      |<string>:1: error: foo
      |foo
      |   ^
      |<string>:2: error: foo
      |
      |^
    """.trim.stripMargin)
  }

  @Test
  def fooNewlineBar: Unit = {
    test("foo\nbar")("""
      |<string>:1: error: foo
      |foo
      |^
      |<string>:1: error: foo
      |foo
      | ^
      |<string>:1: error: foo
      |foo
      |  ^
      |<string>:1: error: foo
      |foo
      |   ^
      |<string>:2: error: foo
      |bar
      |^
      |<string>:2: error: foo
      |bar
      | ^
      |<string>:2: error: foo
      |bar
      |  ^
      |<string>:2: error: foo
      |bar
      |   ^
    """.trim.stripMargin)
  }
}