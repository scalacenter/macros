package scala.macros.tests
package dialects

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.macros.config._
import scala.macros.Dialect
import scala.macros.dialects._

@RunWith(classOf[JUnit4])
class CurrentSuite {
  @Test
  def scalaVersionMatchesCurrentDialect: Unit = {
    scalaVersion match {
      case Version(2, 10, _, _, _) => assertEquals(Dialect.current, Scala210)
      case Version(2, 11, _, _, _) => assertEquals(Dialect.current, Scala211)
      case Version(2, 12, _, _, _) => assertEquals(Dialect.current, Scala212)
      case Version(2, 13, _, _, _) => assertEquals(Dialect.current, Scala213)
      case Version(0, 1, _, _, _) => assertEquals(Dialect.current, Dotty)
      case other => fail(s"unexpected scalaVersion $other")
    }
  }
}