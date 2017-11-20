package scala.macros.tests

import org.junit._
import org.junit.runner._
import org.junit.runners._
import org.junit.Assert._

@RunWith(classOf[JUnit4])
class SerializeSuite {
  @Test
  def simple: Unit = {
    case class C(x: Int, y: String)
    assertEquals("""{ "x": 40, "y": "2" }""", Serialize.materialize[C](C(40, "2")))
  }
}
