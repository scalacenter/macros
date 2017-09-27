package scala.macros.tests
package scaladays

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._
import scala.macros.config._

@RunWith(classOf[JUnit4])
class SerializeSuite {
  @Test
  def simple: Unit = {
    case class C(x: Int, y: String)
    def serialize[T: Serialize](x: T): String = implicitly[Serialize[T]].apply(x)
    assertEquals("""{ "x": 40, "y": "2" }""", serialize(C(40, "2")))
  }
}
